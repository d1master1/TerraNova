package org.example.terranova.service;

import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;

import java.util.List;

public interface RealtyService {
    Realty getRealtyById(Long id);
    Realty saveRealty(Realty realty);
    void deleteRealty(Long id);
    List<Realty> findAll();
    void deleteAll();
    Employee findByUser(User user);
    List<Realty> findAllByOwner(User owner);
    List<Realty> findAllByClientUser(User user);
    void deleteById(Long id);
    int deleteAllExceptInDeals();

    int deleteUnlinkedRealties();

    int countLinkedRealties();

    List<Realty> deleteAllExceptWithDeals();
}