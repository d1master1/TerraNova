package org.example.terranova.service;

import org.example.terranova.impl.DealServiceImpl;
import org.example.terranova.model.Deal;
import org.example.terranova.repo.DealRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private DealRepo dealRepo;

    @InjectMocks
    private DealServiceImpl dealService;

    @Test
    void getAllDeals_shouldReturnList() {
        List<Deal> mockDeals = List.of(new Deal(), new Deal());
        Mockito.when(dealRepo.findAll(Mockito.<Sort>any())).thenReturn(mockDeals);

        List<Deal> result = dealService.getAllDeals(Sort.by("id"));

        assertEquals(mockDeals, result);
        Mockito.verify(dealRepo).findAll(Mockito.any(Sort.class));
    }

    @Test
    void saveDeal_shouldCallSave() {
        Deal deal = new Deal();
        dealService.saveDeal(deal);
        Mockito.verify(dealRepo).save(deal);
    }

    @Test
    void deleteDeal_shouldCallDeleteById() {
        Long id = 1L;

        // Можно при необходимости замокать existsById
        Mockito.when(dealRepo.existsById(id)).thenReturn(true);

        dealService.deleteDeal(id);

        Mockito.verify(dealRepo).deleteById(id);
    }

    @Test
    void findAll_shouldReturnList() {
        List<Deal> mockDeals = List.of(new Deal());
        Mockito.when(dealRepo.findAll()).thenReturn(mockDeals);

        List<Deal> result = dealService.findAll();

        assertEquals(mockDeals, result);
        Mockito.verify(dealRepo).findAll();
    }

    @Test
    void findById_shouldReturnOptional() {
        Deal deal = new Deal();
        Mockito.when(dealRepo.findById(1L)).thenReturn(Optional.of(deal));

        Optional<Deal> result = dealService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(deal, result.get());
        Mockito.verify(dealRepo).findById(1L);
    }

    @Test
    void deleteAllDeals_shouldCallDeleteAll() {
        dealService.deleteAllDeals();
        Mockito.verify(dealRepo).deleteAll();
    }
}
