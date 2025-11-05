package itu.banque.central.servlets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.Devise;
import itu.banque.api.remote.DeviseServiceRemote;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DeviseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DeviseServiceRemote deviseServiceRemote = getDeviseService();
            List<Devise> devises = deviseServiceRemote.getAll();
            req.setAttribute("devises", devises);
            req.getRequestDispatcher("/devises.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String redirectUrl = "devises"; 

        try {
            DeviseServiceRemote deviseService = getDeviseService();
            if ("add".equals(action)) {
                Devise devise = new Devise();
                devise.setNom(req.getParameter("nom"));
                devise.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
                String dateFinStr = req.getParameter("dateFin");
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    devise.setDateFin(LocalDate.parse(dateFinStr));
                }
                devise.setMontant(Double.parseDouble(req.getParameter("montant")));
                deviseService.add(devise);
            } else if ("update".equals(action)) {
                Devise devise = new Devise();
                devise.setRef(Integer.parseInt(req.getParameter("ref")));
                devise.setNom(req.getParameter("nom"));
                devise.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
                String dateFinStr = req.getParameter("dateFin");
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    devise.setDateFin(LocalDate.parse(dateFinStr));
                }
                devise.setMontant(Double.parseDouble(req.getParameter("montant")));
                deviseService.update(devise);
            } else if ("delete".equals(action)) {
                int ref = Integer.parseInt(req.getParameter("ref"));
                deviseService.delete(ref);
            }
        } catch (IllegalArgumentException e) {
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name());
            redirectUrl += "?error=" + errorMessage;
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = URLEncoder.encode("Une erreur inattendue est survenue: " + e.getMessage(), StandardCharsets.UTF_8.name());
            redirectUrl += "?error=" + errorMessage;
        }
        resp.sendRedirect(redirectUrl);
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