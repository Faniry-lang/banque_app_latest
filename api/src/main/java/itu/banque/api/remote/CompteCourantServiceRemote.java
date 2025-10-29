package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import jakarta.ejb.Remote;

@Remote
public interface CompteCourantServiceRemote {
    List<CompteCourantDto> getAll();
}
