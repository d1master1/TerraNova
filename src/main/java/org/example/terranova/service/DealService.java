package org.example.terranova.service;

import org.example.terranova.model.*;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface DealService {
    List<Deal> getAllDeals(Sort sort);
    void saveDeal(Deal deal);
    void deleteDeal(Long id);
    List<Deal> findAll();
    List<Deal> findAllByUser(User user);
    Optional<Deal> findById(Long id);
    void deleteAllDeals();
    boolean existsByRealtyId(Long id);
    void deleteDealById(Long id);
    void deleteDealsByIds(List<Long> ids);

}