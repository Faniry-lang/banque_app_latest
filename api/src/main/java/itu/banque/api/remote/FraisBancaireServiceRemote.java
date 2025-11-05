package itu.banque.api.remote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.FraisBancaireDto;
import jakarta.ejb.Remote;

@Remote
public interface FraisBancaireServiceRemote {
    List<FraisBancaireDto> getAll();
    FraisBancaireDto add(FraisBancaireDto dto);
    FraisBancaireDto getByDateAndMontant(LocalDate date, BigDecimal montant); 
}
