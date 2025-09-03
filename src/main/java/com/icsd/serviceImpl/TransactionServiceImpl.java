package com.icsd.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.FundTransferRequestDTO;
import com.icsd.dto.request.TransactionDepositRequestDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Account;
import com.icsd.model.Transaction;
import com.icsd.model.TransactionType;
import com.icsd.repo.AccountRepo;
import com.icsd.repo.TransactionRepo;
import com.icsd.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	TransactionRepo transactionRepo;
	@Autowired
	AccountRepo accountRepo;
	@Autowired
	AccountServiceImpl accountServiceImpl;

	@Override
	public Transaction depositAmountInAccount(TransactionDepositRequestDTO tdd) {
		log.info("inside transaction service for depositAmountInAccount");

		int accountNumber = tdd.getAccountNumber();
		Optional<Account> optAccount = accountRepo.findById(accountNumber);
		if (optAccount.isEmpty()) {
			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + accountNumber);
		}

		double amount = tdd.getAmount();
		if (amount <= 0) {
			throw new IllegalArgumentException(messages.NEGATIVE_AMOUNT);
		}
		Account acc = accountServiceImpl.getAccountByAccNumber(accountNumber);

		log.info(
				"acc reterived from account service inside transaction service implementation - we use accountservice as a dependency inisde transaction service ");
		log.info("To acocunt reterieved is " + acc);
		double newOpeningBalance = acc.getOpeningBalance() + tdd.getAmount();
		log.info("updating opening balance in account");
		updateOpeningBalanceByAccountNumber(acc, newOpeningBalance);
		TransactionType transactionType = TransactionType.CREDIT;
		LocalDate transactionDate = LocalDate.now();
		String description = tdd.getDescription();
		log.info("reteriving from account from account table ");
//	Account fromAccount = acs.getAccountByAccNumber(tdd.getFromAccountNumber());
//	log.info("from account reterieved " + fromAccount);
		Account toAcc = acc;
		log.info("saving transaction ");
		Transaction trans = new Transaction(transactionType, transactionDate, amount, description, null, toAcc);

		transactionRepo.save(trans);
		log.info("transaction saved with details " + trans);

		return trans;
	}

	@Override
	public Transaction WithDrawAmountfromAccount(TransactionDepositRequestDTO tdd) {
		log.info("inside transaction service for WithDrawAmountfromAccount");

		int accountNumber = tdd.getAccountNumber();
		Optional<Account> optAccount = accountRepo.findById(accountNumber);
		if (optAccount.isEmpty()) {
			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + accountNumber);
		}

		double amount = tdd.getAmount();
		if (amount <= 0) {
			throw new IllegalArgumentException(messages.NEGATIVE_AMOUNT);
		}
		Account acc = accountServiceImpl.getAccountByAccNumber(accountNumber);
		if (acc.getOpeningBalance() < amount) {
			throw new IllegalArgumentException(messages.INSUFFICIENT_BALANCE);
		}
		log.info(
				"acc reterived from account servie inside transaction service implementation - we use accoutservice as a dependency inisde transaction service ");
		log.info("from acocunt reterieved is " + acc);
		double newOpeningBalance = acc.getOpeningBalance() - tdd.getAmount();
		log.info("updating opening balance in account");
		updateOpeningBalanceByAccountNumber(acc, newOpeningBalance);
		TransactionType transactionType = TransactionType.DEBIT;
		LocalDate transactionDate = LocalDate.now();
		String description = tdd.getDescription();
		log.info("reteriving from account from account table ");
		// Account fromAccount = acs.getAccountByAccNumber(tdd.getFromAccountNumber());
		// log.info("from account reterieved " + fromAccount);
		Account fromAcc = acc;
		log.info("saving transaction ");
		Transaction trans = new Transaction(transactionType, transactionDate, amount, description, fromAcc, null);

		transactionRepo.save(trans);
		log.info("transaction saved with details " + trans);

		return trans;
	}
