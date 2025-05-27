package org.example.terranova.service;

import org.example.terranova.impl.ClientServiceImpl;
import org.example.terranova.model.Client;
import org.example.terranova.repo.ClientRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepo clientRepo;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void testFindAllSortedByField_validParams_shouldReturnClients() {
        List<Client> mockClients = List.of(new Client(), new Client());
        Mockito.when(clientRepo.findAll(Mockito.<Sort>any())).thenReturn(mockClients);

        List<Client> result = clientService.findAllSortedByField("name", "ASC");

        Assertions.assertEquals(mockClients, result);

        Mockito.verify(clientRepo).findAll((Sort) Mockito.argThat(argument -> {
            if (!(argument instanceof Sort sort)) {
                return false;
            }
            return sort.getOrderFor("name") != null && sort.getOrderFor("name").isAscending();
        }));
    }

    @Test
    void testFindAllSortedByField_invalidField_shouldThrow() {
        // Ожидаем, что при недопустимом поле выбросится исключение
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            clientService.findAllSortedByField("invalidField", "ASC");
        });
    }

    @Test
    void testFindAllSortedByField_invalidDirection_shouldThrow() {
        // Ожидаем, что при неправильном направлении сортировки выбросится исключение
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            clientService.findAllSortedByField("name", "INVALID_DIRECTION");
        });
    }
}
