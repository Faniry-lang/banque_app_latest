package itu.banque.central.filters;

import java.io.IOException;

import itu.banque.api.remote.UtilisateurSessionRemote;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();

        // Public URLs
        boolean isLoginURL = requestURI.equals(contextPath + "/login");
        boolean isLoginJSP = requestURI.equals(contextPath + "/login.jsp");

        // Check if user is logged in
        boolean isLoggedIn = false;
        if (session != null) {
            UtilisateurSessionRemote userSession = (UtilisateurSessionRemote) session.getAttribute("utilisateurSession");
            if (userSession != null && userSession.getUtilisateur() != null) {
                isLoggedIn = true;
            }
        }

        if (isLoggedIn || isLoginURL || isLoginJSP) {
            // User is logged in or is accessing a public page, continue chain
            chain.doFilter(request, response);
        } else {
            // User is not logged in and trying to access a protected page
            res.sendRedirect(contextPath + "/login");
        }
    }
}
