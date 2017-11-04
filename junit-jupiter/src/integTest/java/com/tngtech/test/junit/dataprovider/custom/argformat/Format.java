package com.tngtech.test.junit.dataprovider.custom.argformat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Format {

    Class<? extends ArgumentFormatter> value();

}
