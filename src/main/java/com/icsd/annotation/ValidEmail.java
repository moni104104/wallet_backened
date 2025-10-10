package com.icsd.annotation;

import com.icsd.dto.common.messages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailValidator.class)

public @interface ValidEmail {
    String message() default messages.NOT_EXIST;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
