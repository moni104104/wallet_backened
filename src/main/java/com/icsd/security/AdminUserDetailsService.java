package com.icsd.security;

import com.icsd.dto.common.messages;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Admin;
import com.icsd.service.AdminService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminService adminService;

    public AdminUserDetailsService(@Lazy AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String emailId) throws ResourceNotFoundException {
        Admin admin = adminService.getAdminDataByEmailId(emailId)
                .orElseThrow(() -> new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND));
        return org.springframework.security.core.userdetails.User
                .withUsername(admin.getEmailId())
                .password(admin.getPassword())
                .authorities("ADMIN")
                .build();
    }
}


