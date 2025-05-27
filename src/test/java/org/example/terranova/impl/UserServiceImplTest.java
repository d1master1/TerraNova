package org.example.terranova.impl;

import org.example.terranova.dto.UserDTO;
import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldSaveUser_whenUsernameAvailable() {
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");
        dto.setPassword("password");
        dto.setName("Test");
        dto.setSurname("User");

        when(userRepo.existsByUsername("testuser")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.save(dto);

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Test", savedUser.getName());
        assertEquals("User", savedUser.getSurname());
        assertTrue(savedUser.getRoles().contains(Role.USER));
        verify(userRepo).save(any(User.class));
    }

    @Test
    void save_shouldThrow_whenUsernameNotAvailable() {
        UserDTO dto = new UserDTO();
        dto.setUsername("testuser");

        when(userRepo.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.save(dto));
        assertEquals("Пользователь с таким именем уже существует", ex.getMessage());
    }

    @Test
    void isUsernameAvailable_shouldReturnTrue_whenUsernameDoesNotExist() {
        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        assertTrue(userService.isUsernameAvailable("newuser"));
    }

    @Test
    void isUsernameAvailable_shouldReturnFalse_whenUsernameExists() {
        when(userRepo.existsByUsername("existinguser")).thenReturn(true);
        assertFalse(userService.isUsernameAvailable("existinguser"));
    }

    @Test
    void findById_shouldReturnUser_whenFound() {
        User user = new User();
        user.setId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.findById(1L);
        assertEquals(user, result);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findById(1L));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void deleteUser_shouldCallDeleteById() {
        userService.deleteUser(2L);
        verify(userRepo).deleteById(2L);
    }

    @Test
    void updateUserRoles_shouldUpdateAndSaveRoles() {
        User user = new User();
        user.setId(1L);
        user.setRoles(Set.of(Role.USER));

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        userService.updateUserRoles(1L, Set.of(Role.ADMIN));

        assertTrue(user.getRoles().contains(Role.ADMIN));
        verify(userRepo).save(user);
    }

    @Test
    void updateUserRoles_shouldThrow_whenUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUserRoles(1L, Set.of(Role.ADMIN)));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void findByUsername_shouldReturnOptionalUser() {
        User user = new User();
        when(userRepo.findByUsername("test")).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByUsername("test");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void saveAvatarFile_shouldReturnFilename_whenValidImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.getInputStream()).thenReturn(InputStream.nullInputStream());

        String filename = userService.saveAvatarFile(file);

        assertNotNull(filename);
        assertTrue(filename.endsWith("_avatar.png"));
    }

    @Test
    void saveAvatarFile_shouldThrow_whenInvalidContentType() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text/plain");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.saveAvatarFile(file));
        assertEquals("Можно загружать только изображения.", ex.getMessage());
    }

}
