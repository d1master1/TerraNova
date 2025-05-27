package org.example.terranova.controller;

import jakarta.servlet.ServletContext;
import org.example.terranova.model.Deal;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.service.DealService;
import org.example.terranova.service.RealtyService;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private UserService userService;

    @Mock
    private RealtyService realtyService;

    @Mock
    private DealService dealService;

    @Mock
    private ServletContext servletContext;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Установка uploadPath через сеттер
        profileController.setUploadPath("uploads");
    }

    @Test
    void userProfile_ReturnsProfileViewWithModelAttributes() {
        User user = new User();
        user.setUsername("testuser");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        List<Realty> realtyList = List.of(new Realty());
        List<Deal> dealList = List.of(new Deal());

        when(realtyService.findAllByOwner(user)).thenReturn(realtyList);
        when(dealService.findAllByUser(user)).thenReturn(dealList);

        String viewName = profileController.userProfile(principal, model);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("realtyList", realtyList);
        verify(model).addAttribute("dealList", dealList);
        assertEquals("include/profile", viewName);
    }
}
