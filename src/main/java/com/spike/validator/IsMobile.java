package com.spike.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = IsMobileValidator.class
)
public @interface  IsMobile {

    boolean required() default true;
    String message() default "手机号码格式有误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
