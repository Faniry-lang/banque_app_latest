package itu.banque.central.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.ContexteTransactionDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.remote.ActionRoleServiceRemote;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.ContexteTransactionServiceRemote;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.api.remote.TypeTransactionServiceRemote;
import itu.banque.api.remote.UtilisateurSessionRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TransactionServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TransactionCourantService!itu.banque.api.remote.TransactionCourantServiceRemote")
    TransactionCourantServiceRemote transactionCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    CompteCourantServiceRemote compteCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TypeTransactionService!itu.banque.api.remote.TypeTransactionServiceRemote")
    TypeTransactionServiceRemote typeTransactionService;

    @EJB(lookup = "ejb:service-central/service-central-ejb/ActionRoleService!itu.banque.api.remote.ActionRoleServiceRemote")
    ActionRoleServiceRemote actionRoleService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/UtilisateurSession!itu.banque.api.remote.UtilisateurSessionRemote")
    UtilisateurSessionRemote utilisateurSession;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/ContexteTransactionService!itu.banque.api.remote.ContexteTransactionServiceRemote")
    ContexteTransactionServiceRemote contexteTransactionService;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TransactionCourantDto> transactions = transactionCourantService.getAll();
        req.setAttribute("transactions", transactions);
        List<CompteCourantDto> comptes = compteCourantService.getAll();
        req.setAttribute("comptes", comptes);
        List<TypeTransactionDto> types = typeTransactionService.getAll();
        req.setAttribute("types", types);

        List<Devise> devises = new ArrayList<>();
        try {
            final Properties jndiProperties = new Properties();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8082");
            jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
            jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");
            final Context context = new InitialContext(jndiProperties);

            String lookupString = "ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote";

            DeviseServiceRemote remoteDeviseService = (DeviseServiceRemote) context.lookup(lookupString);
            devises = remoteDeviseService.getAll();

        } catch (NamingException e) {
            // Cette erreur sera beaucoup plus précise sur la nature du problème.
            e.printStackTrace();
        }
        // On extrait les noms uniques pour l'affichage
        Set<String> nomsDevisesUniques = devises.stream()
                                                .map(Devise::getNom)
                                                .collect(Collectors.toSet());
        req.setAttribute("devises", nomsDevisesUniques);

        HttpSession session = req.getSession(false);
        UtilisateurSessionRemote utilisateurSession = (UtilisateurSessionRemote) session.getAttribute("utilisateurSession");

        boolean canCreate = false;
        if (utilisateurSession != null) {
            canCreate = utilisateurSession.canPerformAction("transaction_courant", "insert");
        }
        req.setAttribute("canCreate", canCreate);

        String openModal = req.getParameter("openModal");
        if (openModal != null && openModal.equals("true")) {
            req.setAttribute("openModal", true);
        }

        req.getRequestDispatcher("transactions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idCompteStr = req.getParameter("idCompte");
        String montantStr = req.getParameter("montant");
        String idTypeTransactionStr = req.getParameter("idTypeTransaction");
        String nomDevise = req.getParameter("nomDevise");
        String dateTransactionStr = req.getParameter("dateTransaction");

        try {
            LocalDate dateTransaction = LocalDate.parse(dateTransactionStr);
            BigDecimal montantInitial = new BigDecimal(montantStr);

            // 1. Appel EJB distant pour trouver la devise et son taux
            Devise devise = null;
            try {
                final Properties jndiProperties = new Properties();
                jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
                jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8082");
                jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
                jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");
                final Context context = new InitialContext(jndiProperties);
                String lookupString = "ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote";
                DeviseServiceRemote remoteDeviseService = (DeviseServiceRemote) context.lookup(lookupString);
                devise = remoteDeviseService.getByNomEtDate(nomDevise, dateTransaction);
            } catch (NamingException e) {
                throw new ServletException("Erreur lors de l'appel au service de change.", e);
            }

            if (devise == null) {
                resp.sendRedirect("transactions?error=deviseNotFound");
                return;
            }

            ContexteTransactionDto ctdto = this.contexteTransactionService.findByLibelle("STANDARD");
            if(ctdto == null)
            {
                throw new ServletException("Aucun contexte de transaction nommé 'STANDARD' trouvé dans la base de donnée");
            }

            // 2. Calcul du montant final
            BigDecimal taux = BigDecimal.valueOf(devise.getMontant());
            BigDecimal montantFinal = montantInitial.multiply(taux);

            // 3. Création et enregistrement de la transaction
            TransactionCourantDto transaction = new TransactionCourantDto();
            transaction.setIdCompte(Integer.parseInt(idCompteStr));
            transaction.setMontant(montantFinal); // On utilise le montant converti
            transaction.setIdTypeTransaction(Integer.parseInt(idTypeTransactionStr));
            transaction.setDeviseRef(devise.getRef());
            transaction.setDateTransaction(dateTransaction);
            transaction.setIdContexteTransaction(ctdto.getId());

            this.compteCourantService.effectuerTransaction(transaction);

            resp.sendRedirect("transactions");

        } catch (NumberFormatException | DateTimeParseException e) {
            e.printStackTrace();
            resp.sendRedirect("transactions?error=invalidInput");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("transactions?error=unknown");
        }
    }

    @Override
    public void init() throws ServletException {
        // TODO Auto-generated method stub
        super.init();
    }
    
}
