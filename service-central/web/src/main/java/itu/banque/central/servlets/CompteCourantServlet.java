package itu.banque.central.servlets;

import java.io.IOException;
import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CompteCourantServlet extends HttpServlet {

    @EJB(lookup = "ejb:service-compte/service-compte-ejb/CompteCourantService!itu.banque.api.remote.CompteCourantServiceRemote")
    CompteCourantServiceRemote compteCourantServiceRemote;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CompteCourantDto> comptesCourants = this.compteCourantServiceRemote.getAll();
        req.setAttribute("comptesCourants", comptesCourants);
        req.getRequestDispatcher("comptes-courants-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPost(req, resp);
    }
    
}
