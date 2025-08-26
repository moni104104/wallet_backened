package com.icsd.dto.request;

import java.io.File;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.icsd.model.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequestDTO {

	
private int customerId;

private DocumentType documentType;

private String createdBy;

private LocalDate createdAt= LocalDate.now();

private String status;

private MultipartFile image;
}
