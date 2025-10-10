package com.icsd.service;

import com.icsd.dto.request.AdminLoginDto;
import com.icsd.dto.request.AdminRequestDto;
import com.icsd.model.Admin;
import com.icsd.model.Customer;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    public Admin isValidAdminByEmailidAndPwd(@Valid AdminLoginDto adminLogin);

    public boolean createAdmin(@Valid AdminRequestDto adminRequest);

    public void saveCustomersFromExcel(MultipartFile file);

    List<Customer> getAllCustomers();


    public Optional<Admin> getAdminDataByEmailId(String username);
}
