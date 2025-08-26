package com.icsd.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.CustomerLoginDTO;
//import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
//import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
//import com.icsd.dto.response.CustomerResponse;
import com.icsd.exception.IcsdException;
import com.icsd.model.Customer;
import com.icsd.model.Gender;
import com.icsd.service.CustomerService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Slf4j
public class CustomerController {
	

	@Autowired
	private CustomerService customerService;


	@PostMapping(value = "/customer")
	public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequest)
	{
		log.info("inside create method of customer controller");

		System.out.println("Create Customer API called at "+new Date());
		System.out.println(customerRequest);
		Integer generatedCustomerId=customerService.createCustomer(customerRequest);
	
		ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE,  generatedCustomerId);
		return new ResponseEntity<ApiResponse> (apiResponse,HttpStatus.OK);
		
	}
	
	
	@PostMapping(value="/customer/isValid")
	public ResponseEntity<ApiResponse> isValidUser(@RequestBody  @Valid CustomerLoginDTO customerLogin)
	{
		log.info("authenticating user - valid or not ");
		System.out.println(customerLogin);
		boolean res=customerService.isValidCustByEmailidAndPwd(customerLogin);
		ApiResponse apiresponse=new ApiResponse(HttpStatus.OK.value(),messages.VALID_USER, res);
		
		return new ResponseEntity<ApiResponse>	(apiresponse,HttpStatus.OK);
	}
	
	
	@GetMapping("/customer/{emailId}")
	public ResponseEntity<ApiResponse>getCustomerDataByEmailId (@PathVariable("emailId") String emailId){
		Optional<Customer> cust=customerService.getCustomerDataByEmailId(emailId);
		if(!cust.isPresent()) {
			log.info("no record found");
			ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(),  messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);	
		}
		log.info("customer found for given email id");
ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(),messages.CUSTOMER_FOUND, cust);
		
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);

	}
	
	
	
	
	@GetMapping(value="/customer/lastName/{lastName}")
	public  ResponseEntity<ApiResponse> findByLastNameDTOResponse(@PathVariable("lastName") String lnm)
	{
		log.info("inside controller findByLastName lnm="+ lnm);
		List<CustomerFnmLnmGenderDTO> list=customerService.findByLastName(lnm);
		if(list.isEmpty())
		{
			log.info("no record found");
			ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);
		
		return new ResponseEntity<>(apiresponse,HttpStatus.OK);
	}
	


@GetMapping(value="/customer/firstNameIgnore/{firstName}")
public  ResponseEntity<ApiResponse> findByFirstNameIgnoreCase(@PathVariable("firstName") String fn)
{
	log.info("inside controller findByFirstNameIgnoreCase fn="+ fn);
	List<Customer> list=customerService.findByFirstNameIgnoreCase(fn);
	if(list.isEmpty())
	{
		log.info("no record found");
		ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(),messages.CUSTOMER_NOT_FOUND);
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
	}
	ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);
	
	return new ResponseEntity<>(apiresponse,HttpStatus.OK);
}


@GetMapping(value="/customer/firstNameLike/{firstName}")
public  ResponseEntity<ApiResponse> findByFirstNameLike(@PathVariable("firstName") String fn)
{
	log.info("inside controller findByFirstNameLike fn="+ fn);
	List<Customer> list=customerService.findByFirstNameLike(fn);
	if(list.isEmpty())
	{
		log.info("no record found");
		ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
	}
	ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(),messages.CUSTOMER_FOUND, list);
	
	return new ResponseEntity<>(apiresponse,HttpStatus.OK);
}


@GetMapping(value="/customer/firstNameContaining/{firstName}")
public  ResponseEntity<ApiResponse> findByFirstNameContaining(@PathVariable("firstName") String fn)
{
	log.info("inside controller findByFirstNameContaining fn="+ fn);
	List<Customer> list=customerService.findByFirstNameContaining(fn);
	if(list.isEmpty())
	{
		log.info("no record found");
		ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
	}
	ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);
	
	return new ResponseEntity<>(apiresponse,HttpStatus.OK);
}


@GetMapping(value="/customer/firstNameContains/{firstName}")
public  ResponseEntity<ApiResponse> findByfirstNameContains(@PathVariable("firstName") String fn)
{
	log.info("inside controller findByfirstNameContains fn="+ fn);
	List<Customer> list=customerService.findByfirstNameContains(fn);
	if(list.isEmpty())
	{
		log.info("no record found");
		ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
	}
	ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);
	
	return new ResponseEntity<>(apiresponse,HttpStatus.OK);
}

@GetMapping(value="/customer/firstNameIsContaining/{firstName}")
public  ResponseEntity<ApiResponse> findByfirstNameIsContaining(@PathVariable("firstName") String fn)
{
	log.info("inside controller findByfirstNameIsContaining fn="+ fn);
	List<Customer> list=customerService.findByfirstNameIsContaining(fn);
	if(list.isEmpty())
	{
		log.info("no record found");
		ApiResponse apiresponse=new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.NOT_FOUND);
	}
	ApiResponse apiresponse=new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);
	
	return new ResponseEntity<>(apiresponse,HttpStatus.OK);
}

}

