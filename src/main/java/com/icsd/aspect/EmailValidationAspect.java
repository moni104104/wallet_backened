package com.icsd.aspect;

import com.icsd.dto.common.messages;
import com.icsd.repo.CustomerRepo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

@Aspect
@Component
public class EmailValidationAspect {

    @Autowired
    private CustomerRepo customerRepo;

    @Before("@annotation(com.icsd.annotation.ValidEmailAOP)")
    public void validateEmail(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        String emailId = null;

        for (Object arg : args) {
            if (arg == null) continue;

            if (arg instanceof String && ((String) arg).contains("@")) {
                emailId = (String) arg;
                break;
            }

            try {
                Field field = arg.getClass().getDeclaredField("emailId");
                field.setAccessible(true);
                Object value = field.get(arg);
                if (value instanceof String) {
                    emailId = (String) value;
                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        if (emailId == null || emailId.isBlank()) {
            throw new IllegalArgumentException(messages.EMAIL_NOT_NULL);
        }

        if (!Pattern.compile(messages.EMAIL_PATTERN).matcher(emailId).matches()) {
            throw new IllegalArgumentException(messages.EMAIL);
        }

        if (!customerRepo.existsByEmailId(emailId)) {
            throw new IllegalArgumentException(messages.CUSTOMER_NOT_FOUND);
        }
    }
}
