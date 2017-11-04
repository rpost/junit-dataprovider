package com.tngtech.junit.dataprovider.placeholder.argformat;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This default formatter formats an argument mainly using the following approaches in order:
 * <ul>
 * <li>{@link #STRING_NULL} if argument is {@code null}</li>
 * <li>recursively formats values of an arrays as comma-separated list surrounded by {@code []}</li>
 * <li>{@link #STRING_EMPTY} if argument is {@link String} and empty</li>
 * <li>prints {@link String} if argument is a {@link String}</li>
 * <li>{@link Object#toString()} otherwise</li>
 * </ul>
 * If the above resulting {@link String} is {@code null}, {@link #STRING_NULL} is printed. Otherwise the non-printable
 * characters are replaced by {@link #STRING_NON_PRINTABLE}.
 */
public class DefaultFormatter<T> implements ArgumentFormatter<T> {

    /**
     * {@link String} representation of {@code null}
     */
    public static final String STRING_NULL = "<null>";

    /**
     * {@link String} representation of {@code ""}
     */
    public static final String STRING_EMPTY = "<empty string>";

    /**
     * {@link String} representation of an non-printable character
     */
    public static final String STRING_NON_PRINTABLE = "<np>";

    @Override
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification = "false positive if 'param.toString()' returns 'null'")
    public String format(T param) {
        if (param == null) {
            return STRING_NULL;

        } else if (param.getClass().isArray()) {
            if (param.getClass().getComponentType().isPrimitive()) {
                return formatValuesOfPrimitiveArray(param);
            }
            return "[" + formatValuesOfArray((Object[]) param) + "]";

        } else if (param instanceof String && ((String) param).isEmpty()) {
            return STRING_EMPTY;

        }

        String result;
        if (param instanceof String) {
            result = (String) param;
        } else {
            result = param.toString();
        }
        if (result == null) { // maybe null if "param.toString()" returns null
            return STRING_NULL;
        }
        result = result.replaceAll("\0", "\\\\0").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        return replaceNonPrintableChars(result, STRING_NON_PRINTABLE);
    }

    private String formatValuesOfPrimitiveArray(Object primitiveArray) {
        Class<?> componentType = primitiveArray.getClass().getComponentType();

        if (boolean.class.equals(componentType)) {
            return Arrays.toString((boolean[]) primitiveArray);

        } else if (byte.class.equals(componentType)) {
            return Arrays.toString((byte[]) primitiveArray);

        } else if (char.class.equals(componentType)) {
            return Arrays.toString((char[]) primitiveArray);

        } else if (short.class.equals(componentType)) {
            return Arrays.toString((short[]) primitiveArray);

        } else if (int.class.equals(componentType)) {
            return Arrays.toString((int[]) primitiveArray);

        } else if (long.class.equals(componentType)) {
            return Arrays.toString((long[]) primitiveArray);

        } else if (float.class.equals(componentType)) {
            return Arrays.toString((float[]) primitiveArray);

        } else if (double.class.equals(componentType)) {
            return Arrays.toString((double[]) primitiveArray);
        }
        throw new IllegalStateException("Called 'formatPrimitiveArray' on non-primitive array");
    }

    private String formatValuesOfArray(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(format((T) array[i]));
            if (i < array.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private String replaceNonPrintableChars(String input, String replacement) {
        StringBuilder result = new StringBuilder();
        for (int offset = 0; offset < input.length();) {
            int codePoint = input.codePointAt(offset);
            offset += Character.charCount(codePoint);

            // Replace invisible control characters and unused code points
            switch (Character.getType(codePoint)) {
                case Character.CONTROL: // \p{Cc}
                case Character.FORMAT: // \p{Cf}
                case Character.PRIVATE_USE: // \p{Co}
                case Character.SURROGATE: // \p{Cs}
                case Character.UNASSIGNED: // \p{Cn}
                    result.append(replacement);
                    break;

                default:
                    result.append(Character.toChars(codePoint));
                    break;
            }
        }
        return result.toString();
    }
}
