package org.example.terranova.repo;

import jakarta.transaction.Transactional;
import org.example.terranova.model.Client;
import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealtyRepo extends JpaRepository<Realty, Long> {
    List<Realty> findByClient(Client client);
    List<Realty> findAllByOwner(User owner);
    @Query("SELECT r FROM Realty r WHERE r.client.user = :user")
    List<Realty> findAllByClientUser(@Param("user") User user);
    // Удалить все объекты, ID которых не входят в переданный список
    @Modifying
    @Transactional
    @Query("DELETE FROM Realty r WHERE r.id NOT IN :ids")
    int deleteByIdNotIn(@Param("ids") List<Long> ids);
}