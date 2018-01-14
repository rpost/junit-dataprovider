package com.tngtech.test.junit.dataprovider.custom.argformat;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import com.tngtech.junit.dataprovider.placeholder.argformat.ArgumentFormat;

@ExtendWith(AnnotationArgumentPlaceholderDataProviderExtension.class)
class AnnotationArgumentPlaceholderAcceptanceTest {

    @DataProvider(format = "[%i: %aa[0..-1]]")
    static Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
            { new WrappedClass(AnnotationArgumentPlaceholderAcceptanceTest.class) },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void test(@ArgumentFormat(UnwrapFormatter.class) WrappedClass clazz) {
        // TODO
    }

    @DataProvider(format = "[%i: %aa[0..1]]")
    static Object[][] dataProvider2() {
        // @formatter:off
        return new Object[][] {
            { new WrappedClass(AnnotationArgumentPlaceholderAcceptanceTest.class), new WrappedClass(AnnotationArgumentPlaceholderAcceptanceTest.class), new WrappedClass(AnnotationArgumentPlaceholderAcceptanceTest.class) },
        };
        // @formatter:on
    }

    @TestTemplate
    @UseDataProvider
    void test2(@ArgumentFormat(UnwrapFormatter.class) WrappedClass clazz1,
            @ArgumentFormat(UnwrapFormatter.class) WrappedClass clazz2,
            @ArgumentFormat(UnwrapFormatter.class) WrappedClass clazz3) {
        // TODO
    }
}
