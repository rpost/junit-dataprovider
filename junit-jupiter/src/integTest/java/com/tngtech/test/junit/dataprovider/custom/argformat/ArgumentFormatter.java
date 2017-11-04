package com.tngtech.test.junit.dataprovider.custom.argformat;

public interface ArgumentFormatter<T> {

    String format(T argument);
}
