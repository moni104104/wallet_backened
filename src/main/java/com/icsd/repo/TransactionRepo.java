package com.icsd.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icsd.model.Customer;
import com.icsd.model.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction,Integer> {

	Transaction save(Transaction trans);

}
