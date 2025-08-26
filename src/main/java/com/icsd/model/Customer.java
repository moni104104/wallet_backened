package com.icsd.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer",  uniqueConstraints = {@UniqueConstraint(columnNames = "emailId") })

public class Customer 
{
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "custidSeq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")	
	int customerId;
	String firstName;
	String lastName;
	
	String emailId;
	String contactNo;
	
	@OneToOne
	@JoinColumn(name="addressFk")
	Address address;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Column(name="pwd")
	String password;
	String confirmPassword;
	private LocalDate registerationDate;
	
   private LocalDate expiryDate;
   
   @Enumerated(EnumType.STRING)
   private SubscriptionStatus status;
}
