package org.example.terranova.impl;

import lombok.RequiredArgsConstructor;
import org.example.terranova.model.*;
import org.example.terranova.repo.ClientRepo;
import org.example.terranova.repo.DealRepo;
import org.example.terranova.repo.EmployeeRepo;
import org.example.terranova.repo.RealtyRepo;
import org.example.terranova.service.DealService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepo dealRepository;
    private final RealtyRepo realtyRepository;
    private final ClientRepo clientRepository;
    private final EmployeeRepo employeeRepository;

    public List<Deal> getAllDeals(Sort sort) {
        return dealRepository.findAll(sort);
    }

    @Override
    public void saveDeal(Deal deal) {
        if (deal.getRealty() != null && deal.getRealty().getId() != null) {
            Realty realty = realtyRepository.findById(deal.getRealty().getId()).orElse(null);
            deal.setRealty(realty);
        }

        if (deal.getClient() != null && deal.getClient().getId() != null) {
            Client client = clientRepository.findById(deal.getClient().getId()).orElse(null);
            deal.setClient(client);
        }

        if (deal.getEmployee() != null && deal.getEmployee().getId() != null) {
            Employee employee = employeeRepository.findById(deal.getEmployee().getId()).orElse(null);
            deal.setEmployee(employee);
        }

        dealRepository.save(deal);
    }

    @Override
    public void deleteDeal(Long id) {
        if (dealRepository.existsById(id)) {
            dealRepository.deleteById(id);
        }
    }

    @Override
    public List<Deal> findAll() {
        return dealRepository.findAll();
    }

    @Override
    public List<Deal> findAllByUser(User user) {
        return dealRepository.findAllByUser(user);
    }

    @Override
    public Optional<Deal> findById(Long id) {
        return dealRepository.findById(id);
    }

    @Override
    public void deleteAllDeals() {
        dealRepository.deleteAll();
    }

    @Override
    public boolean existsByRealtyId(Long realtyId) {
        return dealRepository.existsByRealtyId(realtyId);
    }

    @Override
    public void deleteDealById(Long id) {
        dealRepository.deleteById(id);
    }

    @Override
    public void deleteDealsByIds(List<Long> ids) {
        List<Deal> dealsToDelete = dealRepository.findAllById(ids);
        dealRepository.deleteAll(dealsToDelete);
    }

}
