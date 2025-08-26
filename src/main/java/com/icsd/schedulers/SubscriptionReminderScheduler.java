package com.icsd.schedulers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.icsd.dto.common.messages;
import com.icsd.model.Customer;
import com.icsd.repo.CustomerRepo;
import com.icsd.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionReminderScheduler {
	
	private final CustomerRepo customerRepo;
	private final EmailService emailService;

	
	@Scheduled(cron = "0 08 13 * * *") 
	public void sendExpiryReminders() {
	    LocalDate tomorrow = LocalDate.now().plusDays(1);
	    List<Customer> expiringCustomers = customerRepo.findByExpiryDate(tomorrow);
	    
	    log.info("Found customers: " + expiringCustomers.size());

	    for (Customer customer : expiringCustomers) {
	        log.info("Preparing email for: " + customer.getEmailId());

	        String subject = messages.SUBSCRIPTION_SUBJECT;
	        String body = "Hello " + customer.getFirstName() + ", Your " +
	            customer.getStatus() + " version is expiring on " +
	            customer.getExpiryDate() + ". Please renew it.";
	        emailService.sendEmail(customer.getEmailId(), subject, body);
	    }
	}

}
