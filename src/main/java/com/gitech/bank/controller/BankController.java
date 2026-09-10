package com.gitech.bank.controller;

import com.gitech.bank.dto.AccountDTO;
import com.gitech.bank.dto.ClientDTO;
import com.gitech.bank.service.BankService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/clients")
    public ResponseEntity<Page<ClientDTO>> getClients(
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
}
