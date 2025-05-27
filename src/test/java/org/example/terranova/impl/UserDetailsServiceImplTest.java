package org.example.terranova.impl;

import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        user.setRoles(Set.of(Role.USER, Role.ADMIN));

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userService.findByUsername("missingUser")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missingUser"));

        assertEquals("Пользователь не найден: missingUser", ex.getMessage());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserHasNoRoles() {
        User user = new User();
        user.setUsername("noroles");
        user.setPassword("encodedPass");
        user.setRoles(Set.of()); // пустой набор ролей

        when(userService.findByUsername("noroles")).thenReturn(Optional.of(user));

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noroles"));

        assertEquals("У пользователя нет назначенных ролей: noroles", ex.getMessage());
    }
}
