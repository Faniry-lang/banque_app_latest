package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import itu.banque.api.dtos.FraisBancaireDto;
import itu.banque.api.remote.FraisBancaireServiceRemote;
import itu.banque.compte.daos.FraisBancaireDAO;
import itu.banque.compte.entities.FraisBancaire;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class FraisBancaireService implements FraisBancaireServiceRemote {

    @EJB
    FraisBancaireDAO fraisBancaireDAO;

    @Override
    public List<FraisBancaireDto> getAll() {
        List<FraisBancaire> fraisBancaires = this.fraisBancaireDAO.findAll();
        return fraisBancaires.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public FraisBancaireDto add(FraisBancaireDto dto) {
        FraisBancaire fraisBancaire = this.fraisBancaireDAO.create(fromDto(dto));
        if(fraisBancaire != null)
        {
            return toDto(fraisBancaire);
        }
        return null;
    }

    @Override
    public FraisBancaireDto getByDateAndMontant(LocalDate date, BigDecimal montant) {
        FraisBancaire fraisBancaire = this.fraisBancaireDAO.getByDateAndMontant(date, montant);
        if(fraisBancaire != null)
        {
            return toDto(fraisBancaire);
        }

        return null;
    }

    private FraisBancaireDto toDto(FraisBancaire entity)
    {
        Integer id = entity.getId();
        BigDecimal montantInf = entity.getMontantInf();
        BigDecimal montantSup = entity.getMontantSup();
        BigDecimal fraisForfaitaire = entity.getFraisForfaitaire();
        BigDecimal fraisPourcentage = entity.getFraisPourcentage();
        LocalDate dateFrais = entity.getDateFrais();

        FraisBancaireDto dto = new FraisBancaireDto(id, montantInf, montantSup, fraisForfaitaire, fraisPourcentage, dateFrais);

        return dto;
    }  

    public FraisBancaire fromDto(FraisBancaireDto dto)
    {
        Integer id = dto.getId();
        BigDecimal montantInf = dto.getMontantInf();
        BigDecimal montantSup = dto.getMontantSup();
        BigDecimal fraisForfaitaire = dto.getFraisForfaitaire();
        BigDecimal fraisPourcentage = dto.getFraisPourcentage();
        LocalDate dateFrais = dto.getDateFrais();

        FraisBancaire entity = new FraisBancaire(id, montantInf, montantSup, fraisForfaitaire, fraisPourcentage, dateFrais);

        return entity;
    }
    
}
