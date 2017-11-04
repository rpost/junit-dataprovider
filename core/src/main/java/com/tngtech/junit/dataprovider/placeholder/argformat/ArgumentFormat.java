package com.tngtech.junit.dataprovider.placeholder.argformat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a test method parameter to provide a custom formatter for it.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
public @interface ArgumentFormat {
    Class<? extends ArgumentFormatter<?>> value();
}
