package org.example.terranova.controller;

import org.example.terranova.dto.UserDTO;
import org.example.terranova.model.User;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showRegistrationForm_AddsUserDTOToModel_ReturnsView() {
        String view = registrationController.showRegistrationForm(model);

        verify(model).addAttribute(eq("userDTO"), any(UserDTO.class));
        assertEquals("include/registration", view);
    }

    @Test
    void registerUser_WithValidationErrors_ReturnsRegistrationView() {
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = registrationController.registerUser(userDTO, bindingResult, model);

        assertEquals("include/registration", view);
        verifyNoInteractions(userService);
    }

    @Test
    void registerUser_UsernameNotAvailable_ReturnsRegistrationViewWithError() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existingUser");
        userDTO.setPassword("password");
        userDTO.setConfirmPassword("password");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.isUsernameAvailable("existingUser")).thenReturn(false);

        String view = registrationController.registerUser(userDTO, bindingResult, model);

        assertEquals("include/registration", view);
        verify(model).addAttribute("usernameError", "Логин уже занят");
        verify(userService, never()).save((User) any());
    }

    @Test
    void registerUser_PasswordMismatch_ReturnsRegistrationViewWithError() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUser");
        userDTO.setPassword("password1");
        userDTO.setConfirmPassword("password2");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.isUsernameAvailable("newUser")).thenReturn(true);

        String view = registrationController.registerUser(userDTO, bindingResult, model);

        assertEquals("include/registration", view);
        verify(model).addAttribute("passwordMismatch", "Пароли не совпадают");
        verify(userService, never()).save((User) any());
    }

    @Test
    void registerUser_ValidData_SavesUserAndRedirects() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUser");
        userDTO.setPassword("password");
        userDTO.setConfirmPassword("password");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.isUsernameAvailable("newUser")).thenReturn(true);

        String view = registrationController.registerUser(userDTO, bindingResult, model);

        assertEquals("redirect:/include/login", view);
        verify(userService).save(userDTO);
    }
}
