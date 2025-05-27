package org.example.terranova.controller;

import org.example.terranova.model.Client;
import org.example.terranova.model.Deal;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.service.ClientService;
import org.example.terranova.service.DealService;
import org.example.terranova.service.RealtyService;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RealtyControllerTest {

    @InjectMocks
    private RealtyController realtyController;

    @Mock
    private RealtyService realtyService;

    @Mock
    private UserService userService;

    @Mock
    private DealService dealService;

    @Mock
    private ClientService clientService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listRealty_NoSorting_ReturnsViewWithRealtyList() {
        List<Realty> realties = List.of(new Realty(), new Realty());
        when(realtyService.findAll()).thenReturn(realties);

        String view = realtyController.listRealty(null, null, model);

        verify(realtyService).findAll();
        verify(model).addAttribute("realties", realties);
        verify(model).addAttribute("sortField", null);
        verify(model).addAttribute("sortDir", null);
        assertEquals("realty/realty_list", view);
    }

    @Test
    void listRealty_WithSorting_SortsCorrectlyAndReturnsView() {
        Realty r1 = new Realty();
        r1.setCost(200L);
        Realty r2 = new Realty();
        r2.setCost(100L);

        List<Realty> realties = new ArrayList<>(List.of(r1, r2));
        when(realtyService.findAll()).thenReturn(realties);

        String view = realtyController.listRealty("cost", "asc", model);

        verify(model).addAttribute(eq("realties"), argThat(list -> {
            List<Realty> l = (List<Realty>) list;
            return l.get(0).getCost() == 100L && l.get(1).getCost() == 200L;
        }));
        assertEquals("realty/realty_list", view);

        realties = new ArrayList<>(List.of(r1, r2));
        when(realtyService.findAll()).thenReturn(realties);

        view = realtyController.listRealty("cost", "desc", model);

        verify(model, atLeastOnce()).addAttribute(eq("realties"), argThat(list -> {
            List<Realty> l = (List<Realty>) list;
            return l.get(0).getCost() == 200L && l.get(1).getCost() == 100L;
        }));
        assertEquals("realty/realty_list", view);
    }

    @Test
    void showAddForm_AddsModelAttributesAndReturnsFormView() {
        List<User> users = List.of(new User());
        List<Deal> deals = List.of(new Deal());
        List<Client> clients = List.of(new Client());

        when(userService.findAll()).thenReturn(users);
        when(dealService.findAll()).thenReturn(deals);
        when(clientService.findAll()).thenReturn(clients);

        String view = realtyController.showAddForm(model);

        verify(model).addAttribute(eq("realty"), any(Realty.class));
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("deals", deals);
        verify(model).addAttribute("clients", clients);
        assertEquals("realty/realty_form", view);
    }

    @Test
    void saveRealty_CallsSaveAndRedirects() {
        Realty realty = new Realty();

        String redirect = realtyController.saveRealty(realty);

        verify(realtyService).saveRealty(realty);
        assertEquals("redirect:/realty", redirect);
    }

    @Test
    void deleteRealty_WithDeal_ShowsErrorMessage() {
        Long id = 1L;
        when(dealService.existsByRealtyId(id)).thenReturn(true);

        String redirect = realtyController.deleteRealty(id, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Невозможно удалить: недвижимость участвует в сделке.");
        verify(realtyService, never()).deleteById(any());
        assertEquals("redirect:/realty", redirect);
    }

    @Test
    void deleteRealty_WithoutDeal_DeletesSuccessfully() {
        Long id = 2L;
        when(dealService.existsByRealtyId(id)).thenReturn(false);

        String redirect = realtyController.deleteRealty(id, redirectAttributes);

        verify(realtyService).deleteById(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Недвижимость успешно удалена.");
        assertEquals("redirect:/realty", redirect);
    }

    @Test
    void deleteAllExceptWithDeals_WithDeletions_ShowsSuccessMessage() {
        List<Realty> deleted = List.of(new Realty(), new Realty());
        when(realtyService.deleteAllExceptWithDeals()).thenReturn(deleted);

        String redirect = realtyController.deleteAllExceptWithDeals(redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("warningMessage"),
                contains("Были удалены все недвижимости, которые не находятся в сделках"));
        assertEquals("redirect:/realty", redirect);
    }

    @Test
    void deleteAllExceptWithDeals_WithoutDeletions_ShowsWarning() {
        when(realtyService.deleteAllExceptWithDeals()).thenReturn(Collections.emptyList());

        String redirect = realtyController.deleteAllExceptWithDeals(redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("warningMessage",
                "Удаление невозможно: все объекты участвуют в сделках.");
        assertEquals("redirect:/realty", redirect);
    }

    @Test
    void showEditForm_ExistingRealty_AddsAttributesAndReturnsForm() {
        Long id = 1L;
        Realty realty = new Realty();

        List<User> users = List.of(new User());
        List<Deal> deals = List.of(new Deal());
        List<Client> clients = List.of(new Client());

        when(realtyService.getRealtyById(id)).thenReturn(realty);
        when(userService.findAll()).thenReturn(users);
        when(dealService.findAll()).thenReturn(deals);
        when(clientService.findAll()).thenReturn(clients);

        String view = realtyController.showEditForm(id, model);

        verify(model).addAttribute("realty", realty);
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("deals", deals);
        verify(model).addAttribute("clients", clients);
        assertEquals("realty/realty_form", view);
    }

    @Test
    void showEditForm_NonExistingRealty_ThrowsException() {
        Long id = 99L;

        when(realtyService.getRealtyById(id)).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> realtyController.showEditForm(id, model));

        assertEquals("Недвижимость с id 99 не найдена", exception.getMessage());
    }
}
