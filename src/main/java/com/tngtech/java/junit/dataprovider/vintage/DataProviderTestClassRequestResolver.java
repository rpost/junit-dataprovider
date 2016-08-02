package com.tngtech.java.junit.dataprovider.vintage;

import java.util.Set;
import java.util.logging.Logger;

import org.junit.platform.engine.TestDescriptor;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;
import org.junit.vintage.engine.discovery.TestClassRequest;
import org.junit.vintage.engine.discovery.TestClassRequestResolver;

public class DataProviderTestClassRequestResolver extends TestClassRequestResolver {

    DataProviderTestClassRequestResolver(TestDescriptor engineDescriptor, Logger logger) {
        super(engineDescriptor, logger);
    }

    @Override
    public void populateEngineDescriptorFrom(Set<TestClassRequest> requests) {
        RunnerBuilder runnerBuilder = new DataProviderDefensiveAllDefaultPossibilitiesBuilder();
        for (TestClassRequest request : requests) {
            Class<?> testClass = request.getTestClass();
            Runner runner = runnerBuilder.safeRunnerForClass(testClass);
            if (runner != null) {
                addRunnerTestDescriptor(request, testClass, runner);
            }
        }
    }
}
