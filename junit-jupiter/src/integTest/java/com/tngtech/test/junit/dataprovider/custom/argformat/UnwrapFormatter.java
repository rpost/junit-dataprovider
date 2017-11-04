package com.tngtech.test.junit.dataprovider.custom.argformat;

import com.tngtech.junit.dataprovider.placeholder.argformat.ArgumentFormatter;

public class UnwrapFormatter implements ArgumentFormatter<WrappedClass> {

    @Override
    public String format(WrappedClass argument) {
        return argument.getWrappedClazz().getSimpleName();
    }
}
