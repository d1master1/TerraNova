package org.example.terranova.impl;

import lombok.*;
import org.example.terranova.dto.UserDTO;
import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.repo.UserRepo;
import org.example.terranova.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User save(UserDTO userDTO) {
        if (!isUsernameAvailable(userDTO.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setRoles(Collections.singleton(Role.USER));
        return userRepo.save(user);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepo.existsByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public List<User> findAllOrderByUsernameAsc() {
        return userRepo.findAll(Sort.by(Sort.Direction.ASC, "username"));
    }

    @Override
    public List<User> findAllOrderByUsernameDesc() {
        return userRepo.findAll(Sort.by(Sort.Direction.DESC, "username"));
    }

    @Override
    public List<User> findAllOrderByNameAsc() {
        return userRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public List<User> findAllOrderByNameDesc() {
        return userRepo.findAll(Sort.by(Sort.Direction.DESC, "name"));
    }

    @Override
    public void updateUserRoles(Long userId, Set<Role> roles) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setRoles(roles);
        userRepo.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public String saveAvatar(User user, MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Можно загружать только изображения.");
            }

            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("src/main/resources/static/uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла", e);
        }
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public String saveAvatarFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Можно загружать только изображения.");
            }

            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String filename = UUID.randomUUID() + "_" + originalFilename;
            Path uploadPath = Paths.get("src/main/resources/static/uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла", e);
        }
    }

}
