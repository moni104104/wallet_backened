package com.icsd.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepo customerRepo;
	private final AddressRepo addressRepo;
	private final int expiryDays;

	public CustomerServiceImpl(CustomerRepo customerRepo, AddressRepo addressRepo,
			@Value("${customer.expiry.days}") int expiryDays, Validator validator) {
		this.customerRepo = customerRepo;
		this.addressRepo = addressRepo;
		this.expiryDays = expiryDays;

	}

	@Override
	public Integer createCustomer(@Valid CustomerRequestDto crDto) {
		log.info("Inside save customer of service with given request" + crDto);
		Optional<Customer> optCust = customerRepo.findByEmailId(crDto.getEmailId());
		if (optCust.isPresent()) {
			throw new EntityAlreadyExistException(messages.EMAIL_ALREADY_EXIST);
		}

		Address add = Address.builder().addressLine1(crDto.getAddressLine1()).addressLine2(crDto.getAddressLine2())
				.city(crDto.getCity()).state(crDto.getState()).pincode(crDto.getPincode()).build();

		Address addCreated = addressRepo.save(add);
		Customer cust = Customer.builder().firstName(crDto.getFirstName()).lastName(crDto.getLastName())
				.emailId(crDto.getEmailId()).contactNo(crDto.getContactNo()).address(addCreated)
				.gender(crDto.getGender()).password(crDto.getPassword()).registerationDate(LocalDate.now())
				.expiryDate(LocalDate.now().plusDays(expiryDays)).status(crDto.getStatus()).build();
		log.info("cust entity is now from builder : " + cust);
		Customer custCreated = customerRepo.save(cust);
		return custCreated.getCustomerId();
	}

	@Override
	public Customer isValidCustByEmailidAndPwd(@Valid CustomerLoginDTO customerLogin) {
		log.info("Inside customer service to chek whether user is valid or not" + customerLogin);

		Optional<Customer> optCust = customerRepo.findByEmailIdAndPassword(customerLogin.getEmailId(),
				customerLogin.getPassword());
		if (optCust.isEmpty()) {
			log.info("customer is not existing for email id " + customerLogin.getEmailId() + " and pwd= "
					+ customerLogin.getPassword());
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		Customer customer = optCust.get();
		return customer;
	}

	@Override
	public List<CustomerFnmLnmGenderDTO> findByLastName(String lnm) {

		List<CustomerFnmLnmGenderDTO> lst = customerRepo.findByLastName(lnm);
		return lst;
	}

	@Override
	public List<Customer> findByFirstNameIgnoreCase(String fn) {
		List<Customer> lst = customerRepo.findByFirstNameIgnoreCase(fn);
		return lst;
	}

	@Override
	public List<Customer> findByFirstNameLike(String fn) {
		List<Customer> list = customerRepo.findByFirstNameLike(fn);
		return list;
	}

	@Override
	public List<Customer> findByFirstNameContaining(String fn) {
		List<Customer> list = customerRepo.findByFirstNameContaining(fn);
		return list;
	}

	@Override
	public List<Customer> findByfirstNameContains(String fn) {
		List<Customer> list = customerRepo.findByfirstNameContains(fn);
		return list;
	}

	@Override
	public List<Customer> findByfirstNameIsContaining(String fn) {
		List<Customer> list = customerRepo.findByfirstNameIsContaining(fn);
		return list;
	}

	@Override
	public Optional<Customer> getCustomerDataByEmailId(String emailId) {
		log.info("inside service to getCustomerDataByEmailId" + emailId);
		Optional<Customer> cust = customerRepo.findByEmailId(emailId);
		return cust;
	}

	@Override
	public Optional<Customer> getCustomerByCustomerId(int customerId) {
		log.info("inside service to getCustomerByCustomerId" + customerId);
		Optional<Customer> cust = customerRepo.findById(customerId);
		return cust;
	}

	@Override
	public void deleteCustomerByCustomerId(int customerId) {
		log.info("inside service to deleteCustomerByCustomerId" + customerId);
		customerRepo.deleteById(customerId);

	}

	@Override
	public Customer updateCustomerByCustomerId(int customerId, @Valid CustomerRequestDto customerRequest) {
		log.info("inside service to updateCustomerByCustomerId" + customerId);
		Optional<Customer> existingCustomer = customerRepo.findById(customerId);
		Customer updatedCustomer = existingCustomer.get();

		if (customerRequest.getFirstName() != null) {
			updatedCustomer.setFirstName(customerRequest.getFirstName());
		}
		if (customerRequest.getLastName() != null) {
			updatedCustomer.setLastName(customerRequest.getLastName());
		}
		if (customerRequest.getEmailId() != null) {
			updatedCustomer.setEmailId(customerRequest.getEmailId());
		}
		if (customerRequest.getContactNo() != null) {
			updatedCustomer.setContactNo(customerRequest.getContactNo());
		}
		if (customerRequest.getGender() != null) {
			updatedCustomer.setGender(customerRequest.getGender());

		}
		if (customerRequest.getPassword() != null) {
			updatedCustomer.setPassword(customerRequest.getPassword());
		}

		Address address = updatedCustomer.getAddress();
		if (address == null) {
			address = new Address();
		}
		if (customerRequest.getAddressLine1() != null) {
			address.setAddressLine1(customerRequest.getAddressLine1());
		}
		if (customerRequest.getAddressLine2() != null) {
			address.setAddressLine1(customerRequest.getAddressLine2());
		}
		if (customerRequest.getCity() != null) {
			address.setAddressLine1(customerRequest.getCity());
		}
		if (customerRequest.getState() != null) {
			address.setAddressLine1(customerRequest.getState());
		}
		if (customerRequest.getPincode() != null) {
			address.setAddressLine1(customerRequest.getPincode());
		}

		updatedCustomer.setAddress(address);

		if (customerRequest.getStatus() != null) {
			updatedCustomer.setStatus(customerRequest.getStatus());
		}
		return customerRepo.save(updatedCustomer);

	}

//	@Override
//	public List<Customer> searchCustomer(String firstName, String emailId) {
//		if(emailId != null && firstName != null ) {
//            return customerRepo.findByFirstNameAndEmailIdContainingIgnoreCase(firstName, emailId);	
//		}
//		else if(firstName!=null){
//            return customerRepo.findByFirstNameContainingIgnoreCase(firstName);
//
//		}
//		else if(emailId!=null) {
//            return customerRepo.findByEmailIdContainingIgnoreCase(emailId);
//
//		}
//		else {
//			return customerRepo.findAll();
//		}
//	}

}