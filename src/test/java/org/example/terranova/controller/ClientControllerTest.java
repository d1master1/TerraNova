package org.example.terranova.controller;

import org.example.terranova.model.Client;
import org.example.terranova.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    private ClientService clientService;
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        clientController = new ClientController(clientService);
    }

    @Test
    void addClientForm() {
        Model model = mock(Model.class);
        String view = clientController.addClientForm(model);
        assertEquals("client/client_form", view);
        verify(model).addAttribute(eq("client"), any(Client.class));
    }

    @Test
    void saveClient_WithNoErrors() {
        Client client = new Client();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = clientController.saveClient(client, bindingResult);

        assertEquals("redirect:/client", view);
        verify(clientService).save(client);
    }

    @Test
    void saveClient_WithValidationErrors() {
        Client client = new Client();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = clientController.saveClient(client, bindingResult);

        assertEquals("client/client_form", view);
        verify(clientService, never()).save(any());
    }

    @Test
    void editClientForm_Found() {
        Long id = 1L;
        Model model = mock(Model.class);
        Client client = new Client();
        when(clientService.findById(id)).thenReturn(Optional.of(client));

        String view = clientController.editClientForm(id, model);

        assertEquals("client/client_form", view);
        verify(clientService).findById(id);
        verify(model).addAttribute("client", client);
    }

    @Test
    void editClientForm_NotFound() {
        Long id = 1L;
        Model model = mock(Model.class);
        when(clientService.findById(id)).thenReturn(Optional.empty());

        String view = clientController.editClientForm(id, model);

        assertEquals("redirect:/client", view);
        verify(clientService).findById(id);
        verify(model, never()).addAttribute(eq("client"), any());
    }

    @Test
    void deleteClient_DeletedSuccessfully() {
        Long id = 1L;
        when(clientService.deleteByIdIfPossible(id)).thenReturn(true);

        String view = clientController.deleteClient(id);

        assertEquals("redirect:/client", view);
        verify(clientService).deleteByIdIfPossible(id);
    }

    @Test
    void deleteClient_DeletionFailed() {
        Long id = 1L;
        when(clientService.deleteByIdIfPossible(id)).thenReturn(false);

        String view = clientController.deleteClient(id);

        assertEquals("redirect:/client?error=true", view);
        verify(clientService).deleteByIdIfPossible(id);
    }

    @Test
    void deleteAllClients_NoPartialError() {
        when(clientService.deleteAllExceptWithDeals()).thenReturn(0);

        String view = clientController.deleteAllClients();

        assertEquals("redirect:/client", view);
        verify(clientService).deleteAllExceptWithDeals();
    }

    @Test
    void deleteAllClients_WithPartialError() {
        int notDeletedCount = 3;
        when(clientService.deleteAllExceptWithDeals()).thenReturn(notDeletedCount);

        String view = clientController.deleteAllClients();

        assertEquals("redirect:/client?partialError=" + notDeletedCount, view);
        verify(clientService).deleteAllExceptWithDeals();
    }

    @Test
    void listClients_NoSorting() {
        Model model = mock(Model.class);
        List<Client> clients = List.of(new Client(), new Client());
        when(clientService.findAll()).thenReturn(clients);

        String view = clientController.listClients(model, null, null);

        assertEquals("client/client_list", view);
        verify(clientService).findAll();
        verify(model).addAttribute("clients", clients);
        verify(model).addAttribute("sortField", null);
        verify(model).addAttribute("sortDir", null);
    }

    @Test
    void listClients_WithSorting() {
        Model model = mock(Model.class);
        String sortField = "name";
        String sortDir = "desc";
        List<Client> clients = List.of(new Client(), new Client());
        when(clientService.findAllSortedByField(sortField, sortDir)).thenReturn(clients);

        String view = clientController.listClients(model, sortField, sortDir);

        assertEquals("client/client_list", view);
        verify(clientService).findAllSortedByField(sortField, sortDir);
        verify(model).addAttribute("clients", clients);
        verify(model).addAttribute("sortField", sortField);
        verify(model).addAttribute("sortDir", sortDir);
    }
}
