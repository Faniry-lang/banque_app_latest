package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import itu.banque.api.dtos.PlafondDto;
import itu.banque.api.remote.PlafondServiceRemote;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.daos.ContexteTransactionDAO;
import itu.banque.compte.daos.FrequencePlafondDAO;
import itu.banque.compte.daos.PlafondDAO;
import itu.banque.compte.daos.TypeTransactionDao;
import itu.banque.compte.entities.CompteCourant;
import itu.banque.compte.entities.ContexteTransaction;
import itu.banque.compte.entities.FrequencePlafond;
import itu.banque.compte.entities.Plafond;
import itu.banque.compte.entities.TypeTransaction;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class PlafondService implements PlafondServiceRemote {

    @EJB
    PlafondDAO plafondDao;

    @EJB
    TypeTransactionDao typeTransactionDao;

    @EJB
    FrequencePlafondDAO frequencePlafondDAO;

    @EJB
    ContexteTransactionDAO contexteTransactionDao;

    @EJB
    CompteCourantDao compteCourantDao;

    @Override
    public List<PlafondDto> getAll() {
        List<Plafond> plafonds = this.plafondDao.findAll();
        return plafonds.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public PlafondDto add(PlafondDto dto) {
        Plafond plafond = this.plafondDao.create(fromDto(dto));
        return toDto(plafond);
    }

    @Override
    public PlafondDto update(PlafondDto dto) {
        Plafond plafond = this.plafondDao.update(fromDto(dto));
        return toDto(plafond);
    }

    @Override
    public void remove(PlafondDto dto) {
        this.plafondDao.delete(dto.getId());
    }

    public PlafondDto toDto(Plafond entity)
    {
        Integer id = entity.getId();
        BigDecimal montant = entity.getMontant();
        Integer idCompte = entity.getCompte() != null ? entity.getCompte().getId() : null;
        Integer idTypeTransaction = entity.getTypeTransaction().getId();
        Integer idFrequencePlafond = entity.getFrequencePlafond().getId();
        Integer idContexteTransaction = entity.getContexteTransaction().getId();
        LocalDate dateDebut = entity.getDateDebut();
        LocalDate dateFin = entity.getDateFin();

        PlafondDto dto = new PlafondDto(id, montant, idTypeTransaction, idFrequencePlafond, idCompte, idContexteTransaction, dateDebut, dateFin);

        return dto;
    }

    public Plafond fromDto(PlafondDto dto)
    {
        if(dto.getId() != null)
        {
            return this.plafondDao.findById(dto.getId());
        }
        
        Integer id = dto.getId();
        BigDecimal montant = dto.getMontant();
        TypeTransaction typeTransaction = this.typeTransactionDao.findById(dto.getIdTypeTransaction());
        FrequencePlafond frequencePlafond = this.frequencePlafondDAO.findById(dto.getIdFrequencePlafond());
        ContexteTransaction contexteTransaction = this.contexteTransactionDao.findById(dto.getIdContexteTransaction());
        LocalDate dateDebut = dto.getDateDebut();
        LocalDate dateFin = dto.getDateFin();
        CompteCourant compteCourant = dto.getIdCompte() != null ? this.compteCourantDao.findById(dto.getIdCompte()):null ;

        Plafond plafond = new Plafond(id, montant, typeTransaction, frequencePlafond, compteCourant, contexteTransaction, dateDebut, dateFin);
        return plafond;
    }
    
}
