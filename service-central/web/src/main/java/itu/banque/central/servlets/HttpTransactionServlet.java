package itu.banque.central.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.remote.ActionRoleServiceRemote;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.api.remote.TypeTransactionServiceRemote;
import itu.banque.api.remote.UtilisateurSessionRemote;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class HttpTransactionServlet extends HttpServlet {

    // EJBs locaux (inchangés)
    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TransactionCourantService!itu.banque.api.remote.TransactionCourantServiceRemote")
    TransactionCourantServiceRemote transactionCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    CompteCourantServiceRemote compteCourantService;

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TypeTransactionService!itu.banque.api.remote.TypeTransactionServiceRemote")
    TypeTransactionServiceRemote typeTransactionService;

    @EJB(lookup = "ejb:service-central/service-central-ejb/ActionRoleService!itu.banque.api.remote.ActionRoleServiceRemote")
    ActionRoleServiceRemote actionRoleService;

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Récupération des données locales (inchangées)
        List<TransactionCourantDto> transactions = transactionCourantService.getAll();
        req.setAttribute("transactions", transactions);
        List<CompteCourantDto> comptes = compteCourantService.getAll();
        req.setAttribute("comptes", comptes);
        List<TypeTransactionDto> types = typeTransactionService.getAll();
        req.setAttribute("types", types);

        // Logique de session utilisateur (inchangée)
        HttpSession session = req.getSession(false);
        UtilisateurSessionRemote utilisateurSession = null;
        if (session != null) {
            utilisateurSession = (UtilisateurSessionRemote) session.getAttribute("utilisateurSession");
        }

        boolean canCreate = false;
        if (utilisateurSession != null) {
            canCreate = utilisateurSession.canPerformAction("transaction_courant", "insert");
        }
        req.setAttribute("canCreate", canCreate);

        String openModal = req.getParameter("openModal");
        if (openModal != null && openModal.equals("true")) {
            req.setAttribute("openModal", true);
        }

        // --- NOUVEAU : Récupération des devises via HTTP --- 
        List<Devise> devises = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8082/service-change-web/api/devises"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                devises = gson.fromJson(response.body(), new TypeToken<List<Devise>>() {}.getType());
            } else {
                throw new IOException("HTTP GET Request Failed with Error code : " + response.statusCode() + ", Body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer l'erreur, par exemple en affichant un message à l'utilisateur
            req.setAttribute("errorMessage", "Erreur lors de la récupération des devises: " + e.getMessage());
        }

        // On extrait les noms uniques pour l'affichage
        Set<String> nomsDevisesUniques = devises.stream()
                                                .map(Devise::getNom)
                                                .collect(Collectors.toSet());
        req.setAttribute("devises", nomsDevisesUniques);

        req.getRequestDispatcher("http-transactions.jsp").forward(req, resp);
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

            // --- NOUVEAU : Appel HTTP pour trouver la devise et son taux --- 
            Devise devise = null;
            try {
                String url = String.format("http://localhost:8082/service-change-web/api/devises/%s/%s", nomDevise, dateTransactionStr);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    devise = gson.fromJson(response.body(), Devise.class);
                } else if (response.statusCode() == 404) {
                    resp.sendRedirect("http-transactions?error=deviseNotFound");
                    return;
                } else {
                    throw new IOException("HTTP GET Request Failed with Error code : " + response.statusCode() + ", Body: " + response.body());
                }

            } catch (Exception e) {
                throw new ServletException("Erreur lors de l'appel au service de change via HTTP.", e);
            }

            if (devise == null) {
                resp.sendRedirect("http-transactions?error=deviseNotFound");
                return;
            }

            // 2. Calcul du montant final
            BigDecimal taux = BigDecimal.valueOf(devise.getMontant());
            BigDecimal montantFinal = montantInitial.multiply(taux);

            // 3. Création et enregistrement de la transaction
            TransactionCourantDto transaction = new TransactionCourantDto();
            transaction.setIdCompte(Integer.parseInt(idCompteStr));
            transaction.setMontant(montantFinal); // On utilise le montant converti
            transaction.setIdTypeTransaction(Integer.parseInt(idTypeTransactionStr));
            transaction.setDateTransaction(dateTransaction);

            transactionCourantService.create(transaction);

            resp.sendRedirect("http-transactions");

        } catch (NumberFormatException | DateTimeParseException e) {
            e.printStackTrace();
            resp.sendRedirect("http-transactions?error=invalidInput");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("http-transactions?error=unknown");
        }
    }

    @Override
    public void init() throws ServletException {
        // TODO Auto-generated method stub
        super.init();
    }
    
}
