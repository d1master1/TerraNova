package org.example.terranova.service;

import org.example.terranova.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> findAll();
    List<Client> findAllSortedByField(String fieldName, String sortDirection);
    Optional<Client> findById(Long id);
    void save(Client client);
    void deleteById(Long id);
    void deleteAll();
    boolean deleteByIdIfPossible(Long id);
    int deleteAllExceptWithDeals();

}