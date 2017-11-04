package com.tngtech.junit.dataprovider.placeholder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This placeholder format the arguments including their parameter names of a dataprovider test as comma-separated
 * {@link String} according to the given index or range subscript. For a list of special argument treatments, see
 * {@link AbstractArgumentPlaceholder}.
 *
 * @see AbstractArgumentPlaceholder
 */
public class NamedArgumentPlaceholder extends AbstractArgumentPlaceholder {

    private static final Logger logger = Logger.getLogger(NamedArgumentPlaceholder.class.getName());

    public NamedArgumentPlaceholder() {
        super("%na\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
    }

    @Override
    protected String getReplacementFor(String placeholder, ReplacementData data) {
        String subscript = placeholder.substring(4, placeholder.length() - 1);

        int from = Integer.MAX_VALUE;
        int to = Integer.MIN_VALUE;
        if (subscript.contains("..")) {
            String[] split = subscript.split("\\.\\.");

            from = Integer.parseInt(split[0]);
            to = Integer.parseInt(split[1]);
        } else {
            from = Integer.parseInt(subscript);
            to = from;
        }

        List<Object> arguments = data.getArguments();
        from = (from >= 0) ? from : arguments.size() + from;
        to = (to >= 0) ? to + 1 : arguments.size() + to + 1;
        return formatAll(getSubArrayOfMethodParameters(data.getTestMethod(), from, to), arguments.subList(from, to));
    }

    /**
     * Formats the given parameters and arguments to a comma-separated list of {@code $parameterName=$argumentName}.
     * Arguments {@link String} representation are therefore treated specially.
     *
     * @param parameters used to for formatting
     * @param arguments to be formatted
     * @return the formatted {@link String} of the given {@link Parameter}{@code []} and {@link List}{@code <Object>}
     */
    protected String formatAll(Parameter[] parameters, List<Object> arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < arguments.size(); idx++) {
            Parameter parameter = (parameters.length > idx) ? parameters[idx] : null;

            String parameterName = (parameter == null) ? "?" : parameter.getName();
            Object argument = arguments.get(idx);

            stringBuilder.append(parameterName).append("=").append(format(
                    new ParameterAndArgument((parameter == null) ? null : parameter.getType(),
                            (parameter == null) ? new Annotation[0] : parameter.getAnnotations(), argument)));
            if (idx < arguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    private Parameter[] getSubArrayOfMethodParameters(Method testMethod, int fromIndex, int toIndex) {
        Parameter[] parameters = testMethod.getParameters();
        if (parameters.length > 0 && !parameters[0].isNamePresent()) {
            logger.warning(String.format("Parameter names on method '%s' are not available"
                    + ". To store formal parameter names, compile the source file with the '-parameters' option"
                    + ". See also https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html",
                    testMethod));
        }
        return Arrays.copyOfRange(parameters, fromIndex, toIndex);
    }
}
