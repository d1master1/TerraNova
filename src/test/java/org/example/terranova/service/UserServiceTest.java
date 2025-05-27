package org.example.terranova.service;

import org.example.terranova.dto.UserDTO;
import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.repo.UserRepo;
import org.example.terranova.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveUser_WhenUsernameAvailable() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setName("Name");
        userDTO.setSurname("Surname");

        when(userRepo.existsByUsername("testuser")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.save(userDTO);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Name", savedUser.getName());
        assertEquals("Surname", savedUser.getSurname());
        assertTrue(savedUser.getRoles().contains(Role.USER));
        verify(userRepo).save(any(User.class));
    }

    @Test
    void save_ShouldThrow_WhenUsernameNotAvailable() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existinguser");

        when(userRepo.existsByUsername("existinguser")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.save(userDTO));
        assertEquals("Пользователь с таким именем уже существует", ex.getMessage());
    }

    @Test
    void isUsernameAvailable_ShouldReturnTrue_WhenNotExists() {
        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        assertTrue(userService.isUsernameAvailable("newuser"));
    }

    @Test
    void isUsernameAvailable_ShouldReturnFalse_WhenExists() {
        when(userRepo.existsByUsername("existinguser")).thenReturn(true);
        assertFalse(userService.isUsernameAvailable("existinguser"));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.findById(1L));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void deleteUser_ShouldCallDeleteById() {
        Long userId = 1L;
        userService.deleteUser(userId);
        verify(userRepo).deleteById(userId);
    }

    @Test
    void findAll_ShouldReturnList() {
        List<User> users = List.of(new User(), new User());
        when(userRepo.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAllOrderByUsernameAsc_ShouldReturnSortedList() {
        List<User> users = List.of(new User());
        when(userRepo.findAll(Sort.by(Sort.Direction.ASC, "username"))).thenReturn(users);

        List<User> result = userService.findAllOrderByUsernameAsc();

        assertEquals(users, result);
    }

    @Test
    void findAllOrderByUsernameDesc_ShouldReturnSortedList() {
        List<User> users = List.of(new User());
        when(userRepo.findAll(Sort.by(Sort.Direction.DESC, "username"))).thenReturn(users);

        List<User> result = userService.findAllOrderByUsernameDesc();

        assertEquals(users, result);
    }

    @Test
    void findAllOrderByNameAsc_ShouldReturnSortedList() {
        List<User> users = List.of(new User());
        when(userRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(users);

        List<User> result = userService.findAllOrderByNameAsc();

        assertEquals(users, result);
    }

    @Test
    void findAllOrderByNameDesc_ShouldReturnSortedList() {
        List<User> users = List.of(new User());
        when(userRepo.findAll(Sort.by(Sort.Direction.DESC, "name"))).thenReturn(users);

        List<User> result = userService.findAllOrderByNameDesc();

        assertEquals(users, result);
    }

    @Test
    void updateUserRoles_ShouldUpdateAndSaveRoles() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        Set<Role> newRoles = Set.of(Role.ADMIN);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);

        userService.updateUserRoles(1L, newRoles);

        assertEquals(newRoles, user.getRoles());
        verify(userRepo).save(user);
    }

    @Test
    void updateUserRoles_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUserRoles(1L, Set.of(Role.ADMIN)));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void findByUsername_ShouldReturnOptionalUser() {
        User user = new User();
        user.setUsername("user");
        when(userRepo.findByUsername("user")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("user");

        assertTrue(result.isPresent());
        assertEquals("user", result.get().getUsername());
    }

    @Test
    void saveUserEntity_ShouldSaveUser() {
        User user = new User();
        when(userRepo.save(user)).thenReturn(user);

        User saved = userService.save(user);

        assertEquals(user, saved);
    }

    @Test
    void saveAvatarFile_ShouldThrow_WhenNotImage() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.saveAvatarFile(file));
        assertEquals("Можно загружать только изображения.", ex.getMessage());
    }
}
