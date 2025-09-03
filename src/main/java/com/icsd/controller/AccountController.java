package com.icsd.controller;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.AccountRequestDTO;
import com.icsd.model.Account;
import com.icsd.model.AccountType;
import com.icsd.service.AccountService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AccountController {

	@Autowired
	AccountService accountService;

	@PostMapping("/account")
	public ResponseEntity<ApiResponse> createAccount(@RequestBody @Valid AccountRequestDTO accountRequest) {
		log.info("creating a new Account");
		System.out.println(accountRequest);

		int accountNumber = accountService.saveAccount(accountRequest);

		ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.ACCOUNT_CREATED, accountNumber);
		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/accounts/{customerId}")
	public ResponseEntity<ApiResponse> getAllAccountsByCustomerId(@PathVariable("customerId") int customerId)
			throws Exception {
		log.info("get list of accounts by customer id " + customerId);
		List<Account> listAccounts = accountService.getAccountsByCustId(customerId);

		if (listAccounts.isEmpty()) {
			log.info("No Accounts found for customer id " + customerId);
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND, null);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, listAccounts);

		return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
	}

	@DeleteMapping("/account/{emailId}/{password}/{accountNumber}")
	public ResponseEntity<ApiResponse> deleteAccount(@PathVariable("emailId") String emailId,
			@PathVariable("password") String password, @PathVariable("accountNumber") int accountNumber) {
		log.info("delete account in account controller");

		try {
			accountService.deleteAccount(emailId, password, accountNumber);
			ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.DELETE_ACCOUNT);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		} catch (Exception e) {
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}

	}

	@GetMapping(value = "/account/{emailId}")
	public ResponseEntity<ApiResponse> getAllAccountsByCustomerEmailId(
			@PathVariable("emailId") @Valid @NotBlank(message = "emailId name should not be blank") @NotNull(message = "emailId  should not be null") @Email(message = "not valid email") String emailId)
			throws Exception {
		log.info("get list of accounts by customer eid " + emailId);
		List<Account> listAccounts = accountService.getAccountsByCustEmailId(emailId);
		if (listAccounts.isEmpty()) {
			log.info("No Accounts found for customer emailid " + emailId);
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND, null);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, listAccounts);

		return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
	}

	@GetMapping("/account/balance/{accountNumber}")
	public ResponseEntity<ApiResponse> checkBalanceByAccountNumber(@PathVariable("accountNumber") int accountNumber) {
		log.info("in account controller- check the balance");
		double balance = accountService.checkBalanceByAccountNumber(accountNumber);
		ApiResponse apirespnose = new ApiResponse(HttpStatus.OK.value(), messages.AMOUNT, balance);
		return new ResponseEntity<ApiResponse>(apirespnose, HttpStatus.OK);

	}

	@GetMapping(value = "/account/typeIn")
	public ResponseEntity<ApiResponse> findByAccountTypeIn() {
		log.info("in account controller- find by account type");
		List<AccountType> accTypes = new LinkedList<>();
		accTypes.add(AccountType.CURRENT);
		accTypes.add(AccountType.RD);
		accTypes.add(AccountType.LOAN);
		List<Account> listAccounts = accountService.findByAccountTypeIn(accTypes);
		if (listAccounts.isEmpty()) {
			log.info("No Accounts found for Current, RD and Loan");
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, listAccounts);

		return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
	}

	@GetMapping(value = "/account/balanceAsc")
	public ResponseEntity<ApiResponse> findByOrderByOpeningBalanceAsc() {
		log.info("in account controller- findByOrderByOpeningBalanceAsc");
		List<Account> lst = accountService.findByOrderByOpeningBalanceAsc();
		ApiResponse apirespnose = new ApiResponse(HttpStatus.OK.value(), messages.ACCOUNTS, lst);
		return new ResponseEntity<ApiResponse>(apirespnose, HttpStatus.OK);
	}

	@GetMapping(value = "/account/dateAfter")
	public ResponseEntity<ApiResponse> findByOpeningDateAfter() {
		log.info("in account controller- findByOpeningDateAfter");

		LocalDate startDate = LocalDate.of(2025, 8, 13);

		List<Account> list = accountService.findByOpeningDateAfter(startDate);
		ApiResponse apirespnose = new ApiResponse(HttpStatus.OK.value(), messages.ACCOUNTS, list);
		return new ResponseEntity<ApiResponse>(apirespnose, HttpStatus.OK);
	}

	@GetMapping(value = "/account/dateBetween")
	public ResponseEntity<ApiResponse> findByOpeningDateBetween() {
		log.info("in account controller- findByOpeningDateBetween");

		LocalDate startDate = LocalDate.of(2022, 10, 2);
		LocalDate endDate = LocalDate.of(2025, 10, 14);
		List<Account> lst = accountService.findByOpeningDateBetween(startDate, endDate);
		ApiResponse apirespnose = new ApiResponse(HttpStatus.OK.value(), messages.ACCOUNTS, lst);
		return new ResponseEntity<ApiResponse>(apirespnose, HttpStatus.OK);
	}

	@GetMapping(value = "/account/lessThan/{opBal}")
	public ResponseEntity<ApiResponse> getAllAccountsLessThanOpeningBalance(@PathVariable("opBal") double opBal)
			throws Exception {

		log.info("get list of accounts less than openingbalance " + opBal);
		List<Account> lstAccounts = accountService.getAccountsLessThanEqualOpBal(opBal);

		if (lstAccounts.isEmpty()) {
			log.info("No Accounts , having opbal less than  " + opBal);
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, lstAccounts);

		return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
	}

	@GetMapping(value = "/account/{accType}/{opBal}")
	public ResponseEntity<ApiResponse> findDistinctByAccountTypeAndOpeningBalance(
			@PathVariable("accType") String accType, @PathVariable("opBal") double opBal) throws Exception {
		log.info("loan and opbalanc - distinct entry we want ");
		log.info("inside findDistinctByAccountTypeAndOpeningBalance accType=" + accType + " opbal =" + opBal);
		AccountType at = null;
		if (accType.equals("LOAN")) {
			at = AccountType.LOAN;
		} else if (accType.equals("SALARY")) {
			at = AccountType.SALARY;
		}

		List<Account> lst = accountService.findDistinctByAccountTypeAndOpeningBalance(at, opBal);
		if (lst.isEmpty()) {
			log.info("No accounts founds- no record found ");
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND, null);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, lst);

		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/accountType")
	public ResponseEntity<ApiResponse> getDistinctAccountType() {
		log.info("accType - distinct entry we want ");
		List<String> lst = accountService.getDistinctAccountType();
		if (lst.isEmpty()) {
			ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.ACCOUNT_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.ACCOUNTS, lst);

		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}
}
