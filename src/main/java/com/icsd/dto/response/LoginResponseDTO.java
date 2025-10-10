package com.icsd.dto.response;

import com.icsd.model.Admin;
import com.icsd.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private Customer customer;
    public Admin admin;
}
