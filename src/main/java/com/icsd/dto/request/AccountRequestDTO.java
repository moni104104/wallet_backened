package com.icsd.dto.request;

import java.time.LocalDate;

import com.icsd.dto.common.messages;
import com.icsd.model.AccountType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {
	
	private int customerId;
	private AccountType accountType;
	@Min(value = 1 ,message = messages.OPENING_BAL_MIN)
	@Max(value=1000 ,message = messages.OPENING_BAL_MAX)
	
	private String openingBalance;
	
		   
	private LocalDate openingDate=LocalDate.now();
	
	private String description="desc";
	
	
}
