package com.icsd.service;

import java.time.LocalDate;
import java.util.List;

import com.icsd.dto.request.AccountRequestDTO;
import com.icsd.model.Account;
import com.icsd.model.AccountType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface AccountService {

	int saveAccount(@Valid AccountRequestDTO accountRequest);

	List<Account> getAccountsByCustId(int customerId);

	List<Account> getAccountsByCustEmailId(@Valid String emailId);

	List<Account> findByAccountTypeIn(List<AccountType> accTypes);

	List<Account> findByOrderByOpeningBalanceAsc();

	List<Account> findByOpeningDateAfter(LocalDate startDate);

	List<Account> findByOpeningDateBetween(LocalDate startDate, LocalDate endDate);

	List<Account> getAccountsLessThanEqualOpBal(double openingBalance);

	List<Account> findDistinctByAccountTypeAndOpeningBalance(AccountType at, double opBal);

	List<String> getDistinctAccountType();

	Account getAccountByAccNumber(int accountNumber);

	double checkBalanceByAccountNumber(int accountNumber);

	void deleteAccount(String emailId, String password, int accountNumber);


	

}
