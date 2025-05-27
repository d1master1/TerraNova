package org.example.terranova.impl;

import org.example.terranova.model.Employee;
import org.example.terranova.model.User;
import org.example.terranova.repo.DealRepo;
import org.example.terranova.repo.EmployeeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private DealRepo dealRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEmployeeById_found() {
        Employee employee = new Employee();
        employee.setId(1L);
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getEmployeeById_notFound() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.empty());

        Employee result = employeeService.getEmployeeById(1L);
        assertNull(result);
    }

    @Test
    void saveEmployee_shouldCallSave() {
        Employee employee = new Employee();
        employeeService.saveEmployee(employee);

        verify(employeeRepo, times(1)).save(employee);
    }

    @Test
    void deleteEmployee_shouldCallDeleteEmployee() {
        Long id = 5L;
        Employee employee = new Employee();
        employee.setId(id);

        when(employeeRepo.findById(id)).thenReturn(Optional.of(employee));
        when(dealRepo.existsByEmployee(employee)).thenReturn(false); // Теперь мок работает

        employeeService.deleteEmployee(id);

        verify(employeeRepo, times(1)).delete(employee);
    }

    @Test
    void deleteAllEmployees_shouldCallDeleteAll() {
        employeeService.deleteAllEmployees();

        verify(employeeRepo, times(1)).deleteAll();
    }

    @Test
    void getAllEmployeesSortedBy_validField() {
        List<Employee> employees = List.of(new Employee());
        when(employeeRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployeesSortedBy("name");
        assertEquals(employees, result);
    }

    @Test
    void getAllEmployeesSortedBy_invalidField_defaultsToSurname() {
        List<Employee> employees = List.of(new Employee());
        when(employeeRepo.findAll(Sort.by(Sort.Direction.ASC, "surname"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployeesSortedBy("invalidField");
        assertEquals(employees, result);
    }

    @Test
    void findByUser_found() {
        User user = new User();
        Employee employee = new Employee();
        when(employeeRepo.findByUser(user)).thenReturn(Optional.of(employee));

        Employee result = employeeService.findByUser(user);
        assertEquals(employee, result);
    }

    @Test
    void findByUser_notFound_shouldThrow() {
        User user = new User();
        when(employeeRepo.findByUser(user)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> employeeService.findByUser(user));
        assertEquals("Сотрудник не найден", ex.getMessage());
    }

    @Test
    void getEmployeeByUserId_shouldReturnEmployee() {
        Employee employee = new Employee();
        when(employeeRepo.findByUserId(10L)).thenReturn(employee);

        Employee result = employeeService.getEmployeeByUserId(10L);
        assertEquals(employee, result);
    }

    @Test
    void getAllEmployeesWithUsers_shouldReturnList() {
        List<Employee> employees = List.of(new Employee());
        when(employeeRepo.findAllWithUser()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployeesWithUsers();
        assertEquals(employees, result);
    }

    @Test
    void getAllEmployeesSorted_shouldReturnList() {
        List<Employee> employees = List.of(new Employee());
        Sort sort = Sort.by("surname");
        when(employeeRepo.findAll(sort)).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployeesSorted(sort);
        assertEquals(employees, result);
    }

    @Test
    void findAll_shouldReturnList() {
        List<Employee> employees = List.of(new Employee());
        when(employeeRepo.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findAll();
        assertEquals(employees, result);
    }

    @Test
    void getAllEmployees_shouldReturnList() {
        List<Employee> employees = List.of(new Employee());
        when(employeeRepo.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(employees, result);
    }
}
