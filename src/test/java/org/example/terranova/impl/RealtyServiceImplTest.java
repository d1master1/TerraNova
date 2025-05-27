package org.example.terranova.impl;

import org.example.terranova.model.Client;
import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.repo.EmployeeRepo;
import org.example.terranova.repo.RealtyRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealtyServiceImplTest {

    @Mock
    private RealtyRepo realtyRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private RealtyServiceImpl realtyService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRealtyById_found() {
        Realty realty = new Realty();
        realty.setId(1L);
        when(realtyRepo.findById(1L)).thenReturn(Optional.of(realty));

        Realty result = realtyService.getRealtyById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRealtyById_notFound() {
        when(realtyRepo.findById(1L)).thenReturn(Optional.empty());

        Realty result = realtyService.getRealtyById(1L);
        assertNull(result);
    }

    @Test
    void saveRealty_shouldCallSave() {
        Realty realty = new Realty();
        realtyService.saveRealty(realty);

        verify(realtyRepo, times(1)).save(realty);
    }

    @Test
    void deleteRealty_shouldCallDeleteById() {
        Long id = 3L;
        realtyService.deleteRealty(id);

        verify(realtyRepo, times(1)).deleteById(id);
    }

    @Test
    void findAll_shouldReturnList() {
        List<Realty> realties = List.of(new Realty());
        when(realtyRepo.findAll()).thenReturn(realties);

        List<Realty> result = realtyService.findAll();
        assertEquals(realties, result);
    }

    @Test
    void deleteAll_shouldCallDeleteAll() {
        realtyService.deleteAll();

        verify(realtyRepo, times(1)).deleteAll();
    }

    @Test
    void findByUser_found() {
        User user = new User();
        Employee employee = new Employee();
        when(employeeRepo.findByUser(user)).thenReturn(Optional.of(employee));

        Employee result = realtyService.findByUser(user);
        assertEquals(employee, result);
    }

    @Test
    void findByUser_notFound_shouldThrow() {
        User user = new User();
        when(employeeRepo.findByUser(user)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> realtyService.findByUser(user));
        assertEquals("Сотрудник не найден", ex.getMessage());
    }

    @Test
    void findAllByOwner_shouldReturnList() {
        User owner = new User();
        List<Realty> realties = List.of(new Realty());
        when(realtyRepo.findAllByOwner(owner)).thenReturn(realties);

        List<Realty> result = realtyService.findAllByOwner(owner);
        assertEquals(realties, result);
    }

    @Test
    void findAllByClientUser_shouldReturnList() {
        User clientUser = new User();
        List<Realty> realties = List.of(new Realty());
        when(realtyRepo.findAllByClientUser(clientUser)).thenReturn(realties);

        List<Realty> result = realtyService.findAllByClientUser(clientUser);
        assertEquals(realties, result);
    }

    @Test
    void deleteById_shouldCallDeleteById() {
        Long id = 7L;
        realtyService.deleteById(id);

        verify(realtyRepo, times(1)).deleteById(id);
    }
}
