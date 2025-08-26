package com.icsd.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Transaction 
{

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
	@JoinColumn(name="frmAccFk")
	private Account fromAccountNumber;
	
	@ManyToOne
	@JoinColumn(name="toAccFk")
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
