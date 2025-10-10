package com.icsd.dto.request;

import com.icsd.dto.common.messages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDto {

    @NotBlank(message = messages.NOT_BLANK)
    @NotNull(message = messages.NOT_NULL)
    @Email(message = messages.EMAIL)
    private String emailId;

    @NotBlank(message = messages.NOT_BLANK)
    @NotNull(message = messages.NOT_NULL)
    @Pattern(regexp = messages.PASSWORD_PATTERN, message = messages.PASSWORD_PATTERN_MESSAGE)
    private String password;
}
