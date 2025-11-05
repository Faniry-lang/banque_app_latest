package itu.banque.central.servlets;

import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TauxVariation;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.api.remote.TauxVariationServiceRemote;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class TauxVariationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            TauxVariationServiceRemote tauxService = getTauxVariationService();
            DeviseServiceRemote deviseService = getDeviseService();

            List<TauxVariation> tauxVariations = tauxService.getAll();
            List<String> deviseNames = deviseService.getAll().stream()
                    .map(Devise::getNom)
                    .distinct()
                    .collect(Collectors.toList());

            req.setAttribute("tauxVariations", tauxVariations);
            req.setAttribute("deviseNames", deviseNames);
            req.getRequestDispatcher("/taux-variation.jsp").forward(req, resp);

        } catch (NamingException e) {
            throw new ServletException("Erreur de service", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            TauxVariationServiceRemote tauxService = getTauxVariationService();
            if ("add".equals(action)) {
                TauxVariation taux = new TauxVariation();
                taux.setDeviseName(req.getParameter("deviseName"));
                taux.setPourcentage(Double.parseDouble(req.getParameter("pourcentage")));
                taux.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
                String dateFinStr = req.getParameter("dateFin");
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    taux.setDateFin(LocalDate.parse(dateFinStr));
                }
                tauxService.add(taux);
            } else if ("update".equals(action)) {
                TauxVariation taux = new TauxVariation();
                taux.setRef(Integer.parseInt(req.getParameter("ref")));
                taux.setDeviseName(req.getParameter("deviseName"));
                taux.setPourcentage(Double.parseDouble(req.getParameter("pourcentage")));
                taux.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
                String dateFinStr = req.getParameter("dateFin");
                if (dateFinStr != null && !dateFinStr.isEmpty()) {
                    taux.setDateFin(LocalDate.parse(dateFinStr));
                }
                tauxService.update(taux);
            } else if ("delete".equals(action)) {
                int ref = Integer.parseInt(req.getParameter("ref"));
                tauxService.delete(ref);
            }
            resp.sendRedirect("taux-variation");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private TauxVariationServiceRemote getTauxVariationService() throws NamingException {
        return (TauxVariationServiceRemote) getService("ejb:service-change/service-change-ejb/TauxVariationService!itu.banque.api.remote.TauxVariationServiceRemote");
    }

    private DeviseServiceRemote getDeviseService() throws NamingException {
        return (DeviseServiceRemote) getService("ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote");
    }

    private Object getService(String lookupString) throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8082");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");
        final Context context = new InitialContext(jndiProperties);
        return context.lookup(lookupString);
    }
}
