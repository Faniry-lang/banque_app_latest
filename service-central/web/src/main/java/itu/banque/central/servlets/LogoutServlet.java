package itu.banque.central.servlets;

import java.io.IOException;

import itu.banque.api.remote.UtilisateurSessionRemote;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null) {
            // On récupère le bean stateful de la session
            UtilisateurSessionRemote utilisateurSession = (UtilisateurSessionRemote) session.getAttribute("utilisateurSession");

            if (utilisateurSession != null) {
                try {
                    // On appelle la méthode @Remove pour que le conteneur EJB détruise l'instance
                    utilisateurSession.destroy();
                } catch (Exception e) {
                    // Ignorer les erreurs si le bean est déjà détruit ou invalide
                    e.printStackTrace();
                }
            }
            // On invalide la session HTTP, ce qui supprime tous les attributs
            session.invalidate();
        }

        // On redirige vers la page de login
        resp.sendRedirect("login");
    }
}
