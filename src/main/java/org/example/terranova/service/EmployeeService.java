package org.example.terranova.service;

import org.example.terranova.model.Employee;
import org.example.terranova.model.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployeesSortedBy(String sortBy);
    Employee getEmployeeById(Long id);
    void saveEmployee(Employee employee);
    void deleteEmployee(Long id);
    void deleteAllEmployees();
    List<Employee> getAllEmployees();
    List<Employee> getAllEmployeesSorted(Sort sort);
    Employee findByUser(User user);
    List<Employee> findAll();
    Employee getEmployeeByUserId(Long userId);
    List<Employee> getAllEmployeesWithUsers();
    List<Employee> deleteAllExceptWithDeals();

}