package com.icsd.service;

import java.io.IOException;
import java.util.Optional;

import com.icsd.dto.request.DocumentRequestDTO;
import com.icsd.model.Document;
import com.icsd.model.DocumentType;

import jakarta.validation.Valid;

public interface DocumentService {


	Document saveDocument(@Valid DocumentRequestDTO dr) throws IOException;

	Optional<Document> getDocumentByDocumentId(int documentId);

	Optional<Document> getDocumentsByCustomerId(int customerId);

	Optional<Document> getDocumentsByCustomerIdAndDocumentType(int customerId,DocumentType documentType);

	void deleteDocumentByCustomerIdAndDocumentType(int customerId, DocumentType documentType);

	Optional<Document> downloadDocumentByDocumentId(int documentId);


}
