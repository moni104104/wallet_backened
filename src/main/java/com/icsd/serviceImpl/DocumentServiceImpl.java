package com.icsd.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.DocumentRequestDTO;
import com.icsd.model.Document;
import com.icsd.model.DocumentType;
import com.icsd.repo.DocumentRepo;
import com.icsd.service.DocumentService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {
@Autowired
DocumentRepo documentRepo;


public Document saveDocument(DocumentRequestDTO dto) throws IOException {
	log.info("inside service for uploading image");
    MultipartFile file = dto.getImage();
log.info("file received from dto");
    if (file.isEmpty()) {
    	log.info("file is empty");
        throw new IllegalArgumentException(messages.FILE_EMPTY);
    }

    long maxSize = 2 * 1024 * 1024; 
    if (file.getSize() > maxSize) {
    	log.info("File size must not exceed 2MB.");
        throw new IllegalArgumentException(messages.FILE_TOO_LARGE);
    }

    long minSize=20*1024;
    if (file.getSize()<minSize) {
    	log.info("File size must be greater than 20KB.");
        throw new IllegalArgumentException(messages.FILE_TOO_SMALL);
    }

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".jpg")) {
    	log.info("file name is no ending with .jpg"+originalFilename);
        throw new IllegalArgumentException(messages.INVALID_FILE_TYPE);
    }

    String uploadDir = messages.UPLOAD_DIR;
    File uploadDirectory = new File(uploadDir);
    if (!uploadDirectory.exists()) {
        uploadDirectory.mkdirs(); 
    }

    File destination = new File(uploadDirectory, originalFilename);

    try (FileOutputStream fos = new FileOutputStream(destination))
    {
        fos.write(file.getBytes());
        log.info("file written on destination");
    }

    Document document = Document.builder()
            .customerId(dto.getCustomerId())
            .documentType(dto.getDocumentType())
            .createdBy(dto.getCreatedBy())
            .createdAt(dto.getCreatedAt())
            .status(dto.getStatus())
            .fileType(file.getContentType())
            .fileSize(file.getSize() / 1024.0)
           .filePath(destination.getAbsolutePath())
            .build();
log.info("data converted from object to entity");
    return documentRepo.save(document);
}



	@Override
	public Optional<Document> getDocumentByDocumentId(int documentId) {
		log.info("inside serviceImpl get document by document id"+documentId);
		Optional<Document> docs=documentRepo.findById(documentId);
		return docs;
	}

	@Override
	public List<Document> getDocumentsByCustomerId(int customerId) {
		log.info("inside serviceImpl get document by customer id"+customerId);
		List<Document> listDocuments=documentRepo.findByCustomerId(customerId);
		return listDocuments;
	}

	@Override
	public Optional<Document> getDocumentsByCustomerIdAndDocumentType(int customerId,DocumentType documentType) {
		log.info("inside serviceImpl get document by customerId"+customerId+"documentType"+documentType);
		Optional<Document>docs=documentRepo.findByCustomerIdAndDocumentType(customerId,documentType);
		return docs;
	}


	@Transactional
	@Override
	public void deleteDocumentByCustomerIdAndDocumentType(int customerId, DocumentType documentType) {
		log.info("inside serviceImpl delete document");
		Optional<Document> docs=documentRepo.findByCustomerIdAndDocumentType(customerId,documentType);
		Document document=docs.get();
		documentRepo.delete(document);
	}



	@Override
	public Optional<Document> downloadDocumentByDocumentId(int documentId) {
		log.info("inside serviceImpl to download document ");
		Optional<Document> docs=documentRepo.findById(documentId);
		return docs;
	}
	
	

}
