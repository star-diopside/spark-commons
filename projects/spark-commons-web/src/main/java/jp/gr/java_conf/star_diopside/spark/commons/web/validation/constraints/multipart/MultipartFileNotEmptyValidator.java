package jp.gr.java_conf.star_diopside.spark.commons.web.validation.constraints.multipart;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileNotEmptyValidator implements ConstraintValidator<MultipartFileNotEmpty, MultipartFile> {

    @Override
    public void initialize(MultipartFileNotEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isEmpty(value.getOriginalFilename())) {
            return true;
        }
        return !value.isEmpty();
    }
}
