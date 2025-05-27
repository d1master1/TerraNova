package org.example.terranova.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.terranova.dto.UserDTO;
import org.example.terranova.impl.RealtyServiceImpl;
import org.example.terranova.model.Realty;
import org.example.terranova.model.Role;
import org.example.terranova.model.User;
import org.example.terranova.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private RealtyServiceImpl realtyService;

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User userForm,
                             @RequestParam(value = "roles", required = false) List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                try {
                    roles.add(Role.valueOf(roleName));
                } catch (IllegalArgumentException e) {
                    // логирование
                }
            }
        }
        userService.updateUserRoles(userForm.getId(), roles);
        return "redirect:/user";
    }
    
    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(name = "sort", required = false) String sort) {
        List<User> users = switch (sort != null ? sort.toLowerCase() : "") {
            case "username_asc" -> userService.findAllOrderByUsernameAsc();
            case "username_desc" -> userService.findAllOrderByUsernameDesc();
            case "name_asc" -> userService.findAllOrderByNameAsc();
            case "name_desc" -> userService.findAllOrderByNameDesc();
            default -> userService.findAll();
        };
        model.addAttribute("users", users);
        model.addAttribute("sort", sort);
        return "user/user_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("allRoles", Role.values());
        return "user/user_form";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        UserDTO userDTO = UserDTO.fromEntity(user);
        model.addAttribute("user", userDTO);
        model.addAttribute("allRoles", Role.values());
        return "user/user_form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", Role.values());
            return "user/user_form";
        }
        userService.save(userDTO);
        return "redirect:/user";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/user";
    }

    @GetMapping("/realty")
    public String userRealty(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = (User) userDetails;
        List<Realty> realties = realtyService.findAllByOwner(currentUser);
        model.addAttribute("realties", realties);
        return "profile/realty_list";
    }

    @GetMapping("/users")
    public String getAllUsers(@RequestParam(name = "sort", required = false) String sort, Model model) {
        List<User> users;

        switch (sort != null ? sort : "") {
            case "username_asc":
                users = userService.findAllOrderByUsernameAsc();
                break;
            case "username_desc":
                users = userService.findAllOrderByUsernameDesc();
                break;
            case "name_asc":
                users = userService.findAllOrderByNameAsc();
                break;
            case "name_desc":
                users = userService.findAllOrderByNameDesc();
                break;
            default:
                users = userService.findAll();
        }

        model.addAttribute("user", users);
        model.addAttribute("sort", sort);
        return "users/user_list";
    }

}