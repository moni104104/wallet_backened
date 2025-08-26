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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	
	@Id
	@SequenceGenerator(name="generator",sequenceName="accidSeq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="generator")
	private int accountNumber;
	
	@ManyToOne
	@JoinColumn(name="customerFk")
	private Customer customer;
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	private double openingBalance;
	private LocalDate   openingDate=LocalDate.now();//openingDate
	private String description;
	
	
	
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", customer=" +" accountType=" + accountType
				+ ", openingBalance=" + openingBalance + ", openingDate=" + openingDate + ", description=" + description
				+ "]";
	}
	
	

}
