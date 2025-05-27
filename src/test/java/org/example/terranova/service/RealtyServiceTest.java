package org.example.terranova.service;

import org.example.terranova.impl.RealtyServiceImpl;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.model.Employee;
import org.example.terranova.repo.EmployeeRepo;
import org.example.terranova.repo.RealtyRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealtyServiceTest {

    @Mock
    private RealtyRepo realtyRepository;

    @Mock
    private EmployeeRepo employeeRepository;

    @InjectMocks
    private RealtyServiceImpl realtyService;  // ваша реализация

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRealtyById_ShouldReturnRealty() {
        Realty realty = new Realty();
        realty.setId(1L);
        when(realtyRepository.findById(1L)).thenReturn(Optional.of(realty));

        Realty found = realtyService.getRealtyById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void saveRealty_ShouldCallSave() {
        Realty realty = new Realty();
        realtyService.saveRealty(realty);
        verify(realtyRepository, times(1)).save(realty);
    }

    @Test
    void findAll_ShouldReturnList() {
        List<Realty> list = Arrays.asList(new Realty(), new Realty());
        when(realtyRepository.findAll()).thenReturn(list);

        List<Realty> result = realtyService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void deleteAll_ShouldCallDeleteAll() {
        realtyService.deleteAll();
        verify(realtyRepository, times(1)).deleteAll();
    }

    @Test
    void findAllByOwner_ShouldReturnList() {
        User owner = new User();
        List<Realty> list = Arrays.asList(new Realty(), new Realty());
        when(realtyRepository.findAllByOwner(owner)).thenReturn(list);

        List<Realty> result = realtyService.findAllByOwner(owner);

        assertEquals(2, result.size());
    }

    @Test
    void deleteById_ShouldCallDeleteById() {
        Long id = 1L;
        realtyService.deleteById(id);
        verify(realtyRepository, times(1)).deleteById(id);
    }

    @Test
    void findByUser_ShouldReturnEmployee() {
        User user = new User();
        Employee employee = new Employee();
        when(employeeRepository.findByUser(user)).thenReturn(Optional.of(employee));

        Employee result = realtyService.findByUser(user);

        assertNotNull(result);
        assertEquals(employee, result);
    }
}
