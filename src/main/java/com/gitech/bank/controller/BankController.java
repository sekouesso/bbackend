package com.gitech.bank.controller;

import com.gitech.bank.dto.AccountDTO;
import com.gitech.bank.dto.ClientDTO;
import com.gitech.bank.dto.PageResponse;
import com.gitech.bank.entity.Client;
import com.gitech.bank.service.BankService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api") //http://localhost:8080/api
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/clients")
    public ResponseEntity<PageResponse<ClientDTO>> getClients(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(bankService.getClients(search, pageable));
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(bankService.getClientById(id));
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO dto) {
        return new ResponseEntity<>(bankService.saveClient(dto), HttpStatus.CREATED);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO dto) {
        return ResponseEntity.ok(bankService.updateClient(id, dto));
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        bankService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<Page<AccountDTO>> getAccounts(Pageable pageable) {
        return ResponseEntity.ok(bankService.getAccounts(pageable));
    }

    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<List<AccountDTO>> getClientAccounts(@PathVariable Long clientId) {
        return ResponseEntity.ok(bankService.getAccountsByClientId(clientId));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Client updatedClient = bankService.uploadClientPhoto(id, file);
            return ResponseEntity.ok(updatedClient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        }
    }
}
