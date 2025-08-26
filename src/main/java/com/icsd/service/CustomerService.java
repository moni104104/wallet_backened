package com.icsd.service;


import java.util.List;
import java.util.Optional;

import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.model.Customer;

import jakarta.validation.Valid;

public interface CustomerService {


	public Integer createCustomer(@Valid CustomerRequestDto customerRequest);

	public boolean isValidCustByEmailidAndPwd(@Valid CustomerLoginDTO customerLogin);

	public List<CustomerFnmLnmGenderDTO> findByLastName(String lnm);

	public List<Customer> findByFirstNameIgnoreCase(String fn);

	public List<Customer> findByFirstNameLike(String fn);

	public List<Customer> findByFirstNameContaining(String fn);

	public List<Customer> findByfirstNameContains(String fn);

	public List<Customer> findByfirstNameIsContaining(String fn);

	public Optional<Customer> getCustomerDataByEmailId(String emailId);

}
