package com.icsd.annotation;

import com.icsd.dto.common.messages;
import com.icsd.repo.CustomerRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class ValidEmailValidator implements ConstraintValidator<ValidEmail,String> {

    @Autowired
    CustomerRepo customerRepo;


    @Override
    public boolean isValid(String emailId, ConstraintValidatorContext context) {

        if(emailId==null || emailId.isBlank()){
    return false;
}

        if (!(Pattern.compile(messages.EMAIL_PATTERN)).matcher(emailId).matches()) {
            return false;
        }

return customerRepo.existsByEmailId(emailId);
    }
}
