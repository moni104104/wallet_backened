package com.icsd.dto.request;

import com.icsd.dto.common.messages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CustomerLoginDTO {

	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	@Email(message=messages.EMAIL)
	private String emailId;
	

	
	
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message=messages.PASSWORD_PATTERN)
	private String  password;
	
	
}
