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
public class Admin {

@Id	
@SequenceGenerator(name="sequence" , sequenceName="admin_seq" , allocationSize=1)
@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="generator")
private int adminId;

 String emailId;
 @Column(name="pwd")
 String password;



}
