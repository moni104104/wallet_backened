package com.icsd.serviceImpl;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.AdminLoginDto;
import com.icsd.dto.request.AdminRequestDto;
import com.icsd.dto.request.CustomerExcelImportDto;
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
import com.icsd.service.AddressClientService;
import com.icsd.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    Validator validator;

    @Value("${customer.expiry.days}")
    private int expiryDays;

    @Autowired
    PasswordEncoder passwordEncoder;

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
            Admin admin = Admin.builder().emailId(arDto.getEmailId()).password(passwordEncoder.encode(arDto.getPassword())).build();
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

            CustomerExcelImportDto excelDto = CustomerExcelImportDto.builder()
                    .addressRepo(addressRepo)
                    .expiryDays(expiryDays)
                    .validator(validator)
                    .addressClientService(addressClientService)
                    .rowErrors(rowErrors)
                    .existingEmails(dbEmails)
                    .build();

            List<Customer> customersToSave = ExcelHelper.convertExcelToListOfCustomers(file.getInputStream(),
                    excelDto);


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

    @Override
    public Optional<Admin> getAdminDataByEmailId(String emailId) {
        log.info("inside ADMIN service to getAdminDataByEmailId" + emailId);
        Optional<Admin> admin = adminRepo.findByEmailId(emailId);
        return admin;
    }
}
