package org.example.terranova.impl;

import lombok.*;
import org.example.terranova.model.Client;
import org.example.terranova.repo.ClientRepo;
import org.example.terranova.repo.DealRepo;
import org.example.terranova.service.ClientService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepository;
    private final DealRepo dealRepository;
    private static final List<String> ALLOWED_FIELDS = List.of("name", "surname", "passport", "phone", "ownertype", "company");

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> findAllSortedByField(String fieldName, String sortDirection) {
        if (fieldName == null || !ALLOWED_FIELDS.contains(fieldName.toLowerCase())) {
            throw new IllegalArgumentException("Недопустимое поле для сортировки: " + fieldName);
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимое направление сортировки: " + sortDirection, e);
        }

        String actualFieldName = switch (fieldName.toLowerCase()) {
            case "name" -> "name";
            case "surname" -> "surname";
            case "passport" -> "passport";
            case "phone" -> "phone";
            case "ownertype" -> "ownerType";
            case "company" -> "company";
            default -> fieldName;
        };

        return clientRepository.findAll(Sort.by(direction, actualFieldName));
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        clientRepository.deleteAll();
    }

    @Override
    public boolean deleteByIdIfPossible(Long id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (!dealRepository.existsByClient(client)) {
                clientRepository.delete(client);
                return true;
            }
        }
        return false;
    }

    @Override
    public int deleteAllExceptWithDeals() {
        List<Client> allClients = clientRepository.findAll();
        int notDeletedCount = 0;
        for (Client client : allClients) {
            if (!dealRepository.existsByClient(client)) {
                clientRepository.delete(client);
            } else {
                notDeletedCount++;
            }
        }
        return notDeletedCount;
    }

}