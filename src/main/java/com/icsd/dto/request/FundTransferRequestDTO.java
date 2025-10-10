package com.icsd.dto.request;

import com.icsd.dto.common.messages;

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
public class FundTransferRequestDTO {

//	@Min(value=1,message="customer id should not be 0 ")
//	int customerId;
	@Min(value = 1, message = messages.ACCOUNT_NUMBER_MIN)
	int toAccountNumber;
	@Min(value = 1, message = messages.AMOUNT_MIN)
	@Max(value = 100000, message = messages.AMOUNT_MAX)
	double amount;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message = messages.NOT_NULL)
	String description;
	@Min(value = 1, message = messages.ACCOUNT_NUMBER_MIN)
	int fromAccountNumber;

}