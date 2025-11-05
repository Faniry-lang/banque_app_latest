package itu.banque.central.servlets;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.remote.ActionRoleServiceRemote;
import itu.banque.api.remote.DirectionServiceRemote;
import itu.banque.api.remote.UtilisateurServiceRemote;
import itu.banque.api.remote.UtilisateurSessionRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet  {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/UtilisateurService!itu.banque.api.remote.UtilisateurServiceRemote")
    UtilisateurServiceRemote utilisateurService;

    @EJB(lookup = "ejb:service-central/service-central-ejb/ActionRoleService!itu.banque.api.remote.ActionRoleServiceRemote")
    ActionRoleServiceRemote actionRoleService;

    @EJB(lookup = "ejb:service-central/service-central-ejb/DirectionService!itu.banque.api.remote.DirectionServiceRemote")
    DirectionServiceRemote directionService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String nom = req.getParameter("nom");
        String motDePasse = req.getParameter("motDePasse");
        var utilisateur = utilisateurService.login(nom, motDePasse);

        if(utilisateur != null) {
            var actionsRoles = actionRoleService.findAllowedRoles(utilisateur.getRoleLvl());
            var directions = directionService.getAll();

            try {
                // On demande explicitement au conteneur de créer une NOUVELLE instance de l'EJB stateful
                InitialContext ctx = new InitialContext();
                UtilisateurSessionRemote utilisateurSession = (UtilisateurSessionRemote) ctx.lookup(
                    "ejb:service-compte/service-compte-ejb/UtilisateurSession!itu.banque.api.remote.UtilisateurSessionRemote?stateful"
                );

                // On initialise cette nouvelle instance avec les données de l'utilisateur
                utilisateurSession.setUtilisateur(utilisateur);
                utilisateurSession.setActionsRoles(actionsRoles);
                utilisateurSession.setDirections(directions);

                // Et on la stocke dans la session HTTP pour les requêtes futures
                HttpSession session = req.getSession();
                session.setAttribute("utilisateurSession", utilisateurSession);

                resp.sendRedirect("comptes-courants");

            } catch (NamingException e) {
                throw new ServletException("Impossible de créer ou trouver l'EJB de session utilisateur.", e);
            }

        } else {
            req.setAttribute("errorMessage", "Erreur d'authentification. Veuillez réessayer.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }
	    

}
