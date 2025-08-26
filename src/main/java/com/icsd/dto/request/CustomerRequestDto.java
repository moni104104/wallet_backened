package com.icsd.dto.request;


import java.security.MessageDigestSpi;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icsd.dto.common.messages;
import com.icsd.model.Gender;
import com.icsd.model.SubscriptionStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


//
//"data": {
//    "firstName": "rohan",
//    "lastName": "kumar",
//    "emailId": "rb@rb.com",
//    "contactNo": "987654321",
//    "gender": "MALE",
//    "password": "icsd",
//    "confirmPassword": "icsd",
//    "addressLine1": "delhi",
//    "addressLine2": "sec 1123",
//    "city": "delhi",
//    "pincode": "132103",
//    "state": "delhis"
//}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDto {

	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	@Size(min = 2,max = 60,message = messages.NAME_SIZE)
	public String firstName;
	
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	private String lastName;
	
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	@Email(message =messages.EMAIL)
	private String emailId;
	
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
    @Pattern(regexp = "^[0-9]{10}$", message = messages.CONTACT_PATTERN )
	private String contactNo;
	
	private Gender gender;
	
	
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message=messages.PASSWORD_PATTERN)
	private String  password;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	private String confirmPassword;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	private String addressLine1;
//	@NotBlank(message = "First name should not be blank")
//	@NotNull(message="First name should not be null")
	private String addressLine2;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	private String city;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
	private String State;
	@NotBlank(message = messages.NOT_BLANK)
	@NotNull(message=messages.NOT_NULL)
    @Pattern(regexp = "^[0-9]{6}$", message =messages.PINCODE_PATTERN)
	private String pincode;
    @JsonFormat(pattern = "yyyy-MM-dd")

	private LocalDate expiryDate;
	
	   private SubscriptionStatus status;

	   @Override
	   public String toString() {
		return "CustomerRequestDto [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
				+ ", contactNo=" + contactNo + ", gender=" + gender + ", password=" + password + ", confirmPassword="
				+ confirmPassword + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city="
				+ city + ", State=" + State + ", pincode=" + pincode + ", expiryDate=" + expiryDate + ", status="
				+ status + "]";
	   }

	//@Override
//	public String toString() {
//		return "CustomerRequestDto [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId
//				+ ", contactNo=" + contactNo + ", gender=" + gender + ", password=" + password + ", confirmPassword="
//				+ confirmPassword + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city="
//				+ city + ", State=" + State + ", pincode=" + pincode + "]";
//	}
//		
	
	
	
	}

