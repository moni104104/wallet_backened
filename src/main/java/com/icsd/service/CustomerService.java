package com.icsd.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.model.Customer;

import jakarta.validation.Valid;

public interface CustomerService {

	public Integer createCustomer(@Valid CustomerRequestDto customerRequest);

	public Customer isValidCustByEmailidAndPwd(@Valid CustomerLoginDTO customerLogin);

	public List<CustomerFnmLnmGenderDTO> findByLastName(String lnm);

	public List<Customer> findByFirstNameIgnoreCase(String fn);

	public List<Customer> findByFirstNameLike(String fn);

	public List<Customer> findByFirstNameContaining(String fn);

	public List<Customer> findByfirstNameContains(String fn);

	public List<Customer> findByfirstNameIsContaining(String fn);

	public Optional<Customer> getCustomerDataByEmailId(String emailId);

	void saveCustomersFromExcel(MultipartFile file);

	List<Customer> getAllCustomersFromExcel();

	public Optional<Customer> getCustomerByCustomerId(int customerId);

}
