package com.icsd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.service.AddressService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api")
@CrossOrigin("*")
public class AddressController {

	@Autowired
	private AddressService addressService;

//	
//	@GetMapping("address/{customerId}")
//	public ResponseEntity<ApiResponse> getAddressByCustomerId(@PathVariable("customerId") int customerId) {
//		log.info("inside get method of address controller");
//		Optional<Address> add = addressService.getAddressByCustomerId(customerId);
//		if (!add.isPresent()) {
//			log.info("no record found");
//			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.ADDRESS_NOT_FOUND);
//			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
//		}
//		log.info("address found for given customer id");
//		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ADDRESS_FOUND, add);
//
//		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
//
//	}

	@GetMapping("/address/notNull")
	public ResponseEntity<ApiResponse> findByAddressLine2IsNotNull() {
		log.info("inside address controller findByAddressLine2IsNull");

		ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.ADDRESS_NOT_NULL,
				addressService.findByAddressLine2IsNotNull());
		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}

	@GetMapping("/address/null")
	public ResponseEntity<ApiResponse> findByAddressLine2IsNull() {
		log.info("inside address controller findByAddressLine2IsNotNull");
		ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.ADDRESS_NULL,
				addressService.findByAddressLine2IsNull());
		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}
}
