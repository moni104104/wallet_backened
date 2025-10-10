package com.icsd.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer", uniqueConstraints = {@UniqueConstraint(columnNames = "emailId")})

public class Customer {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "custidSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    int customerId;
    String firstName;
    String lastName;

    String emailId;
    String contactNo;
    @OneToOne
    @JoinColumn(name = "addressFk")
    Address address;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "pwd")
    String password;
    @Transient
    String confirmPassword;
    private LocalDate registrationDate;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @JsonManagedReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

}
