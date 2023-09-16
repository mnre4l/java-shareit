package ru.practicum.shareit.booking.service;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {
    String message() default "Bad start/end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
