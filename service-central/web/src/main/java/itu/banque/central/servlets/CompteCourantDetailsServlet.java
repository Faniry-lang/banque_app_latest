package itu.banque.central.servlets;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.api.remote.UtilisateurSessionRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

@WebServlet("/compte-courant-details")
public class CompteCourantDetailsServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    private CompteCourantServiceRemote compteCourantService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect("comptes-courants");
                return;
            }
            int compteId = Integer.parseInt(idParam);

            CompteCourantDto compte = compteCourantService.findById(compteId);
            request.setAttribute("compte", compte);
            request.setAttribute("solde", compteCourantService.getSolde(LocalDate.now(), compteId));
            request.setAttribute("transactions", compteCourantService.getAllTransactions(compteId));
            request.setAttribute("virements", compteCourantService.getVirementsForCompte(compteId));

            request.setAttribute("typesTransactions", compteCourantService.getAllTypeTransactions());
            request.setAttribute("comptes", compteCourantService.getAll());

            DeviseServiceRemote deviseService = getDeviseService();
            List<String> devisesNoms = deviseService.getAll().stream()
                                                    .map(Devise::getNom)
                                                    .distinct()
                                                    .collect(Collectors.toList());
            request.setAttribute("devisesNoms", devisesNoms);

            request.getRequestDispatcher("compte-courant-details.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("comptes-courants?error=" + URLEncoder.encode("Erreur lors de la récupération des détails du compte.", StandardCharsets.UTF_8));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String idCompte = request.getParameter("idCompte");
        String redirectUrl = "compte-courant-details?id=" + idCompte;

        try {
            DeviseServiceRemote deviseService = getDeviseService();
            UtilisateurSessionRemote session = (UtilisateurSessionRemote) request.getSession().getAttribute("utilisateurSession");
            if(session == null)
            {
                response.sendRedirect("login");
                return; 
            }
            UtilisateurDto utilisateurDto = session.getUtilisateur();
            Integer utilisateurId = utilisateurDto != null ? utilisateurDto.getId() : null;

            switch (action) {
                case "insert_transaction":
                    TransactionCourantDto txDto = new TransactionCourantDto();
                    LocalDate dateTx = LocalDate.parse(request.getParameter("dateTransaction"));
                    String nomDeviseTx = request.getParameter("nomDevise");
                    Devise deviseTx = deviseService.getByNomEtDate(nomDeviseTx, dateTx);
                    if (deviseTx == null) throw new Exception("La devise '" + nomDeviseTx + "' n'est pas valide pour la date choisie.");

                    txDto.setIdCompte(Integer.parseInt(idCompte));
                    txDto.setMontant(new BigDecimal(request.getParameter("montant")));
                    txDto.setIdTypeTransaction(Integer.parseInt(request.getParameter("idTypeTransaction")));
                    txDto.setDateTransaction(dateTx);
                    txDto.setDeviseRef(deviseTx.getRef());
                    compteCourantService.effectuerTransaction(txDto);
                    break;

                case "insert_virement":
                    VirementDto virementDto = new VirementDto();
                    LocalDate dateVir = LocalDate.parse(request.getParameter("dateVirement"));
                    String nomDeviseVir = request.getParameter("nomDevise");
                    Devise deviseVir = deviseService.getByNomEtDate(nomDeviseVir, dateVir);
                    if (deviseVir == null) throw new Exception("La devise '" + nomDeviseVir + "' n'est pas valide pour la date choisie.");

                    virementDto.setIdCompte(Integer.parseInt(idCompte));
                    virementDto.setIdCompteBeneficiaire(Integer.parseInt(request.getParameter("idCompteBeneficiaire")));
                    virementDto.setMontant(new BigDecimal(request.getParameter("montant")));
                    virementDto.setDateVirement(dateVir);
                    virementDto.setDeviseRef(deviseVir.getRef());
                    compteCourantService.virer(virementDto);
                    break;

                case "valider_virement":
                    Integer virementId = Integer.parseInt(request.getParameter("virementId"));
                    compteCourantService.validerVirement(virementId, utilisateurId);
                    break;

                case "valider_transaction":
                    Integer transactionId = Integer.parseInt(request.getParameter("transactionId"));
                    compteCourantService.validerTransaction(transactionId, utilisateurId);
                    break;
            }
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectUrl + "&error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
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
}
