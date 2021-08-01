package com.den.marvel.marvel.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
@Documented
public @interface ImageFile {
    String message() default "image has incorrect extension";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
