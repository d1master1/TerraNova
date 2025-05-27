package org.example.terranova.impl;

import lombok.RequiredArgsConstructor;
import org.example.terranova.model.Employee;
import org.example.terranova.model.User;
import org.example.terranova.repo.DealRepo;
import org.example.terranova.repo.EmployeeRepo;
import org.example.terranova.service.EmployeeService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DealRepo dealRepo;

    @Override
    public List<Employee> getAllEmployeesSortedBy(String sortBy) {
        List<String> allowedFields = Arrays.asList("surname", "name", "position", "email");

        if (!allowedFields.contains(sortBy)) {
            sortBy = "surname";
        }

        return employeeRepo.findAll(Sort.by(Sort.Direction.ASC, sortBy));
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepo.findById(id).orElse(null);
    }

    @Override
    public void saveEmployee(Employee employee) {
        employeeRepo.save(employee);
    }

    @Override
    public void deleteAllEmployees() {
        employeeRepo.deleteAll();
    }

    @Override
    public List<Employee> getAllEmployeesSorted(Sort sort) {
        return employeeRepo.findAll(sort);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public Employee findByUser(User user) {
        return employeeRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepo.findAll();
    }

    @Override
    public Employee getEmployeeByUserId(Long userId) {
        return employeeRepo.findByUserId(userId);
    }

    @Override
    public List<Employee> getAllEmployeesWithUsers() {
        return employeeRepo.findAllWithUser();
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepo.findById(id).orElse(null);
        if (employee == null) return;

        if (dealRepo.existsByEmployee(employee)) {
            throw new IllegalStateException("Employee is involved in a deal.");
        }

        employeeRepo.delete(employee);
    }

    @Override
    public List<Employee> deleteAllExceptWithDeals() {
        List<Employee> allEmployees = employeeRepo.findAll();
        List<Employee> undeleted = new ArrayList<>();

        for (Employee employee : allEmployees) {
            if (dealRepo.existsByEmployee(employee)) {
                undeleted.add(employee);
            } else {
                employeeRepo.delete(employee);
            }
        }

        return undeleted;
    }
}
