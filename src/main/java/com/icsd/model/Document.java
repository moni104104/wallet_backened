package com.icsd.model;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.icsd.model.DocumentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Document {
@Id
@SequenceGenerator(name="sequence",sequenceName="docIdSeq", allocationSize=1)
@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence")
 private int documentId;

	private int customerId;

	@Enumerated(EnumType.STRING)
	private DocumentType documentType;
	
	private LocalDate createdAt;
	
	private String createdBy;
	
	private String status;
	private String fileType;
	private double fileSize;
	private String filePath;
	
	
	
}
