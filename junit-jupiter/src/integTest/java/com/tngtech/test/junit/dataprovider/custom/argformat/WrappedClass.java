package com.tngtech.test.junit.dataprovider.custom.argformat;

class WrappedClass {
    private final Class<?> clazz;

    WrappedClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getWrappedClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "WrappedClass [clazz=" + clazz + "]";
    }
}