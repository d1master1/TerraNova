package org.example.terranova.controller;

import jakarta.servlet.ServletContext;
import lombok.Setter;
import org.example.terranova.model.Deal;
import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.service.DealService;
import org.example.terranova.service.RealtyService;
import org.example.terranova.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private Environment env;  // Правильный импорт!

    @Autowired
    private final UserService userService;
    private final RealtyService realtyService;
    private final DealService dealService;
    @Autowired
    private ServletContext servletContext;

    // Значение можно получить из application.properties (fallback — "uploads")
    @Setter
    @Value("${upload.path:uploads}")
    private String uploadPath;

    public ProfileController(UserService userService, RealtyService realtyService, DealService dealService) {
        this.userService = userService;
        this.realtyService = realtyService;
        this.dealService = dealService;
    }

    @GetMapping("/profile")
    public String userProfile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<Realty> userRealties = realtyService.findAllByOwner(user);
        List<Deal> userDeals = dealService.findAllByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("realtyList", userRealties);
        model.addAttribute("dealList", userDeals);

        return "include/profile";
    }

    @PostMapping("/profile/avatar")
    public String handleAvatarUpload(@RequestParam("avatar") MultipartFile avatar, Principal principal) throws IOException {
        if (avatar != null && !avatar.isEmpty()) {
            String relativePath = uploadPath; // например, "/uploads" или "/terranova/uploads"
            String realPath = servletContext.getRealPath(relativePath);
            if (realPath == null) {
                throw new IOException("Не удалось определить реальный путь для " + relativePath);
            }

            File uploadDir = new File(realPath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Не удалось создать директорию для загрузки файлов: " + realPath);
            }

            String filename = System.currentTimeMillis() + "_" + principal.getName() + ".jpg";
            File file = new File(uploadDir, filename);
            avatar.transferTo(file);

            Optional<User> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setAvatarUrl(relativePath + "/" + filename); // сохраняем относительный URL
                userService.save(user);
            } else {
                throw new IOException("Пользователь не найден: " + principal.getName());
            }
        }
        return "redirect:/profile";
    }
}
