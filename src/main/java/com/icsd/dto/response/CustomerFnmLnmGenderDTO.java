package com.icsd.dto.response;

import com.icsd.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CustomerFnmLnmGenderDTO {
	String firstName;

	 String lastName;

	Gender gender;

}
