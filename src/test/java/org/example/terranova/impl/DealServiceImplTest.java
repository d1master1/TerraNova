package org.example.terranova.impl;

import org.example.terranova.model.*;
import org.example.terranova.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealServiceImplTest {

    @Mock
    private DealRepo dealRepo;

    @Mock
    private RealtyRepo realtyRepo;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private DealServiceImpl dealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDeal_shouldResolveEntitiesAndSave() {
        Deal deal = new Deal();
        Realty realty = new Realty();
        realty.setId(1L);
        deal.setRealty(realty);
        Client client = new Client();
        client.setId(2L);
        deal.setClient(client);
        Employee employee = new Employee();
        employee.setId(3L);
        deal.setEmployee(employee);

        when(realtyRepo.findById(1L)).thenReturn(Optional.of(realty));
        when(clientRepo.findById(2L)).thenReturn(Optional.of(client));
        when(employeeRepo.findById(3L)).thenReturn(Optional.of(employee));

        dealService.saveDeal(deal);

        // Проверяем, что сущности установлены из репозиториев
        assertEquals(realty, deal.getRealty());
        assertEquals(client, deal.getClient());
        assertEquals(employee, deal.getEmployee());

        verify(dealRepo).save(deal);
    }

    @Test
    void deleteDeal_existingId_shouldCallDeleteById() {
        Long dealId = 10L;

        // Мокируем existsById на true, чтобы метод deleteDeal выполнил удаление
        when(dealRepo.existsById(dealId)).thenReturn(true);

        // Вызываем тестируемый метод
        dealService.deleteDeal(dealId);

        // Проверяем, что deleteById вызван с правильным параметром
        verify(dealRepo).deleteById(dealId);
    }

    @Test
    void deleteDeal_nonExistingId_shouldNotCallDelete() {
        Long dealId = 10L;

        when(dealRepo.findById(dealId)).thenReturn(Optional.empty());

        dealService.deleteDeal(dealId);

        verify(dealRepo, never()).deleteById(anyLong());
    }

    @Test
    void findById_existingId_shouldReturnDeal() {
        Deal deal = new Deal();
        deal.setId(5L);

        when(dealRepo.findById(5L)).thenReturn(Optional.of(deal));

        Optional<Deal> result = dealService.findById(5L);

        assertTrue(result.isPresent());
        assertEquals(deal, result.get());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(dealRepo.findById(999L)).thenReturn(Optional.empty());

        Optional<Deal> result = dealService.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByUser_shouldReturnList() {
        User user = new User();
        List<Deal> deals = List.of(new Deal(), new Deal());

        when(dealRepo.findAllByUser(user)).thenReturn(deals);

        List<Deal> result = dealService.findAllByUser(user);

        assertEquals(deals, result);
    }

    @Test
    void deleteAllDeals_shouldCallDeleteAll() {
        dealService.deleteAllDeals();

        verify(dealRepo).deleteAll();
    }
}
