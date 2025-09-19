package com.icsd.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.icsd.service.AddressClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.AdminLoginDto;
import com.icsd.dto.request.AdminRequestDto;
import com.icsd.dto.request.RowError;
import com.icsd.exception.EntityAlreadyExistException;
import com.icsd.exception.InvalidExcelDataException;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.helper.ExcelHelper;
import com.icsd.model.Admin;
import com.icsd.model.Customer;
import com.icsd.repo.AddressRepo;
import com.icsd.repo.AdminRepo;
import com.icsd.repo.CustomerRepo;
import com.icsd.service.AdminService;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminRepo adminRepo;

	@Autowired
	CustomerRepo customerRepo;

	@Autowired
	AddressRepo addressRepo;

   @Autowired
    AddressClientService addressClientService;

	private Validator validator;

	@Value("${customer.expiry.days}")
	private int expiryDays;

	public AdminServiceImpl(CustomerRepo customerRepo, AddressRepo addressRepo, AddressClientService addressClientService,
                            @Value("${customer.expiry.days}") int expiryDays, Validator validator) {
		this.customerRepo = customerRepo;
		this.addressRepo = addressRepo;
        this.addressClientService = addressClientService;
        this.expiryDays = expiryDays;
		this.validator = validator;

	}

	@Override
	public Admin isValidAdminByEmailidAndPwd(@Valid AdminLoginDto adminLogin) {
		log.info("Inside admin service to chek whether admin is valid or not" + adminLogin);

		Optional<Admin> optAdmin = adminRepo.findByEmailIdAndPassword(adminLogin.getEmailId(),
				adminLogin.getPassword());
		if (optAdmin.isEmpty()) {
			log.info("customer is not existing for email id " + adminLogin.getEmailId() + " and pwd= "
					+ adminLogin.getPassword());
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		Admin admin = optAdmin.get();
		return admin;
	}

	@Override
	public boolean createAdmin(@Valid AdminRequestDto arDto) {
		boolean res = false;
		log.info("Inside save admin of service with given request" + arDto);
		Optional<Admin> optAdmin = adminRepo.findByEmailId(arDto.getEmailId());

		if (optAdmin.isPresent()) {
			throw new EntityAlreadyExistException(messages.EMAIL_ALREADY_EXIST);
		} else {
			Admin admin = Admin.builder().emailId(arDto.getEmailId()).password(arDto.getPassword()).build();
			log.info("admin entity is now from builder : " + admin);
			adminRepo.save(admin);
			res = true;
		}

		return res;
	}

	@Override
	public void saveCustomersFromExcel(MultipartFile file) {
		List<RowError> rowErrors = new ArrayList<>();
		Set<String> dbEmails = customerRepo.findAllEmails();
		log.info("emails present in database");
		try {
			List<Customer> customersToSave = ExcelHelper.convertExcelToListOfCustomers(file.getInputStream(),
					addressRepo, expiryDays, validator, addressClientService,rowErrors, dbEmails);

			if (!customersToSave.isEmpty()) {
				customerRepo.saveAll(customersToSave);
				log.info("customers saved successfully");
			}

			if (!rowErrors.isEmpty()) {
				throw new InvalidExcelDataException(rowErrors);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Customer> getAllCustomers() {
		List<Customer> customers = customerRepo.findAll();
		return customers;
	}
}
