package com.icsd.dto.request;

import com.icsd.dto.common.messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {


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
    private String state;
    @NotBlank(message = messages.NOT_BLANK)
    @NotNull(message=messages.NOT_NULL)
    @Pattern(regexp = "^[0-9]{6}$", message =messages.PINCODE_PATTERN)
    private String pincode;
}
