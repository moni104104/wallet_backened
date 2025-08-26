package com.icsd.service;

import java.util.List;

import com.icsd.dto.request.FundTransferRequestDTO;
import com.icsd.dto.request.TransactionDepositRequestDTO;
import com.icsd.model.Account;
import com.icsd.model.Transaction;

import jakarta.validation.Valid;

public interface TransactionService {

	public Transaction depositAmountInAccount(@Valid TransactionDepositRequestDTO tdReq);

	void updateOpeningBalanceByAccountNumber(Account acc, double newOpeningBalance);

	Transaction WithDrawAmountfromAccount(@Valid TransactionDepositRequestDTO tdd);

	Transaction fundTransferFunction( @Valid FundTransferRequestDTO tdReq);



	//List<Transaction> getTransactionsByAccountNumber(int accountNumber);



}
