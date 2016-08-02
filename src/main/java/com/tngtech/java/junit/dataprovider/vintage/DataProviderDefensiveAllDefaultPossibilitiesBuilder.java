package com.tngtech.java.junit.dataprovider.vintage;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.junit.internal.builders.JUnit4Builder;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.runner.Runner;
import org.junit.vintage.engine.discovery.DefensiveAllDefaultPossibilitiesBuilder;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

public class DataProviderDefensiveAllDefaultPossibilitiesBuilder extends DefensiveAllDefaultPossibilitiesBuilder {

    private final DefensiveJUnit4Builder defensiveJUnit4Builder;

    public DataProviderDefensiveAllDefaultPossibilitiesBuilder() {
        defensiveJUnit4Builder = new DataProviderDefensiveJUnit4Builder();
    }

    @Override
    protected JUnit4Builder junit4Builder() {
        return defensiveJUnit4Builder;
    }

    /** Customization of {@link DefensiveJUnit4Builder} that handles classes containing any {@code @}{@link UseDataProvider} annotation. */
    private static class DataProviderDefensiveJUnit4Builder extends DefensiveJUnit4Builder {
        private static final Predicate<Method> hasUseDataProviderAnnotation = new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return method.isAnnotationPresent(UseDataProvider.class);
            }
        };

        @Override
        public Runner runnerForClass(Class<?> testClass) throws Throwable {
            if (containsUseDataProviderTestMethods(testClass)) {
                return new DataProviderRunner(testClass);
            }
            return super.runnerForClass(testClass);
        }

        private boolean containsUseDataProviderTestMethods(Class<?> testClass) {
            return !ReflectionUtils.findMethods(testClass, hasUseDataProviderAnnotation).isEmpty();
        }
    }
}
