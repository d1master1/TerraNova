package org.example.terranova.controller;

import org.example.terranova.dto.UserDTO;
import org.example.terranova.impl.RealtyServiceImpl;
import org.example.terranova.model.Realty;
import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private RealtyServiceImpl realtyService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Инициализируем контроллер через конструктор с UserService
        userController = new UserController(userService);

        // Инжектируем realtyService в приватное поле через Reflection,
        // т.к. поле private и не инициализируется конструктором
        Field realtyServiceField = UserController.class.getDeclaredField("realtyService");
        realtyServiceField.setAccessible(true);
        realtyServiceField.set(userController, realtyService);
    }

    @Test
    void updateUser_shouldUpdateRolesAndRedirect() {
        User user = new User();
        user.setId(1L);
        List<String> roles = List.of("ADMIN", "USER");

        String view = userController.updateUser(user, roles);

        // Используем ArgumentCaptor с правильным дженериком
        ArgumentCaptor<Set<Role>> captor = ArgumentCaptor.forClass(Set.class);
        verify(userService).updateUserRoles(eq(1L), captor.capture());

        Set<Role> rolesSet = captor.getValue();
        assertTrue(rolesSet.contains(Role.ADMIN));
        assertTrue(rolesSet.contains(Role.USER));
        assertEquals("redirect:/user", view);
    }

    @Test
    void listUsers_shouldReturnUserListViewWithUsers() {
        List<User> users = List.of(new User(), new User());
        when(userService.findAll()).thenReturn(users);

        String view = userController.listUsers(model, null);

        verify(model).addAttribute("users", users);
        verify(model).addAttribute("sort", null);
        assertEquals("user/user_list", view);
    }

    @Test
    void showCreateForm_shouldPrepareModelAndReturnFormView() {
        String view = userController.showCreateForm(model);

        verify(model).addAttribute(eq("user"), any(UserDTO.class));
        verify(model).addAttribute("allRoles", Role.values());
        assertEquals("user/user_form", view);
    }

    @Test
    void editUserForm_shouldLoadUserAndReturnForm() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);

        String view = userController.editUserForm(1L, model);

        verify(model).addAttribute(eq("user"), any(UserDTO.class));
        verify(model).addAttribute("allRoles", Role.values());
        assertEquals("user/user_form", view);
    }

    @Test
    void saveUser_shouldSaveAndRedirectIfNoErrors() {
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = userController.saveUser(userDTO, bindingResult, model);

        // Явно указываем save(UserDTO), чтобы
    }
}