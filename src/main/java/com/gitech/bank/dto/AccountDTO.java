package com.gitech.bank.dto;

import com.gitech.bank.entity.TypeCompte;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private Long id;
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private TypeCompte type;
}