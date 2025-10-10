package com.icsd.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Transaction {

    @Id
    @SequenceGenerator(name = "generator", sequenceName = "transidSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private int transactionId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private LocalDate transactionDate;
    private double amount;
    private String description;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "frmAccFk")
    private Account fromAccountNumber;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "toAccFk")
    private Account toAccountNumber;

    public Transaction(TransactionType transactionType, LocalDate transactionDate, double amount, String description,
                       Account fromAccountNumber, Account toAccountNumber) {
        super();
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.description = description;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
    }

}
