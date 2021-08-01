package com.den.marvel.marvel.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    @Override
    public void initialize(ImageFile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return contentTypes.contains(file.getContentType());
    }
}
