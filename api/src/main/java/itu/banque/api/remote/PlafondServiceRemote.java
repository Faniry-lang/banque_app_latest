package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.PlafondDto;
import jakarta.ejb.Remote;

@Remote
public interface PlafondServiceRemote {
    List<PlafondDto> getAll();
    PlafondDto add(PlafondDto dto);
    PlafondDto update(PlafondDto dto);
    void remove(PlafondDto dto); 
}
