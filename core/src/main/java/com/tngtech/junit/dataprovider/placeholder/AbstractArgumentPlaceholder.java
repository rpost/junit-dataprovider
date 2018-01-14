package com.tngtech.junit.dataprovider.placeholder;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import com.tngtech.junit.dataprovider.placeholder.argformat.ArgumentFormat;
import com.tngtech.junit.dataprovider.placeholder.argformat.ArgumentFormatter;
import com.tngtech.junit.dataprovider.placeholder.argformat.DefaultFormatter;

/**
 * This abstract placeholder is able to format arguments of a dataprovider test as comma-separated {@link String}
 * according to the given index or range subscript. Furthermore the following arguments are treated specially:
 * <table summary="Special {@link String} treatment">
 * <tr>
 * <th>Argument value</th>
 * <th>target {@link String}</th>
 * </tr>
 * <tr>
 * <td>null</td>
 * <td>&lt;null&gt;</td>
 * </tr>
 * <tr>
 * <td>&quot;&quot; (= empty string)</td>
 * <td>&lt;empty string&gt;</td>
 * </tr>
 * <tr>
 * <td>array (e.g. String[])</td>
 * <td>{@code "[" + formatPattern(array) + "]"}</td>
 * </tr>
 * <tr>
 * <td>other</td>
 * <td>{@link Object#toString()}</td>
 * </tr>
 * </table>
 */
abstract class AbstractArgumentPlaceholder extends BasePlaceholder {

    protected static class ParameterAndArgument {

        private final Class<?> parameterType;
        private final Annotation[] parameterAnnotations;
        private final Object argument;

        public ParameterAndArgument(Class<?> parameterType, Annotation[] parameterAnnotations, Object argument) {
            this.parameterType = parameterType;
            this.parameterAnnotations = parameterAnnotations;
            this.argument = argument;
        }

        public Class<?> getParameterType() {
            return parameterType;
        }

        public Annotation[] getParameterAnnotations() {
            return parameterAnnotations;
        }

        public Object getArgument() {
            return argument;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((argument == null) ? 0 : argument.hashCode());
            result = prime * result + Arrays.hashCode(parameterAnnotations);
            result = prime * result + ((parameterType == null) ? 0 : parameterType.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ParameterAndArgument other = (ParameterAndArgument) obj;
            if (argument == null) {
                if (other.argument != null) {
                    return false;
                }
            } else if (!argument.equals(other.argument)) {
                return false;
            }
            if (!Arrays.equals(parameterAnnotations, other.parameterAnnotations)) {
                return false;
            }
            if (parameterType == null) {
                if (other.parameterType != null) {
                    return false;
                }
            } else if (!parameterType.equals(other.parameterType)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ParameterAndArgument [parameterType=" + parameterType + ", parameterAnnotations="
                    + Arrays.toString(parameterAnnotations) + ", argument=" + argument + "]";
        }
    }

    AbstractArgumentPlaceholder(String placeholderRegex) {
        super(placeholderRegex);
    }

    protected String format(ParameterAndArgument parameterAndArgument) {
        Annotation[] parameterAnnotations = parameterAndArgument.getParameterAnnotations();
        Object argument = parameterAndArgument.getArgument();

        Class<? extends ArgumentFormatter<?>> formatter = (Class) DefaultFormatter.class;
        for (Annotation annotation : parameterAnnotations) {
            if (ArgumentFormat.class.isInstance(annotation)) { // TODO meta annotation in core? really?
                formatter = ((ArgumentFormat) annotation).value();
                break;
            }
        }

        try {
            return ((Class<ArgumentFormatter<Object>>) formatter).newInstance().format(argument);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null; // TODO
    }
}
