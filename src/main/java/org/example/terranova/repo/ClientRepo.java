package org.example.terranova.repo;

import org.example.terranova.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepo extends JpaRepository<Client, Long> {
    List<Client> findAllByOrderBySurnameAsc();
    List<Client> findAllByOrderBySurnameDesc();
    List<Client> findAllByOrderByNameAsc();
    List<Client> findAllByOrderByNameDesc();

}