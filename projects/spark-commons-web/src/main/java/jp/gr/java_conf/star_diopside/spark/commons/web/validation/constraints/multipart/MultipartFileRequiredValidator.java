package jp.gr.java_conf.star_diopside.spark.commons.web.validation.constraints.multipart;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileRequiredValidator implements ConstraintValidator<MultipartFileRequired, MultipartFile> {

    @Override
    public void initialize(MultipartFileRequired constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !StringUtils.isEmpty(value.getOriginalFilename());
    }

}
