package org.example.terranova.service;

import org.example.terranova.impl.EmployeeServiceImpl;
import org.example.terranova.model.Employee;
import org.example.terranova.repo.EmployeeRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void getAllEmployeesSortedBy_shouldReturnSortedList() {
        List<Employee> mockEmployees = List.of(new Employee(), new Employee());

        Mockito.when(employeeRepo.findAll(Sort.by("name"))).thenReturn(mockEmployees);

        List<Employee> result = employeeService.getAllEmployeesSortedBy("name");

        Assertions.assertEquals(mockEmployees, result);
        Mockito.verify(employeeRepo).findAll(Sort.by("name"));
    }
}