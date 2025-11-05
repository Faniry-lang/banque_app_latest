package itu.banque.change.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TauxVariation;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.api.remote.TauxVariationServiceRemote;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.change.DeviseFileManager;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;

@Stateless
public class DeviseService implements DeviseServiceRemote {

    private List<Devise> devises;
    private final DeviseFileManager fileManager = new DeviseFileManager();

    @PostConstruct
    public void init() {
        try {
            devises = fileManager.lireDevises();
        } catch (IOException e) {
            e.printStackTrace();
            devises = new ArrayList<>();
        }
    }

    @Override
    public List<Devise> getAll() {
        return this.devises;
    }

    @Override
    public Devise getByNomEtDate(String nom, LocalDate date) {
        for (Devise devise : this.devises) {
            if (devise.getNom().equalsIgnoreCase(nom)) {
                boolean isAfterOrEqualDebut = !date.isBefore(devise.getDateDebut());
                boolean isBeforeOrEqualFin = devise.getDateFin() == null || !date.isAfter(devise.getDateFin());
                if (isAfterOrEqualDebut && isBeforeOrEqualFin) {
                    return devise;
                }
            }
        }
        return null;
    }

    @Override
    public Devise getByRef(Integer ref) {
        Optional<Devise> optDevise = this.devises.stream().filter(d -> d.getRef().equals(ref)).findFirst();
        return optDevise.orElse(null);
    }

    @Override
    public void add(Devise devise) {
        try {
            validateDeviseHistory(devise);
            fileManager.ajouterDevise(devise);
            init(); 
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de la devise.", e);
        }
    }

    @Override
    public void update(Devise devise) {
        try {
            TransactionCourantServiceRemote serviceRemote = getTransactionCourantService();
            List<TransactionCourantDto> dtos = serviceRemote.getByDeviseRef(devise.getRef());
            Devise oldDevise = getByRef(devise.getRef());
            if(oldDevise == null)
            {
                throw new IOException("Aucune devise à mettre à jour trouvée");
            }
            validateDeviseHistory(devise);
            for(TransactionCourantDto dto : dtos)
            {
                BigDecimal ogMontant = dto.getMontant()
                        .divide(BigDecimal.valueOf(oldDevise.getMontant()), 10, RoundingMode.HALF_UP);
                BigDecimal nouveauMontant = ogMontant.multiply(BigDecimal.valueOf(devise.getMontant()))
                                     .setScale(2, RoundingMode.HALF_UP);
                dto.setMontant(nouveauMontant);
            }

            List<TransactionCourantDto> updatedDtos = serviceRemote.update(dtos);   
            fileManager.modifierDevise(devise);        

            init(); 
        } catch (IOException | NamingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification de la devise.", e);
        }
    }

    @Override
    public void delete(Integer ref) {
        try {
            // Devise devise = getByRef(ref);
            // if(devise == null)
            // {
            //     throw new IOException("Aucune devise correspondante à la ref: "+ref);
            // }
            // TransactionCourantServiceRemote serviceRemote = getTransactionCourantService();
            // List<TransactionCourantDto> dtos = serviceRemote.getByDeviseRef(ref);

            // Devise previousRef = findPreviousDeviseByNameAndDate(devise.getNom(), devise.getDateDebut());

            fileManager.supprimerDevise(ref);
            init(); 
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification de la devise.", e);
        }
    }

    @Override
    public Devise getDevisePrecedente(Integer ref) {
        Devise currentDevise = getByRef(ref);
        if (currentDevise == null) {
            return null;
        }

        return devises.stream()
                .filter(d -> d.getNom().equalsIgnoreCase(currentDevise.getNom()))
                .filter(d -> d.getDateDebut().isBefore(currentDevise.getDateDebut()))
                .sorted((d1, d2) -> d2.getDateDebut().compareTo(d1.getDateDebut()))
                .findFirst()
                .orElse(null);
    }

    private void checkVariationRate(Devise currentDevise, Devise previousDevise) {
        if (previousDevise != null) {
            try {
                TauxVariationServiceRemote tauxVariationService = getTauxVariationService();
                TauxVariation tauxVariation = tauxVariationService.getTauxVariationForDevise(currentDevise.getNom(), currentDevise.getDateDebut());

                if (tauxVariation != null) {
                    double minAllowed = previousDevise.getMontant() * (1 - tauxVariation.getPourcentage() / 100);
                    double maxAllowed = previousDevise.getMontant() * (1 + tauxVariation.getPourcentage() / 100);

                    if (currentDevise.getMontant() < minAllowed || currentDevise.getMontant() > maxAllowed) {
                        throw new IllegalArgumentException("Le montant de la devise " + currentDevise.getNom() + " (Ref: " + currentDevise.getRef() + ") à la date " + currentDevise.getDateDebut() + " est en dehors de la marge de variation autorisée (" + String.format("%.2f", minAllowed) + " - " + String.format("%.2f", maxAllowed) + ").");
                    }
                }
            } catch (NamingException e) {
                throw new RuntimeException("Erreur lors de la récupération du service de taux de variation.", e);
            }
        }
    }

    private void validateDeviseHistory(Devise targetDevise) {
        List<Devise> devisesForValidation = new ArrayList<>(this.devises);

        // Remove the old version of the targetDevise if it's an update
        devisesForValidation.removeIf(d -> d.getRef().equals(targetDevise.getRef()));

        // Add the new/updated targetDevise
        devisesForValidation.add(targetDevise);

        // Filter by name and sort by dateDebut
        List<Devise> sortedDevises = devisesForValidation.stream()
                .filter(d -> d.getNom().equalsIgnoreCase(targetDevise.getNom()))
                .sorted((d1, d2) -> d1.getDateDebut().compareTo(d2.getDateDebut()))
                .collect(java.util.stream.Collectors.toList());

        // Check variation rate for each devise in the history
        for (int i = 0; i < sortedDevises.size(); i++) {
            Devise currentDevise = sortedDevises.get(i);
            Devise previousDevise = (i > 0) ? sortedDevises.get(i - 1) : null;
            checkVariationRate(currentDevise, previousDevise);
        }
    }

    private Devise findPreviousDeviseByNameAndDate(String deviseName, LocalDate date) {
        return devises.stream()
                .filter(d -> d.getNom().equalsIgnoreCase(deviseName))
                .filter(d -> d.getDateDebut().isBefore(date))
                .sorted((d1, d2) -> d2.getDateDebut().compareTo(d1.getDateDebut()))
                .findFirst()
                .orElse(null);
    }

    private TauxVariationServiceRemote getTauxVariationService() throws NamingException {
        final Context context = new InitialContext();
        String lookupString = "ejb:service-change/service-change-ejb/TauxVariationService!itu.banque.api.remote.TauxVariationServiceRemote";
        return (TauxVariationServiceRemote) context.lookup(lookupString);
    }

    private TransactionCourantServiceRemote getTransactionCourantService() throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://host.docker.internal:8081");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");

        final Context context = new InitialContext(jndiProperties);
        String lookupString = "ejb:service-compte/service-compte-ejb/TransactionCourantService!itu.banque.api.remote.TransactionCourantServiceRemote";

        return (TransactionCourantServiceRemote) context.lookup(lookupString);
    }
}
