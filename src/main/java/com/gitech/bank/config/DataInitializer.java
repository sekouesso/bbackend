package com.gitech.bank.config;

import com.gitech.bank.entity.*;
import com.gitech.bank.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(ClientRepository clientRepo, AccountRepository accountRepo) {
        return args -> {
            Client c1 = Client.builder()
                    .nom("Jean Dupont")
                    .email("jean.dupont@example.com")
                    .telephone("+228 90 01 02 03")
                    .statut(StatutClient.ACTIF)
                    .build();

            Client c2 = Client.builder()
                    .nom("Marie Curie")
                    .email("marie.curie@example.com")
                    .telephone("+228 91 12 23 34")
                    .statut(StatutClient.ACTIF)
                    .build();

            Client c3 = Client.builder()
                    .nom("Thomas Pesquet")
                    .email("thomas.pesquet@example.com")
                    .telephone("+228 92 23 34 45")
                    .statut(StatutClient.INACTIF)
                    .build();

            Client c4 = Client.builder()
                    .nom("Sophie Marceau")
                    .email("sophie.marceau@example.com")
                    .telephone("+228 93 34 45 56")
                    .statut(StatutClient.ACTIF)
                    .build();

            Client c5 = Client.builder()
                    .nom("Lucas Bernard")
                    .email("lucas.bernard@example.com")
                    .telephone("+228 94 45 56 67")
                    .statut(StatutClient.ACTIF)
                    .build();

            clientRepo.saveAll(List.of(c1, c2, c3, c4, c5));

            accountRepo.save(Account.builder()
                    .accountNumber("FR1000000001")
                    .balance(new BigDecimal("2450.50"))
                    .type(TypeCompte.COURANT)
                    .client(c1)
                    .build());

            accountRepo.save(Account.builder()
                    .accountNumber("FR1000000002")
                    .balance(new BigDecimal("12000.00"))
                    .type(TypeCompte.EPARGNE)
                    .client(c1)
                    .build());

            accountRepo.save(Account.builder()
                    .accountNumber("FR2000000001")
                    .balance(new BigDecimal("850.75"))
                    .type(TypeCompte.COURANT)
                    .client(c2)
                    .build());

            accountRepo.save(Account.builder()
                    .accountNumber("FR3000000001")
                    .balance(new BigDecimal("5400.00"))
                    .type(TypeCompte.COURANT)
                    .client(c3)
                    .build());

            accountRepo.save(Account.builder()
                    .accountNumber("FR4000000001")
                    .balance(new BigDecimal("120.00"))
                    .type(TypeCompte.COURANT)
                    .client(c4)
                    .build());
        };
    }
}