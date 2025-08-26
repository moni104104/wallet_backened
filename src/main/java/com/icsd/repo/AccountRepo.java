package com.icsd.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.icsd.model.Account;
import com.icsd.model.AccountType;
import com.icsd.model.Customer;

public interface AccountRepo extends JpaRepository<Account,Integer> {

	List<Account> findByCustomerCustomerId(int intCustid);

	List<Account> findByAccountTypeIn(List<AccountType> accTypes);

	List<Account> findByOrderByOpeningBalanceAsc();

	List<Account> findByOpeningDateAfter(LocalDate dt);
	
   Optional<Account> findByAccountNumberAndCustomer(int accountNumber,Customer customer);

	List<Account> findByOpeningDateBetween(LocalDate startDate, LocalDate endDate);

	List<Account> findByOpeningBalanceLessThanEqual(double openingBalance);

	List<Account> findDistinctByAccountTypeAndOpeningBalance(AccountType accType, double openingBalance);

	@Query(value = "select DISTINCT(a.accountType) from Account a")
	List<String> getDistinctAccountType();



}
