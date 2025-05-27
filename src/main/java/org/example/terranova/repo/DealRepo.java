package org.example.terranova.repo;

import org.example.terranova.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DealRepo extends JpaRepository<Deal, Long> {

    List<Deal> findAll(Sort sort);
    @Query("SELECT d FROM Deal d WHERE d.client.user = :user")
    List<Deal> findAllByUser(@Param("user") User user);
    boolean existsByClient(Client client);
    boolean existsByEmployee(Employee employee);
    boolean existsByRealtyId(Long realtyId);
    List<Deal> findByRealty(Realty realty);

}
