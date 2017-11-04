package com.tngtech.junit.dataprovider.placeholder.argformat;

/**
 * Interface for defining custom argument formatter using the {@link ArgumentFormat} annotation.
 *
 * @param <T> the type of the object to format
 */
public interface ArgumentFormatter<T> {
    String format(T argument);
}
