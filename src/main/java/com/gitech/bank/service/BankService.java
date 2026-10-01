package com.gitech.bank.service;

import com.gitech.bank.dto.AccountDTO;
import com.gitech.bank.dto.ClientDTO;
import com.gitech.bank.dto.PageResponse;
import com.gitech.bank.entity.Account;
import com.gitech.bank.entity.Client;
import com.gitech.bank.repository.AccountRepository;
import com.gitech.bank.repository.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Value("${file.upload-dir:./uploads/photos}")
    private String uploadDir;

    private final Path fileStorageLocation = Paths.get("./uploads/pdf").toAbsolutePath().normalize();

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le dossier", ex);
        }
    }

    public BankService(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    public PageResponse<ClientDTO> getClients(String query, Pageable pageable) {
        Page<Client> clients = (query != null && !query.isBlank())
                ? clientRepository.findByNomContainingIgnoreCase(query, pageable)
                : clientRepository.findAll(pageable);

        return PageResponse.from(clients.map(this::toClientDTO));
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

    public Client uploadClientPhoto(Long clientId, MultipartFile file) throws IOException {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID : " + clientId));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier envoyé est vide.");
        }

        // Création du dossier s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Génération d'un nom de fichier unique avec extension
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // Sauvegarde du fichier dans le répertoire cible
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Mise à jour de l'entité Client
        client.setPhotoUrl(fileName);
        return clientRepository.save(client);
    }



    public String storeFile(MultipartFile file) {
        // Nom de fichier unique pour éviter les collisions
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString(); // Retourne le chemin du fichier
        } catch (IOException ex) {
            throw new RuntimeException("Impossible d'enregistrer le fichier " + fileName, ex);
        }
    }

    private ClientDTO toClientDTO(Client c) {
        return new ClientDTO(c.getId(), c.getNom(), c.getEmail(), c.getTelephone(), c.getStatut());
    }

    private AccountDTO toAccountDTO(Account a) {
        return new AccountDTO(a.getId(), a.getClient() != null ? a.getClient().getId() : null, a.getAccountNumber(), a.getBalance(), a.getType());
    }
}
