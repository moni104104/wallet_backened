package com.icsd.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.DocumentRequestDTO;
import com.icsd.model.Customer;
import com.icsd.model.Document;
import com.icsd.model.DocumentType;
import com.icsd.repo.DocumentRepo;
import com.icsd.service.DocumentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DocumentController {
	
@Autowired
DocumentService documentService;
	@PostMapping("/document")
	public ResponseEntity<ApiResponse> uploadDocuments(@Valid @ModelAttribute DocumentRequestDTO dr) throws IOException{
	  log.info("inside document controller to save document");
		Document document = documentService.saveDocument(dr);
		ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(),messages.DOCUMENT_UPLOAD);
		return new ResponseEntity<ApiResponse> (apiResponse,HttpStatus.OK);
	}
	
		
	@GetMapping("/document/{documentId}")
	public ResponseEntity<ApiResponse>getDocumentByDocumentId(@PathVariable("documentId") int documentId){
		   log.info("inside document controller to find document by documentid");
		Optional<Document> docs=documentService.getDocumentByDocumentId(documentId);
		if(!docs.isPresent()) {
		   log.info("inside document controller--data not found for given document id"+documentId);
			ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.NO_DOCUMENT);
			return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);	
		}
		   log.info("inside document controller--data found for given document id"+documentId);
ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.DOCUMENT_FOUND);
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);
	}
	
	

 @GetMapping("/document/download/{documentId}")
 public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") int documentId) {
	 log.info("in document controller to download the document");
	 Optional <Document> docs = documentService.getDocumentByDocumentId(documentId);
     Document document =docs.get();
	 if (document.getFilePath() == null) {
		   log.info("file path is null");
	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

	        }
	 try {
	       Path filePath = Paths.get(document.getFilePath());
     log.info("file path of image is"+filePath);
	    if (!Files.exists(filePath)) {
	    	log.info("file path does not exist");
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	            }

	    Resource resource = new UrlResource(filePath.toUri());
	    log.info("inside document controller resource"+resource);
	     String contentType = Files.probeContentType(filePath);
		 log.info("content type of the file is"+contentType);

	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
	    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"") .body(resource);

	        } catch (Exception e) {
	            e.printStackTrace();
	            log.info("exception occur in controller");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
	

	
@GetMapping("/document/{customerId}/{documentType}")
	public ResponseEntity<ApiResponse>getDocumentsByCustomerIdAndDocumentType(@PathVariable("customerId") int customerId,@PathVariable("documentType") DocumentType documentType){
		log.info("customerId" +customerId+ "documentType in controller" +documentType );
		Optional<Document> docs=documentService.getDocumentsByCustomerIdAndDocumentType(customerId,documentType);
		if(!docs.isPresent()) {
         log.info("document not found for given customerid"+customerId+" and document type"+documentType);
			ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.NO_DOCUMENT);
			return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);	
		}
ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.DOCUMENT_FOUND, docs);
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);
	}
	
	
	@DeleteMapping("/document/{customerId}/{documentType}")
	public ResponseEntity<ApiResponse> deleteDocumentByCustomerIdAndDocumentType(@PathVariable("customerId") int customerId,@PathVariable("documentType") DocumentType documentType){
		log.info(" delete document in controller for given customerId" +customerId+ " and documentType" +documentType );
		documentService.deleteDocumentByCustomerIdAndDocumentType(customerId,documentType);
		log.info("document deleted successfully" );

		ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(),messages.DOCUMENT_FOUND_DELETE);
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);
	}
	
	
	@GetMapping("/documents/{customerId}")
	public ResponseEntity<ApiResponse>getDocumentsByCustomerId(@PathVariable("customerId") int customerId){
	Optional<Document> docs=documentService.getDocumentsByCustomerId(customerId);
		if(!docs.isPresent()) {
			ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.NO_DOCUMENT);
			return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);	
		}
		log.info("in controller-- docs found for given customerid"+customerId);
ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(),messages.DOCUMENT_FOUND);
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);
	}
	
	
	
	}

	



