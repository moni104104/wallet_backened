package com.icsd.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @SequenceGenerator(name = "generator", sequenceName = "accidSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private int accountNumber;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "customerFk")
    private Customer customer;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private double openingBalance;
    private LocalDate openingDate = LocalDate.now();
    private String description;

    @JsonBackReference
    @OneToMany(mappedBy = "fromAccountNumber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> sentTransactions;

    @JsonBackReference
    @OneToMany(mappedBy = "toAccountNumber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> receivedTransactions;

    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", customer=" + " accountType=" + accountType
                + ", openingBalance=" + openingBalance + ", openingDate=" + openingDate + ", description=" + description
                + "]";
    }

}
