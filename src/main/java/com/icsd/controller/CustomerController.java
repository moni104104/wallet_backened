package com.icsd.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Customer;
import com.icsd.service.CustomerService;
import com.icsd.service.PdfService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PdfService pdfService;



    @PostMapping(value = "/customer")
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequest) {
        log.info("inside create method of customer controller");

        Integer generatedCustomerId = customerService.createCustomer(customerRequest);

        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE, generatedCustomerId);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

    }




    @GetMapping("customer/{customerId}")
	public ResponseEntity<ApiResponse> getCustomerByCustomerId(@PathVariable("customerId") int customerId) {
		log.info("inside get method of customer controller");
		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		if (!cust.isPresent()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		log.info("customer found for given customer id");
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, cust);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);

	}

	@GetMapping("customer/pdf/{customerId}")
	public ResponseEntity<byte[]> getCustomerPdf(@PathVariable("customerId") int customerId) throws IOException {
		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		if (!cust.isPresent()) {
			log.info("customer not found");
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		ByteArrayInputStream bis = pdfService.generateCustomerPdf(customerId);
		log.info("customer found for given customer id");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename= customer_" + customerId + ".pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());
	}


	
	@DeleteMapping("/customer/{customerId}")
	public ResponseEntity<ApiResponse> deleteCustomerByCustomerId(@PathVariable("customerId") int customerId) {
		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		if (!cust.isPresent()) {
			log.info("customer not found");
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		customerService.deleteCustomerByCustomerId(customerId);
		ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_DELETE);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}
	
	

	@PutMapping("/customer/{customerId}")
	public ResponseEntity<ApiResponse> updateCustomerByCustomerId(@PathVariable("customerId") int customerId,
			@RequestBody CustomerRequestDto customerRequest) {
		log.info("inside PUt method of customer controller");

		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		if (!cust.isPresent()) {
			log.info("customer not found");
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		Customer updatedCustomer = customerService.updateCustomerByCustomerId(customerId, customerRequest);
		ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_UPDATE, updatedCustomer);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/customer/isValid")
	public ResponseEntity<ApiResponse> isValidUser(@RequestBody @Valid CustomerLoginDTO customerLogin) {
		log.info("authenticating user - valid or not ");
		System.out.println(customerLogin);
		Customer customer = customerService.isValidCustByEmailidAndPwd(customerLogin);
		ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_USER, customer);

		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}

	@GetMapping("/customer/email/{emailId}")
	public ResponseEntity<ApiResponse> getCustomerDataByEmailId(@PathVariable("emailId") String emailId) {
		Optional<Customer> cust = customerService.getCustomerDataByEmailId(emailId);
		if (!cust.isPresent()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		log.info("customer found for given email id");
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, cust);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/lastName/{lastName}")
	public ResponseEntity<ApiResponse> findByLastNameDTOResponse(@PathVariable("lastName") String lnm) {
		log.info("inside controller findByLastName lnm=" + lnm);
		List<CustomerFnmLnmGenderDTO> list = customerService.findByLastName(lnm);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameIgnore/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameIgnoreCase(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameIgnoreCase fn=" + fn);
		List<Customer> list = customerService.findByFirstNameIgnoreCase(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameLike/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameLike(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameLike fn=" + fn);
		List<Customer> list = customerService.findByFirstNameLike(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameContaining/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameContaining(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameContaining fn=" + fn);
		List<Customer> list = customerService.findByFirstNameContaining(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameContains/{firstName}")
	public ResponseEntity<ApiResponse> findByfirstNameContains(@PathVariable("firstName") String fn) {
		log.info("inside controller findByfirstNameContains fn=" + fn);
		List<Customer> list = customerService.findByfirstNameContains(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameIsContaining/{firstName}")
	public ResponseEntity<ApiResponse> findByfirstNameIsContaining(@PathVariable("firstName") String fn) {
		log.info("inside controller findByfirstNameIsContaining fn=" + fn);
		List<Customer> list = customerService.findByfirstNameIsContaining(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	
	
	
	
	

@GetMapping("/customers")
	public Page<Customer> getCustomers(@RequestParam(name="pageNo",defaultValue = "0",required = false) int pageNo,
			@RequestParam(name="pageSize",defaultValue = "2",required = false) int pageSize, @RequestParam(name="sortBy",defaultValue = "customerId",required = false) String sortBy,
			@RequestParam(defaultValue = "asc", name ="sortDir",required = false) String sortDir, @RequestParam(name="keyword", required = false) String keyword) {

		Sort sort= sortDir.equalsIgnoreCase("desc")? Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
		
		Pageable pageable= PageRequest.of(pageNo, pageSize, sort);
		
		if(keyword!=null && !keyword.isEmpty()) {
			return customerService.searchCustomersByFirstNameAndEmailId(keyword,keyword,pageable);
		}
		else {
			return customerService.getAllCustomer(pageable);
		}
		
	}

}
