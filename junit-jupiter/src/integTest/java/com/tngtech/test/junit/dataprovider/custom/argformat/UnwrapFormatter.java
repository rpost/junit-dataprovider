package com.tngtech.test.junit.dataprovider.custom.argformat;

public class UnwrapFormatter implements ArgumentFormatter<WrappedClass> {

    @Override
    public String format(WrappedClass argument) {
        return argument.getWrappedClazz().getSimpleName();
    }

}
