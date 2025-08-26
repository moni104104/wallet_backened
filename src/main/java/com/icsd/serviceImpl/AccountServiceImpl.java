package com.icsd.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.AccountRequestDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Account;
import com.icsd.model.AccountType;
import com.icsd.model.Customer;
import com.icsd.repo.AccountRepo;
import com.icsd.repo.CustomerRepo;
import com.icsd.service.AccountService;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Service
@Builder
@Slf4j
public class AccountServiceImpl implements AccountService{

	
	@Autowired 
	AccountRepo accountRepo;
	
	@Autowired
	CustomerRepo customerRepo;
	public int saveAccount(AccountRequestDTO accReq) {
		
		log.info("create a new account");
		Optional<Customer> optionalCust= customerRepo.findById(accReq.getCustomerId());
		if(!optionalCust.isPresent())
		{
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		
		Customer cust=optionalCust.get();
		cust.setCustomerId(accReq.getCustomerId());
		
		Account account=Account.builder()
				.accountType(accReq.getAccountType())
				.customer(cust).openingBalance(Double.parseDouble(accReq.getOpeningBalance())).description(accReq.getDescription())
				.openingDate(LocalDate.now()).build();
		
		int accNumberCreated=accountRepo.save(account).getAccountNumber();
				
				

		
		return accNumberCreated;
	}

	@Override
	public List<Account> getAccountsByCustId(int intCustid) {
		
		log.info("getting accounts by custid for customer id "+ intCustid);
		
		List<Account>  listAccounts=accountRepo.findByCustomerCustomerId(intCustid);
		if(listAccounts.isEmpty())
		{
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND+ intCustid);
		}
		return listAccounts;
	}

	@Override
	public List<Account> getAccountsByCustEmailId(String custEmailId)
	{
		log.info("inside service getAccountsByCustEmailId");		

		Customer cust= customerRepo.getCustomerByEmailId(custEmailId);
		List<Account> list=getAccountsByCustId(cust.getCustomerId());
		return list;
	}
	
	
	@Override
	public List<Account> findByAccountTypeIn(List<AccountType> accTypes) {
		log.info("inside service findByAccountTypeIn");		

	return accountRepo.findByAccountTypeIn(accTypes);
	}
	
	
	@Override
	public List<Account> findByOrderByOpeningBalanceAsc() {
		log.info("inside service findByOrderByOpeningBalanceAsc");		

		return accountRepo.findByOrderByOpeningBalanceAsc();
	}
	
	
	@Override
	public List<Account> findByOpeningDateAfter(LocalDate dt) {
		log.info("inside service findByOpeningDateAfter");		

		return accountRepo.findByOpeningDateAfter(dt);
	}
	
	@Override
	public List<Account> findByOpeningDateBetween(LocalDate startDate, LocalDate endDate) {
		log.info(startDate.toString());
		log.info(endDate.toString());
		return accountRepo.findByOpeningDateBetween(startDate, endDate);
	}

	
	@Override
	public List<Account> getAccountsLessThanEqualOpBal(double openingBalance) {
		log.info("inside  getAccountsLessThanOpBalEqual");
		List<Account> lst=accountRepo.findByOpeningBalanceLessThanEqual(openingBalance);
		return lst;
	}
	
	
	@Override
	public List<Account> findDistinctByAccountTypeAndOpeningBalance(AccountType accType, double openingBalance) {
		
		log.info("inside find distinct By accctype and op balance - accType "+ accType+" op bal= "+ openingBalance);		
		List<Account> lst=	accountRepo.findDistinctByAccountTypeAndOpeningBalance(accType, openingBalance);
		return lst;
		
		
	}	
		
		@Override
		public List<String> getDistinctAccountType() {
			log.info("inside get distinct acc type");
			return  accountRepo.getDistinctAccountType();
		}

		
		@Override
		public Account getAccountByAccNumber(int accountNumber) {
			log.info("inside service getAccountByAccNumber"+ accountNumber);		

			Optional<Account> optAcc=accountRepo.findById(accountNumber);
			if(optAcc.isEmpty())
			{
				throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND+ accountNumber);
			}
			return optAcc.get();
			
		}

		@Override
		public double checkBalanceByAccountNumber(int accountNumber) {
			log.info("inside service checkBalanceByAccountNumber"+ accountNumber);		

			Account acc= getAccountByAccNumber(accountNumber);
			
			double balance =acc.getOpeningBalance();
			return balance;
		}

	

		@Override
		public void deleteAccount(String emailId, String password, int accountNumber) {
			log.info("inside servic delete account for account number"+ accountNumber);		

        Optional<Customer> cust= customerRepo.findByEmailIdAndPassword(emailId, password)	;
        Customer customer= cust.get();
        Optional<Account> acc = accountRepo.findByAccountNumberAndCustomer(accountNumber, customer);
        Account account=acc.get();
        accountRepo.delete(account);;
		}
		
		
		

	}

	

