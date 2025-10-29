package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.DirectionDto;
import jakarta.ejb.Remote;

@Remote
public interface DirectionServiceRemote {
    List<DirectionDto> getAll();
}
