package com.tngtech.junit.dataprovider.placeholder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationBasedArgumentPlaceholder extends AbstractArgumentPlaceholder {

    public AnnotationBasedArgumentPlaceholder() {
        super("%aa\\[(-?[0-9]+|-?[0-9]+\\.\\.-?[0-9]+)\\]");
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
        return formatAll(getParametersAndArguments(data.getTestMethod(), arguments, from, to));
    }

    private List<ParameterAndArgument> getParametersAndArguments(Method testMethod, List<Object> arguments, int from, int to) {
        Annotation[][] parameterAnnotations = testMethod.getParameterAnnotations();
        Class<?>[] parameterTypes = testMethod.getParameterTypes();

        List<ParameterAndArgument> result = new ArrayList<ParameterAndArgument>();
        for (int idx = 0; idx < arguments.size() && idx < parameterTypes.length; idx++) { // TODO test!
            result.add(new ParameterAndArgument(parameterTypes[idx], parameterAnnotations[idx], arguments.get(idx)));
        }
        return result;
    }

    /**
     * Formats the given parameters and arguments to a comma-separated list of {@code $parameterName=$argumentName}.
     * Arguments {@link String} representation are therefore treated specially.
     *
     * @param parameterAndArguments parameter types and annotation as well as arguments to be formatted
     * @return the formatted {@link String} of the given parameters and arguments
     */
    protected String formatAll(List<ParameterAndArgument> parameterAndArguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < parameterAndArguments.size(); idx++) {
            ParameterAndArgument parameterAndArgument = parameterAndArguments.get(idx);

            stringBuilder.append(format(parameterAndArgument));

            if (idx < parameterAndArguments.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
