package com.icsd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Address {
@Id
@SequenceGenerator(initialValue=1, name="mySeq", sequenceName="mySeq" ,allocationSize=1)
@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="mySeq")
private int addressId;

private String addressLine1;
private String addressLine2;
private String city;
private String state;
@Column(length=6)
private String pincode;



}
