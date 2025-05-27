package org.example.terranova.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.terranova.model.Client;
import org.example.terranova.model.Deal;
import org.example.terranova.model.Employee;
import org.example.terranova.model.Realty;
import org.example.terranova.service.ClientService;
import org.example.terranova.service.DealService;
import org.example.terranova.service.EmployeeService;
import org.example.terranova.service.RealtyService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;
    private final ClientService clientService;
    private final RealtyService realtyService;
    private final EmployeeService employeeService;

    @GetMapping
    public String listDeals(
            Model model,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortDir", required = false) String sortDir
    ) {
        Sort sort;

        if (sortField == null || sortField.isEmpty()) {
            sort = Sort.unsorted();
        } else {
            try {
                // Защита от null и неправильных значений
                Sort.Direction direction = (sortDir == null || (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")))
                        ? Sort.Direction.ASC
                        : Sort.Direction.fromString(sortDir);
                sort = Sort.by(direction, sortField);
            } catch (IllegalArgumentException ex) {
                sort = Sort.by(Sort.Direction.ASC, "date");
                sortField = "date";
                sortDir = "asc";
            }
        }

        List<Deal> deals = dealService.getAllDeals(sort);

        model.addAttribute("deals", deals);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", (sortDir != null && sortDir.equalsIgnoreCase("asc")) ? "desc" : "asc");

        return "deal/deal_list";
    }

    @PostMapping("/save")
    public String saveDeal(@ModelAttribute Deal deal) {
        dealService.saveDeal(deal);
        return "redirect:/deal";
    }

    @GetMapping({"/add", "/edit/{id}"})
    public String showDealForm(@PathVariable(required = false) Long id, Model model) throws JsonProcessingException {
        Deal deal = (id != null) ? dealService.findById(id).orElse(new Deal()) : new Deal();

        List<Realty> realties = realtyService.findAll();
        List<Client> clients = clientService.findAll();
        List<Employee> employees = employeeService.findAll();

        model.addAttribute("deal", deal);
        model.addAttribute("realties", realties);
        model.addAttribute("clients", clients);
        model.addAttribute("employees", employees);

        ObjectMapper objectMapper = new ObjectMapper();

        // JSON-данные по недвижимости
        List<Map<String, Object>> realtyJsonList = realties.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("cost", r.getCost());
            map.put("ownerName", r.getOwner() != null ? r.getOwner().getFullName() : "—");
            return map;
        }).collect(Collectors.toList());

        // JSON-данные по клиентам
        List<Map<String, Object>> clientJsonList = clients.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("phone", c.getPhone());
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("realtyJson", objectMapper.writeValueAsString(realtyJsonList));
        model.addAttribute("clientJson", objectMapper.writeValueAsString(clientJsonList));

        return "deal/deal_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteDeal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dealService.deleteDealById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Сделка успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении сделки");
        }
        return "redirect:/deal";
    }

    @PostMapping("/deleteAll")
    public String deleteAllDeals(RedirectAttributes redirectAttributes) {
        try {
            dealService.deleteAllDeals();
            redirectAttributes.addFlashAttribute("successMessage", "Все сделки успешно удалены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении всех сделок");
        }
        return "redirect:/deal";
    }

}