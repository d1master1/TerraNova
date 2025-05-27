package org.example.terranova.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.terranova.model.Client;
import org.example.terranova.model.Deal;
import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.service.ClientService;
import org.example.terranova.service.DealService;
import org.example.terranova.service.EmployeeService;
import org.example.terranova.service.RealtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealControllerTest {

    private DealService dealService;
    private ClientService clientService;
    private RealtyService realtyService;
    private EmployeeService employeeService;
    private DealController dealController;

    private Model model;

    @BeforeEach
    void setUp() {
        dealService = mock(DealService.class);
        clientService = mock(ClientService.class);
        realtyService = mock(RealtyService.class);
        employeeService = mock(EmployeeService.class);
        model = mock(Model.class);

        dealController = new DealController(dealService, clientService, realtyService, employeeService);
    }

    @Test
    void listDeals_NoSortParams_ReturnsDealListView() {
        List<Deal> deals = new ArrayList<>();
        when(dealService.getAllDeals(Sort.unsorted())).thenReturn(deals);

        String view = dealController.listDeals(model, null, null);

        assertEquals("deal/deal_list", view);
        verify(dealService).getAllDeals(Sort.unsorted());
        verify(model).addAttribute("deals", deals);
        verify(model).addAttribute("sortField", null);
        verify(model).addAttribute("sortDir", null);
        verify(model).addAttribute("reverseSortDir", "asc");  // изменено с "desc" на "asc"
    }

    @Test
    void listDeals_WithSortParams_ReturnsSortedDealListView() {
        List<Deal> deals = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "date");
        when(dealService.getAllDeals(sort)).thenReturn(deals);

        String view = dealController.listDeals(model, "date", "asc");

        assertEquals("deal/deal_list", view);
        verify(dealService).getAllDeals(sort);
        verify(model).addAttribute("deals", deals);
        verify(model).addAttribute("sortField", "date");
        verify(model).addAttribute("sortDir", "asc");
        verify(model).addAttribute("reverseSortDir", "desc");
    }

    @Test
    void saveDeal_CallsServiceAndRedirects() {
        Deal deal = new Deal();

        String view = dealController.saveDeal(deal);

        verify(dealService).saveDeal(deal);
        assertEquals("redirect:/deal", view);
    }

    @Test
    void showDealForm_WithId_FillsModelAndReturnsForm() throws JsonProcessingException {
        Long id = 1L;
        Deal deal = new Deal();
        List<Realty> realties = List.of(new Realty());
        List<Client> clients = List.of(new Client());
        List<Employee> employees = List.of(new Employee());

        when(dealService.findById(id)).thenReturn(Optional.of(deal));
        when(realtyService.findAll()).thenReturn(realties);
        when(clientService.findAll()).thenReturn(clients);
        when(employeeService.findAll()).thenReturn(employees);

        String view = dealController.showDealForm(id, model);

        assertEquals("deal/deal_form", view);

        verify(model).addAttribute("deal", deal);
        verify(model).addAttribute("realties", realties);
        verify(model).addAttribute("clients", clients);
        verify(model).addAttribute("employees", employees);
        verify(model).addAttribute(startsWith("realtyJson"), any());
        verify(model).addAttribute(startsWith("clientJson"), any());
    }


    @Test
    void showDealForm_WithoutId_FillsModelAndReturnsForm() throws JsonProcessingException {
        List<Realty> realties = List.of(new Realty());
        List<Client> clients = List.of(new Client());
        List<Employee> employees = List.of(new Employee());

        when(realtyService.findAll()).thenReturn(realties);
        when(clientService.findAll()).thenReturn(clients);
        when(employeeService.findAll()).thenReturn(employees);

        String view = dealController.showDealForm(null, model);

        assertEquals("deal/deal_form", view);

        // Используем any(Deal.class), так как объект создается внутри метода контроллера
        verify(model).addAttribute(eq("deal"), any(Deal.class));
        verify(model).addAttribute("realties", realties);
        verify(model).addAttribute("clients", clients);
        verify(model).addAttribute("employees", employees);
        verify(model).addAttribute(startsWith("realtyJson"), any());
        verify(model).addAttribute(startsWith("clientJson"), any());
    }

}