package com.icsd.security;

import com.icsd.dto.common.messages;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Customer;
import com.icsd.service.CustomerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    CustomerService customerService;


    private final CustomerService customerService;

    public CustomUserDetailsService(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }


    @Override
    public UserDetails loadUserByUsername(String emailId) throws ResourceNotFoundException {
        Customer customer = customerService.getCustomerDataByEmailId(emailId)
                .orElseThrow(() -> new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND));
        return org.springframework.security.core.userdetails.User
                .withUsername(customer.getEmailId())
                .password(customer.getPassword())
                .authorities("CUSTOMER")
                .build();
    }
}
