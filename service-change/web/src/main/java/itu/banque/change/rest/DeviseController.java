package itu.banque.change.rest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import itu.banque.api.dtos.Devise;
import itu.banque.api.remote.DeviseServiceRemote;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/devises")
public class DeviseController {

    @EJB(lookup = "ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote")
    private DeviseServiceRemote deviseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Devise> devises = deviseService.getAll();
        return Response.ok(devises).build();
    }

    @GET
    @Path("/{nom}/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByNomEtDate(@PathParam("nom") String nom, @PathParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            Devise devise = deviseService.getByNomEtDate(nom, date);
            if (devise != null) {
                return Response.ok(devise).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("Devise not found for name '" + nom + "' on date '" + dateStr + "'")
                               .build();
            }
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Invalid date format. Please use YYYY-MM-DD.")
                           .build();
        }
    }
}
