package com.gitech.bank.service;

import com.gitech.bank.dto.AccountDTO;
import com.gitech.bank.dto.ClientDTO;
import com.gitech.bank.entity.Account;
import com.gitech.bank.entity.Client;
import com.gitech.bank.repository.AccountRepository;
import com.gitech.bank.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    public BankService(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    public Page<ClientDTO> getClients(String query, Pageable pageable) {
        Page<Client> clients = (query != null && !query.isBlank())
                ? clientRepository.findByNomContainingIgnoreCase(query, pageable)
                : clientRepository.findAll(pageable);
        return clients.map(this::toClientDTO);
    }

    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id)
                .map(this::toClientDTO)
                .orElseThrow(() -> new RuntimeException("Client introuvable id: " + id));
    }

    public ClientDTO saveClient(ClientDTO dto) {
        Client client = Client.builder()
                .nom(dto.getNom())
                .id(dto.getId())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .statut(dto.getStatut())
                .build();
        Client saved = clientRepository.save(client);
        return toClientDTO(saved);
    }

    public ClientDTO updateClient(Long id, ClientDTO dto) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable id: " + id));
        existing.setNom(dto.getNom());
        existing.setEmail(dto.getEmail());
        existing.setTelephone(dto.getTelephone());
        existing.setStatut(dto.getStatut());
        return toClientDTO(clientRepository.save(existing));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public Page<AccountDTO> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(this::toAccountDTO);
    }

    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId).stream()
                .map(this::toAccountDTO)
                .toList();
    }

    private ClientDTO toClientDTO(Client c) {
        return new ClientDTO(c.getId(), c.getNom(), c.getEmail(), c.getTelephone(), c.getStatut());
    }

    private AccountDTO toAccountDTO(Account a) {
        return new AccountDTO(a.getId(), a.getClient() != null ? a.getClient().getId() : null, a.getAccountNumber(), a.getBalance(), a.getType());
    }
}
