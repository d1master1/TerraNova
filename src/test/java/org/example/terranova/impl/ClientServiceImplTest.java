package org.example.terranova.impl;

import org.example.terranova.model.Client;
import org.example.terranova.repo.ClientRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @Mock
    private ClientRepo clientRepo;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnAllClients() {
        List<Client> clients = List.of(new Client(), new Client());
        when(clientRepo.findAll()).thenReturn(clients);

        List<Client> result = clientService.findAll();

        assertEquals(clients.size(), result.size());
        verify(clientRepo).findAll();
    }

    @Test
    void findById_existingId_shouldReturnClient() {
        Client client = new Client();
        client.setId(1L);
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(clientRepo).findById(1L);
    }

    @Test
    void save_shouldInvokeRepoSave() {
        Client client = new Client();
        clientService.save(client);

        verify(clientRepo).save(client);
    }

    @Test
    void deleteById_shouldInvokeRepoDeleteById() {
        clientService.deleteById(1L);

        verify(clientRepo).deleteById(1L);
    }

    @Test
    void deleteAll_shouldInvokeRepoDeleteAll() {
        clientService.deleteAll();

        verify(clientRepo).deleteAll();
    }

    @Test
    void findAllSortedByField_validFieldAsc_shouldReturnSorted() {
        List<Client> clients = List.of(new Client(), new Client());
        when(clientRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(clients);

        List<Client> result = clientService.findAllSortedByField("name", "asc");

        assertEquals(clients, result);
        verify(clientRepo).findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void findAllSortedByField_invalidField_shouldThrowException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clientService.findAllSortedByField("invalidField", "asc");
        });

        assertTrue(thrown.getMessage().contains("Недопустимое поле для сортировки"));
    }

    @Test
    void findAllSortedByField_invalidDirection_shouldThrowException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clientService.findAllSortedByField("name", "invalidDir");
        });

        assertTrue(thrown.getMessage().contains("Недопустимое направление сортировки"));
    }
}
