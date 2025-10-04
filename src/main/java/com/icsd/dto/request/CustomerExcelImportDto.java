package com.icsd.dto.request;

import com.icsd.repo.AddressRepo;
import com.icsd.service.AddressClientService;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerExcelImportDto {
    private AddressRepo addressRepo;
    private int expiryDays;
    private Validator validator;
    private AddressClientService addressClientService;
    private List<RowError> rowErrors;
    private Set<String> existingEmails;


//    public CustomerExcelImportDto(
//            AddressRepo addressRepo,
//            int expiryDays,
//            Validator validator,
//            AddressClientService addressClientService,
//            List<RowError> rowErrors,
//            Set<String> existingEmails) {
//        this.addressRepo = addressRepo;
//        this.expiryDays = expiryDays;
//        this.validator = validator;
//        this.addressClientService = addressClientService;
//        this.rowErrors = rowErrors;
//        this.existingEmails = existingEmails;
//    }
}
