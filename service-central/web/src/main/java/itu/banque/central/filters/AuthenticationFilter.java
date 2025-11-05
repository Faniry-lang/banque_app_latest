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

        boolean isLoginURL = requestURI.equals(contextPath + "/login");
        boolean isLoginJSP = requestURI.equals(contextPath + "/login.jsp");

        boolean isLoggedIn = false;
        if (session != null) {
            UtilisateurSessionRemote userSession = (UtilisateurSessionRemote) session.getAttribute("utilisateurSession");
            if (userSession != null && userSession.getUtilisateur() != null) {
                isLoggedIn = true;
            }
        }

        if (isLoggedIn || isLoginURL || isLoginJSP) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(contextPath + "/login");
        }
    }
}
