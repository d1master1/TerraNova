package org.example.terranova.controller;

import org.example.terranova.model.Realty;
import org.example.terranova.model.User;
import org.example.terranova.service.ClientService;
import org.example.terranova.service.DealService;
import org.example.terranova.service.RealtyService;
import org.example.terranova.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/realty")
public class RealtyController {

    private final RealtyService realtyService;
    private final UserService userService;
    private final DealService dealService;
    private final ClientService clientService;

    public RealtyController(RealtyService realtyService, UserService userService, DealService dealService, ClientService clientService) {
        this.realtyService = realtyService;
        this.userService = userService;
        this.dealService = dealService;
        this.clientService = clientService;
    }

    @GetMapping
    public String listRealty(
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir,
            Model model) {

        List<Realty> realties = realtyService.findAll();

        if (sortField != null && sortDir != null) {
            Comparator<Realty> comparator = null;

            switch (sortField) {
                case "cost":
                    comparator = Comparator.comparing(Realty::getCost);
                    break;
                case "region":
                    comparator = Comparator.comparing(Realty::getRegion, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "street":
                    comparator = Comparator.comparing(Realty::getStreet, String.CASE_INSENSITIVE_ORDER);
                    break;
            }

            if (comparator != null) {
                if ("desc".equalsIgnoreCase(sortDir)) {
                    comparator = comparator.reversed();
                }
                realties.sort(comparator);
            }
        }

        model.addAttribute("realties", realties);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "realty/realty_list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("realty", new Realty());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("deals", dealService.findAll());
        model.addAttribute("clients", clientService.findAll());
        return "realty/realty_form";
    }

    @PostMapping("/save")
    public String saveRealty(@ModelAttribute Realty realty) {
        realtyService.saveRealty(realty);
        return "redirect:/realty";
    }

    @PostMapping("/delete/{id}")
    public String deleteRealty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (dealService.existsByRealtyId(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Невозможно удалить: недвижимость участвует в сделке.");
        } else {
            realtyService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Недвижимость успешно удалена.");
        }
        return "redirect:/realty";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Realty realty = realtyService.getRealtyById(id);
        if (realty == null) {
            throw new NoSuchElementException("Недвижимость с id " + id + " не найдена");
        }
        model.addAttribute("realty", realty);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("deals", dealService.findAll());
        model.addAttribute("clients", clientService.findAll());
        return "realty/realty_form";
    }

    @PostMapping("/delete")
    public String deleteAllExceptWithDeals(RedirectAttributes redirectAttributes) {
        List<Realty> deleted = realtyService.deleteAllExceptWithDeals();
        if (deleted.isEmpty()) {
            redirectAttributes.addFlashAttribute("warningMessage", "Удаление невозможно: все объекты участвуют в сделках.");
        } else {
            int deletedCount = deleted.size();
            redirectAttributes.addFlashAttribute("warningMessage",
                    "Были удалены все недвижимости, которые не находятся в сделках. Количество удалённых: " + deletedCount);
        }
        return "redirect:/realty";
    }

}
