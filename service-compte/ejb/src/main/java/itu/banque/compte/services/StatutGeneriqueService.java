package itu.banque.compte.services;

import itu.banque.compte.daos.StatutGeneriqueDAO;
import itu.banque.compte.entities.StatutGenerique;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class StatutGeneriqueService {

    @EJB
    StatutGeneriqueDAO statutGeneriqueDAO;

    public boolean siStatutExiste(String tableReference, Integer idReference, String libelle)
    {
        StatutGenerique dernierStatut = this.statutGeneriqueDAO.findLatestByIdReference(tableReference, idReference);
        if(dernierStatut != null && dernierStatut.getLibelle().getLibelle().equalsIgnoreCase(libelle))
        {
            return true;
        }
        return false;
    }
}
