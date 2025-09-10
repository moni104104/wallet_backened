package com.icsd.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.icsd.dto.request.AdminLoginDto;
import com.icsd.dto.request.AdminRequestDto;
import com.icsd.model.Admin;
import com.icsd.model.Customer;

import jakarta.validation.Valid;

public interface AdminService {

	public Admin isValidAdminByEmailidAndPwd(@Valid AdminLoginDto adminLogin) ;

	public boolean createAdmin(@Valid AdminRequestDto adminRequest);

	public void saveCustomersFromExcel(MultipartFile file);

	List<Customer> getAllCustomers();


}
