package itu.banque.central.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.ContexteTransactionDto;
import itu.banque.api.dtos.CreerVirementDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.ContexteTransactionServiceRemote;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.api.remote.TypeTransactionServiceRemote;
import itu.banque.api.remote.UtilisateurSessionRemote;
import itu.banque.api.remote.VirementServiceRemote;
import itu.banque.api.ui.TransactionCourantViewModel;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CompteCourantDetailsServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    private CompteCourantServiceRemote compteCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TransactionCourantService!itu.banque.api.remote.TransactionCourantServiceRemote")
    private TransactionCourantServiceRemote transactionCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/ContexteTransactionService!itu.banque.api.remote.ContexteTransactionServiceRemote")
    private ContexteTransactionServiceRemote contexteTransactionService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TypeTransactionService!itu.banque.api.remote.TypeTransactionServiceRemote")
    private TypeTransactionServiceRemote typeTransactionService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/VirementService!itu.banque.api.remote.VirementServiceRemote")
    private VirementServiceRemote virementService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID du compte manquant.");
            return;
        }

        try {
            Integer id = Integer.parseInt(idParam);
            
            CompteCourantDto compte = compteCourantService.findById(id);
            List<TransactionCourantViewModel> transactions = transactionCourantService.getTransactionViewModelsByCompteId(id);
            BigDecimal solde = transactionCourantService.getSolde(LocalDate.now(), id);

            List<CompteCourantDto> tousLesComptes = compteCourantService.getAll();
            List<TypeTransactionDto> typesTransactions = typeTransactionService.getAll();
            List<Devise> devises = getDeviseService().getAll();

            req.setAttribute("compte", compte);
            req.setAttribute("transactions", transactions);
            req.setAttribute("solde", solde);
            req.setAttribute("comptes", tousLesComptes);
            req.setAttribute("typesTransactions", typesTransactions);
            req.setAttribute("devises", devises.stream().map(Devise::getNom).collect(Collectors.toSet()));

            req.getRequestDispatcher("/compte-courant-details.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID du compte invalide.");
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la récupération des détails du compte.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String idCompteStr = req.getParameter("idCompte");

        if (action == null || idCompteStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action ou ID de compte manquant.");
            return;
        }

        String redirectUrl = "compte-courant-details?id=" + idCompteStr;

        try {
            switch (action) {
                case "insert_transaction":
                    effectuerTransaction(req);
                    redirectUrl += "&success=transaction_ok";
                    break;
                case "insert_virement":
                    virer(req);
                    redirectUrl += "&success=virement_ok";
                    break;
                case "change_status":
                    changerStatut(req);
                    redirectUrl += "&success=change_status_ok";
                    break;
                default:
                    redirectUrl += "&error=action_inconnue";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name());
            redirectUrl += "&error=" + errorMessage;
        }
        
        resp.sendRedirect(redirectUrl);
    }

    private void virer(HttpServletRequest req) throws Exception {

        UtilisateurSessionRemote session = (UtilisateurSessionRemote) req.getSession().getAttribute("utilisateurSession");
        UtilisateurDto utilisateur = session.getUtilisateur();
        BigDecimal montant = new BigDecimal(req.getParameter("montant"));
        Integer idCompte = Integer.parseInt(req.getParameter("idCompte"));
        Integer idCompteBeneficiaire = Integer.parseInt(req.getParameter("idCompteBeneficiaire"));
        LocalDate dateVirement = LocalDate.parse(req.getParameter("dateVirement"));
        String nomDevise = req.getParameter("deviseRef");

        DeviseServiceRemote deviseService = getDeviseService();
        Devise devise = deviseService.getByNomEtDate(nomDevise, dateVirement);
        if (devise == null) {
            throw new ServletException("Devise non trouvée pour la date spécifiée.");
        }

        CreerVirementDto dto = new CreerVirementDto(montant, idCompte, idCompteBeneficiaire, dateVirement, utilisateur.getId(), devise.getRef());
        compteCourantService.virer(dto);
    }

    private void effectuerTransaction(HttpServletRequest req) throws Exception {
        Integer idCompte = Integer.parseInt(req.getParameter("idCompte"));
        BigDecimal montantInitial = new BigDecimal(req.getParameter("montant"));
        Integer idTypeTransaction = Integer.parseInt(req.getParameter("idTypeTransaction"));
        String nomDevise = req.getParameter("nomDevise");
        LocalDate dateTransaction = LocalDate.parse(req.getParameter("dateTransaction"));

        Devise devise = getDeviseService().getByNomEtDate(nomDevise, dateTransaction);
        if (devise == null) {
            throw new ServletException("Devise non trouvée pour la date spécifiée.");
        }

        ContexteTransactionDto ctdto = contexteTransactionService.findByLibelle("STANDARD");
        if (ctdto == null) {
            throw new ServletException("Contexte de transaction 'STANDARD' non trouvé.");
        }

        BigDecimal taux = BigDecimal.valueOf(devise.getMontant());
        BigDecimal montantFinal = montantInitial.multiply(taux);

        TransactionCourantDto transaction = new TransactionCourantDto();
        transaction.setIdCompte(idCompte);
        transaction.setMontant(montantFinal);
        transaction.setIdTypeTransaction(idTypeTransaction);
        transaction.setDeviseRef(devise.getRef());
        transaction.setDateTransaction(dateTransaction);
        transaction.setIdContexteTransaction(ctdto.getId());

        transactionCourantService.effectuerTransaction(transaction);
    }

    private DeviseServiceRemote getDeviseService() throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8082");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");
        
        final Context context = new InitialContext(jndiProperties);
        String lookupString = "ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote";
        
        return (DeviseServiceRemote) context.lookup(lookupString);
    }

    private void changerStatut(HttpServletRequest req) throws Exception {
        Integer idTransaction = Integer.parseInt(req.getParameter("idTransaction"));
        String statutActuel = req.getParameter("statutActuel");
        String contexte = req.getParameter("contexteTransaction");
        UtilisateurSessionRemote session = (UtilisateurSessionRemote) req.getSession().getAttribute("utilisateurSession");
        UtilisateurDto utilisateur = session.getUtilisateur();

        if ("VIREMENT".equalsIgnoreCase(contexte)) {
            VirementDto virement = virementService.findByTransactionId(idTransaction);
            if (virement == null) {
                throw new ServletException("Impossible de trouver le virement lié à cette transaction.");
            }

            if ("VALIDE".equalsIgnoreCase(statutActuel)) {
                virementService.annuler(virement, utilisateur.getId());
            } else if ("ANNULE".equalsIgnoreCase(statutActuel)) {
                virementService.valider(virement, utilisateur.getId());
            }
        } else {
            TransactionCourantDto tx = this.transactionCourantService.findById(idTransaction);
            
            if ("VALIDE".equalsIgnoreCase(statutActuel)) {
                transactionCourantService.annuler(tx, utilisateur.getId());
            } else if ("ANNULE".equalsIgnoreCase(statutActuel)) {
                transactionCourantService.valider(tx, utilisateur.getId());
            }
        }
    }
}
