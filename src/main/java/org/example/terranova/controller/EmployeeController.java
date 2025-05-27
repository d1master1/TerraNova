package org.example.terranova.controller;

import jakarta.validation.Valid;
import org.example.terranova.model.Employee;
import org.example.terranova.service.EmployeeService;
import org.example.terranova.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    public EmployeeController(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "sortBy", required = false) String sortBy,
                                @RequestParam(value = "sortDir", required = false) String sortDir,
                                Model model) {
        List<Employee> employees;

        if (sortBy == null || sortBy.isEmpty()) {
            employees = employeeService.getAllEmployeesWithUsers();
        } else {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            employees = employeeService.getAllEmployeesSorted(Sort.by(direction, sortBy));
        }

        model.addAttribute("employees", employees);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "employee/employee_list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("users", userService.findAll());
        return "employee/employee_form";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            return "employee/employee_form";
        }

        try {
            employeeService.saveEmployee(employee);
            return "redirect:/employee";
        } catch (Exception e) {
            model.addAttribute("validationErrors", List.of("Ошибка при сохранении сотрудника: " + e.getMessage()));
            model.addAttribute("users", userService.findAll());
            return "employee/employee_form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return "redirect:/employee";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("users", userService.findAll());
        return "employee/employee_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
        } catch (IllegalStateException e) {
            redirectAttributes.addAttribute("error", true);
        }
        return "redirect:/employee";
    }

    @GetMapping("/deleteAll")
    public String deleteAllEmployees(RedirectAttributes redirectAttributes) {
        List<Employee> notDeleted = employeeService.deleteAllExceptWithDeals();

        if (!notDeleted.isEmpty()) {
            redirectAttributes.addAttribute("partialError", notDeleted.size());
        }

        return "redirect:/employee";
    }

    @GetMapping("/patronymic/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getPatronymicByUserId(@PathVariable Long userId) {
        Employee employee = employeeService.getEmployeeByUserId(userId);
        if (employee != null && employee.getPatronymic() != null) {
            return ResponseEntity.ok(Map.of("patronymic", employee.getPatronymic()));
        } else {
            return ResponseEntity.ok(Map.of("patronymic", ""));
        }
    }
}
