package itu.banque.central.servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.FraisBancaireDto;
import itu.banque.api.remote.FraisBancaireServiceRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FraisBancaireServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/FraisBancaireService!itu.banque.api.remote.FraisBancaireServiceRemote")
    FraisBancaireServiceRemote fraisBancaireService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<FraisBancaireDto> fraisBancaireDtos = this.fraisBancaireService.getAll();
        req.setAttribute("fraisBancaires", fraisBancaireDtos);
        req.getRequestDispatcher("frais-bancaire.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String montantInfStr = req.getParameter("montantInf");
        String montantSupStr = req.getParameter("montantSup");
        String fraisForfaitaireStr = req.getParameter("fraisForfaitaire");
        String fraisPourcentageStr = req.getParameter("fraisPourcentage");
        String dateFraisStr = req.getParameter("dateFrais");

        try 
        {
            BigDecimal montantInf = BigDecimal.valueOf(Double.valueOf(montantInfStr));
            BigDecimal montantSup = BigDecimal.valueOf(Double.valueOf(montantSupStr));
            BigDecimal fraisForfaitaire = (fraisForfaitaireStr != null && !fraisForfaitaireStr.isEmpty()) ? BigDecimal.valueOf(Double.valueOf(fraisForfaitaireStr)) : null;
            BigDecimal fraisPourcentage = (fraisPourcentageStr != null && !fraisPourcentageStr.isEmpty()) ? BigDecimal.valueOf(Double.valueOf(fraisPourcentageStr)) : null;
            LocalDate dateFrais = (dateFraisStr != null && !dateFraisStr.isEmpty()) ? LocalDate.parse(dateFraisStr) : LocalDate.now();
            
            FraisBancaireDto dto = new FraisBancaireDto(null, montantInf, montantSup, fraisForfaitaire, fraisPourcentage, dateFrais);

            this.fraisBancaireService.add(dto);

            resp.sendRedirect("frais-bancaire");

        } catch (Exception e)
        {
            resp.sendRedirect("frais-bancaire?error=Une erreur est survenue "+e.getMessage());
        }
    }
}
