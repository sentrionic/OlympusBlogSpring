package com.github.sentrionic.olympusblog.dto.password;


import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordsEqualConstraintValidator.class)
@Target({ElementType.TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface PasswordsEqualConstraint {
    String message() default "Passwords are not equal";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class PasswordsEqualConstraintValidator implements ConstraintValidator<PasswordsEqualConstraint, ChangePasswordDTO> {
    @Override
    public boolean isValid(ChangePasswordDTO dto, ConstraintValidatorContext constraintValidatorContext) {
        return dto.getConfirmNewPassword().equals(dto.getNewPassword());
    }
}
