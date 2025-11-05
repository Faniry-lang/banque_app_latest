package itu.banque.central.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.ContexteTransactionDto;
import itu.banque.api.dtos.FrequencePlafondDto;
import itu.banque.api.dtos.PlafondDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.ContexteTransactionServiceRemote;
import itu.banque.api.remote.FrequencePlafondServiceRemote;
import itu.banque.api.remote.PlafondServiceRemote;
import itu.banque.api.remote.TypeTransactionServiceRemote;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PlafondServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/PlafondService!itu.banque.api.remote.PlafondServiceRemote")
    PlafondServiceRemote plafondServiceRemote;
    
    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    CompteCourantServiceRemote compteCourantServiceRemote;
    
    @EJB(lookup = "ejb:service-compte/service-compte-ejb/TypeTransactionService!itu.banque.api.remote.TypeTransactionServiceRemote")
    TypeTransactionServiceRemote typeTransactionServiceRemote;
    
    @EJB(lookup = "ejb:service-compte/service-compte-ejb/FrequencePlafondService!itu.banque.api.remote.FrequencePlafondServiceRemote")
    FrequencePlafondServiceRemote frequencePlafondServiceRemote;
    
    @EJB(lookup = "ejb:service-compte/service-compte-ejb/ContexteTransactionService!itu.banque.api.remote.ContexteTransactionServiceRemote")
    ContexteTransactionServiceRemote contexteTransactionServiceRemote;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TypeTransactionDto> typeTransactions = this.typeTransactionServiceRemote.getAll();
        List<CompteCourantDto> compteCourants = this.compteCourantServiceRemote.getAll();
        List<FrequencePlafondDto> frequencePlafonds = this.frequencePlafondServiceRemote.getAll();
        List<ContexteTransactionDto> contexteTransactions = this.contexteTransactionServiceRemote.getAll();

        req.setAttribute("typeTransactions", typeTransactions);
        req.setAttribute("compteCourants", compteCourants);
        req.setAttribute("frequencePlafonds", frequencePlafonds);
        req.setAttribute("contexteTransactions", contexteTransactions);

        List<PlafondDto> plafonds = this.plafondServiceRemote.getAll();

        req.setAttribute("plafonds", plafonds);

        req.getRequestDispatcher("plafonds.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String redirectUrl = "plafonds";
        switch (action) {
            case "add":
                add(req, resp, redirectUrl);
                break;
            default:
                break;
        }

        resp.sendRedirect(redirectUrl);
    }
    
    private void add(HttpServletRequest req, HttpServletResponse resp, String redirectUrl) throws ServletException, IOException {
        try {
            BigDecimal montant = BigDecimal.valueOf(Double.valueOf(req.getParameter("montant")));
            Integer idCompte = (req.getParameter("idCompte") != null && !req.getParameter("idCompte").isEmpty()) ? Integer.parseInt(req.getParameter("idCompte")) : null ;
            Integer idTypeTransaction = Integer.parseInt(req.getParameter("idTypeTransaction"));
            Integer idFrequencePlafond = Integer.parseInt(req.getParameter("idFrequencePlafond"));
            Integer idContexteTransaction = Integer.parseInt(req.getParameter("idContexteTransaction"));
            LocalDate dateDebut = LocalDate.parse(req.getParameter("dateDebut"));
            LocalDate dateFin = (req.getParameter("dateFin") != null && !req.getParameter("dateFin").isEmpty()) ? LocalDate.parse(req.getParameter("dateFin")) : null;
        
            PlafondDto dto = new PlafondDto(null, montant, idTypeTransaction, idFrequencePlafond, idCompte, idContexteTransaction, dateDebut, dateFin);
            PlafondDto savedPlafond = this.plafondServiceRemote.add(dto);
            if(savedPlafond.getId() == null)
            {
                throw new Exception("La sauvegarde du plafond a échoué");
            }
        } catch(Exception e)
        {
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name());
            redirectUrl += "?error=" + errorMessage;
        }
    }
        
}
