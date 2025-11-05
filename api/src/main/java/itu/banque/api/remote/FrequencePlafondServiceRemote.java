package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.FrequencePlafondDto;
import jakarta.ejb.Remote;

@Remote
public interface FrequencePlafondServiceRemote {
    List<FrequencePlafondDto> getAll();
}
