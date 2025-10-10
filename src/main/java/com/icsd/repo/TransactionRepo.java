package com.icsd.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icsd.model.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction,Integer> {

	List<Transaction> findBytoAccountNumberCustomerCustomerId(int customerId);

	List<Transaction> findByfromAccountNumberCustomerCustomerId(int customerId);


}
