package org.example.terranova.impl;

import lombok.RequiredArgsConstructor;
import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.repo.DealRepo;
import org.example.terranova.repo.EmployeeRepo;
import org.example.terranova.repo.RealtyRepo;
import org.example.terranova.service.RealtyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealtyServiceImpl implements RealtyService {

    private final RealtyRepo realtyRepo;
    private final EmployeeRepo employeeRepo;
    private final DealRepo dealRepo;

    @Override
    public Realty getRealtyById(Long id) {
        return realtyRepo.findById(id).orElse(null);
    }

    @Override
    public Realty saveRealty(Realty realty) {
        realtyRepo.save(realty);
        return realty;
    }

    @Override
    public void deleteRealty(Long id) {
        realtyRepo.deleteById(id);
    }

    @Override
    public List<Realty> findAll() {
        return realtyRepo.findAll();
    }

    @Override
    public void deleteAll() {
        realtyRepo.deleteAll();
    }

    @Override
    public Employee findByUser(User user) {
        return employeeRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
    }

    @Override
    public List<Realty> findAllByOwner(User owner) {
        return realtyRepo.findAllByOwner(owner);
    }

    @Override
    public void deleteById(Long id) {
        realtyRepo.deleteById(id);
    }

    @Override
    public List<Realty> findAllByClientUser(User user) {
        return realtyRepo.findAllByClientUser(user);
    }

    @Override
    public int deleteAllExceptInDeals() {
        List<Long> realtyIdsInDeals = dealRepo.findAll()
                .stream()
                .map(deal -> deal.getRealty().getId())
                .distinct()
                .toList();

        if (realtyIdsInDeals.isEmpty()) {
            int count = (int) realtyRepo.count();
            realtyRepo.deleteAll();
            return count;
        }

        return realtyRepo.deleteByIdNotIn(realtyIdsInDeals);
    }

    @Override
    public int deleteUnlinkedRealties() {
        List<Realty> all = realtyRepo.findAll();
        int deleted = 0;
        for (Realty realty : all) {
            if (dealRepo.findByRealty(realty).isEmpty()) {
                realtyRepo.delete(realty);
                deleted++;
            }
        }
        return deleted;
    }

    @Override
    public int countLinkedRealties() {
        return (int) realtyRepo.findAll().stream()
                .filter(realty -> !dealRepo.findByRealty(realty).isEmpty())
                .count();
    }

    @Override
    public List<Realty> deleteAllExceptWithDeals() {
        List<Realty> all = realtyRepo.findAll();
        List<Realty> toDelete = all.stream()
                .filter(realty -> dealRepo.findByRealty(realty).isEmpty())
                .collect(Collectors.toList());
        realtyRepo.deleteAll(toDelete);
        return toDelete;
    }

}