//
//	@Override
//	public Transaction fundTransferFunction(FundTransferRequestDTO tdd) {
//		log.info("inside transaction service for fund transfer");
//		int fromAccountNumber = tdd.getFromAccountNumber();
//		Optional<Account> optAccount = accountRepo.findById(fromAccountNumber);
//		if (optAccount.isEmpty()) {
//			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + fromAccountNumber);
//		}
//		int toAccountNumber = tdd.getToAccountNumber();
//		Optional<Account> optAccount2 = accountRepo.findById(toAccountNumber);
//		if (optAccount2.isEmpty()) {
//			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + toAccountNumber);
//		}
//
//		double amount = tdd.getAmount();
//		if (amount <= 0) {
//			throw new IllegalArgumentException(messages.NEGATIVE_AMOUNT);
//		}
//		Account fromAcc = accountServiceImpl.getAccountByAccNumber(fromAccountNumber);
//		if (fromAcc.getOpeningBalance() < amount) {
//			throw new IllegalArgumentException(messages.INSUFFICIENT_BALANCE);
//		}
//		log.info("Fund Transfer fromAcc=: " + fromAcc);
//		Account toAcc = accountServiceImpl.getAccountByAccNumber(toAccountNumber);
//		log.info("Fund Transfer toAcc=: " + toAcc);
//		double oldOpeningBalFromAccout = fromAcc.getOpeningBalance();
//		double newOpeningBalFromAcc = oldOpeningBalFromAccout - tdd.getAmount();
//		log.info("fund Transfer fromAccountNumber: " + fromAccountNumber + " newOpeningBalFromAcc="
//				+ newOpeningBalFromAcc);
//		updateOpeningBalanceByAccountNumber(fromAcc, newOpeningBalFromAcc);
//
//		double oldOpeningBalToAcc = toAcc.getOpeningBalance();
//		double newOpeningBalToAcc = oldOpeningBalToAcc + tdd.getAmount();
//		updateOpeningBalanceByAccountNumber(toAcc, newOpeningBalToAcc);
//		TransactionType transactionType = TransactionType.DEBIT;
//		LocalDate transactionDate = LocalDate.now();
//		log.info("todays date is " + transactionDate.toString());
//		String description = tdd.getDescription();
//
//		Transaction trans = new Transaction(transactionType, transactionDate, tdd.getAmount(), description, fromAcc,
//				toAcc);
//		transactionRepo.save(trans);
//		log.info("saved in database");
//
//		return trans;
//	}

	@Override
	public Transaction fundTransferFunction(FundTransferRequestDTO tdd) {
		log.info("inside transaction service for fund transfer");
		int fromAccountNumber = tdd.getFromAccountNumber();
		Optional<Account> optAccount = accountRepo.findById(fromAccountNumber);
		if (optAccount.isEmpty()) {
			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + fromAccountNumber);
		}
		int toAccountNumber = tdd.getToAccountNumber();
		Optional<Account> optAccount2 = accountRepo.findById(toAccountNumber);
		if (optAccount2.isEmpty()) {
			throw new ResourceNotFoundException(messages.ACCOUNT_NOT_FOUND + toAccountNumber);
		}

		double amount = tdd.getAmount();
		if (amount <= 0) {
			throw new IllegalArgumentException(messages.NEGATIVE_AMOUNT);
		}
		Account fromAcc = accountServiceImpl.getAccountByAccNumber(fromAccountNumber);
		if (fromAcc.getOpeningBalance() < amount) {
			throw new IllegalArgumentException(messages.INSUFFICIENT_BALANCE);
		}
		log.info("Fund Transfer fromAcc=: " + fromAcc);
		Account toAcc = accountServiceImpl.getAccountByAccNumber(toAccountNumber);
		log.info("Fund Transfer toAcc=: " + toAcc);
		double oldOpeningBalFromAccout = fromAcc.getOpeningBalance();
		double newOpeningBalFromAcc = oldOpeningBalFromAccout - tdd.getAmount();
		log.info("fund Transfer fromAccountNumber: " + fromAccountNumber + " newOpeningBalFromAcc="
				+ newOpeningBalFromAcc);
		updateOpeningBalanceByAccountNumber(fromAcc, newOpeningBalFromAcc);
		TransactionType transactionType = TransactionType.DEBIT;
		LocalDate transactionDate = LocalDate.now();
		String description = tdd.getDescription();
		Transaction trans = new Transaction(transactionType, transactionDate, tdd.getAmount(), description, fromAcc,
				null);
		transactionRepo.save(trans);

		double oldOpeningBalToAcc = toAcc.getOpeningBalance();
		double newOpeningBalToAcc = oldOpeningBalToAcc + tdd.getAmount();
		updateOpeningBalanceByAccountNumber(toAcc, newOpeningBalToAcc);
		log.info("todays date is " + transactionDate.toString());
		transactionType = TransactionType.CREDIT;

		trans = new Transaction(transactionType, transactionDate, tdd.getAmount(), description, null, toAcc);
		transactionRepo.save(trans);
		log.info("saved in database");

		return trans;
	}

	@Override
	public void updateOpeningBalanceByAccountNumber(Account acc, double newOpeningBalance) {
		log.info("inside service to update balance by account number");
		acc.setOpeningBalance(newOpeningBalance);
		accountRepo.save(acc);
		log.info("account is updated with new updatedbalance " + newOpeningBalance + " accno" + acc.getAccountNumber());
	}

	@Override
	public List<Transaction> getTransactionsByCustomerId(int customerId) {
		log.info("getting transactions for customer id " + customerId);
		List<Transaction> listTransactions = transactionRepo.findBytoAccountNumberCustomerCustomerId(customerId);
		List<Transaction> listTransactions2 = transactionRepo.findByfromAccountNumberCustomerCustomerId(customerId);

		List<Transaction> list = new ArrayList<>();

		if (!listTransactions.isEmpty() && listTransactions != null) {
			list.addAll(listTransactions);
		}
		if (!listTransactions2.isEmpty() && listTransactions2 != null) {
			list.addAll(listTransactions2);

		}
//         if(list.isEmpty()) {
//  			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND+ customerId);
//
//         }
		return list;
	}

}
