package jp.gr.java_conf.star_diopside.spark.commons.web.validation.constraints.multipart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER })
@Constraint(validatedBy = { MultipartFileNotEmptyValidator.class })
public @interface MultipartFileNotEmpty {

    String message() default "{jp.gr.java_conf.star_diopside.spark.commons.web.validation.constraints.multipart.MultipartFileNotEmpty.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
            ElementType.PARAMETER })
    public @interface List {
        MultipartFileNotEmpty[] value();
    }
}
