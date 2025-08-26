package com.icsd.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.exception.EntityAlreadyExistException;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Address;
import com.icsd.model.Customer;
import com.icsd.repo.AddressRepo;
import com.icsd.repo.CustomerRepo;
import com.icsd.service.CustomerService;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Builder
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	AddressRepo addressRepo;

	@Override
	public Integer createCustomer(@Valid CustomerRequestDto  crDto) {
		log.info("Inside save customer of service with given request"+crDto);
	 
		
		Optional<Customer> optCust=customerRepo.findByEmailId(crDto.getEmailId());
		if(optCust.isPresent())
		{
		throw new EntityAlreadyExistException(messages.EMAIL_ALREADY_EXIST);
		}
	
		Address add=Address.builder().addressLine1(crDto.getAddressLine1())
				.addressLine2(crDto.getAddressLine2())
				.city(crDto.getCity())
				.state(crDto.getState())
				.pincode(crDto.getPincode())
				.build();
	
		Address addCreated=addressRepo.save(add);
		
		
		Customer cust=Customer.builder().firstName(crDto.getFirstName()).lastName(crDto.getLastName()).emailId(crDto.getEmailId())
				.contactNo(crDto.getContactNo()).address(addCreated).gender(crDto.getGender()).password(crDto.getPassword()).registerationDate(LocalDate.now()).expiryDate(crDto.getExpiryDate()).status(crDto.getStatus()).build();
		System.out.println("cust entity is now from builder : "+ cust);
		Customer custCreated=customerRepo.save(cust);
		
		return custCreated.getCustomerId();
	}

	@Override
	public boolean isValidCustByEmailidAndPwd(CustomerLoginDTO customerLogin) {
		log.info("Inside customer service to chek whether user is valid or not"+customerLogin);

				boolean res=true;
		Optional<Customer> optCust=customerRepo.findByEmailIdAndPassword(customerLogin.getEmailId(), customerLogin.getPassword());
		if(optCust.isEmpty())
		{
			res=false;
			log.info("customer is not existing for email id "+ customerLogin.getEmailId() + " and pwd= "+ customerLogin.getPassword());
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		
		return res;
	}

@Override	
public List<CustomerFnmLnmGenderDTO> findByLastName(String lnm)
	{
	
		
		List <CustomerFnmLnmGenderDTO> lst=customerRepo.findByLastName(lnm);
		return lst;
	}

@Override
public List<Customer> findByFirstNameIgnoreCase(String fn)
	{
		List <Customer> lst=customerRepo.findByFirstNameIgnoreCase(fn);
		return lst;
	}

@Override
public List<Customer> findByFirstNameLike(String fn) {
    List<Customer> list= customerRepo.findByFirstNameLike(fn);
	return list;
}

@Override
public List<Customer> findByFirstNameContaining(String fn) {
	List<Customer> list= customerRepo.findByFirstNameContaining(fn);
	return list;
}

@Override
public List<Customer> findByfirstNameContains(String fn) {
	List<Customer> list= customerRepo.findByfirstNameContains(fn);
	return list;
}

@Override
public List<Customer> findByfirstNameIsContaining(String fn) {
	List<Customer> list= customerRepo.findByfirstNameIsContaining(fn);
	return list;
}

@Override
public Optional<Customer> getCustomerDataByEmailId(String emailId) {
log.info("inside service to getCustomerDataByEmailId"+emailId);
	Optional<Customer> cust=customerRepo.findByEmailId(emailId);
	return cust;
}

	}

