package com.icsd.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icsd.model.Document;
import com.icsd.model.DocumentType;

public interface DocumentRepo extends JpaRepository<Document,Integer>{

	Optional<Document> findByCustomerId(int customerId);

	Optional<Document> findByCustomerIdAndDocumentType(int customerId,DocumentType documentType);

//	Optional<Document> deleteByCustomerIdAndDocumentType(int customerId, DocumentType documentType);

}
