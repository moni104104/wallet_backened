package com.icsd.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByEmailId(String emailId);

	Optional<Customer> findByEmailIdAndPassword(String emailId, String password);

	List<CustomerFnmLnmGenderDTO> findByLastName(String lnm);

	List<Customer> findByFirstNameIgnoreCase(String fn);

	List<Customer> findByFirstNameLike(String fn);

	List<Customer> findByfirstNameIsContaining(String fn);

	List<Customer> findByfirstNameContains(String fn);

	List<Customer> findByFirstNameContaining(String fn);

	List<Customer> findByExpiryDate(LocalDate expiryDate);

	Customer getCustomerByEmailId(String custEmailId);

    @Query("SELECT c.emailId FROM Customer c")
	Set<String> findAllEmails();

}
