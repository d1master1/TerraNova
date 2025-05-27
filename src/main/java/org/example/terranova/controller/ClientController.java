package org.example.terranova.controller;

import jakarta.validation.Valid;
import org.example.terranova.model.Client;
import org.example.terranova.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String listClients(Model model,
                              @RequestParam(required = false) String sortField,
                              @RequestParam(required = false) String sortDir) {

        List<Client> clients;
        String direction = (sortDir != null && sortDir.equalsIgnoreCase("desc")) ? "desc" : "asc";

        if (sortField == null || sortField.isBlank()) {
            clients = clientService.findAll();
            sortField = null;
            direction = null;
        } else {
            clients = clientService.findAllSortedByField(sortField.toLowerCase(), direction);
        }

        model.addAttribute("clients", clients);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", direction);

        return "client/client_list";
    }

    @GetMapping("/add")
    public String addClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "client/client_form";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isEmpty()) {
            return "redirect:/client";
        }
        model.addAttribute("client", clientOpt.get());
        return "client/client_form";
    }

    @PostMapping("/save")
    public String saveClient(@Valid @ModelAttribute("client") Client client,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "client/client_form";
        }
        clientService.save(client);
        return "redirect:/client";
    }

    @PostMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        boolean deleted = clientService.deleteByIdIfPossible(id);
        return deleted ? "redirect:/client" : "redirect:/client?error=true";
    }

    @PostMapping("/deleteAll")
    public String deleteAllClients() {
        int notDeletedCount = clientService.deleteAllExceptWithDeals();
        if (notDeletedCount > 0) {
            return "redirect:/client?partialError=" + notDeletedCount;
        }
        return "redirect:/client";
    }
}
