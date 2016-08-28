package com.tngtech.java.junit.dataprovider.engine;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.runner.Description;
import org.junit.runner.Runner;

public class DataProviderTestDescriptor extends AbstractTestDescriptor {

    private final Description description;
    private final Runner runner;

    // TODO static methods with proper names instead of constructors?
    public DataProviderTestDescriptor(TestDescriptor parent, String segmentType, TestSource source, Description description,
            Runner runner) {
        this(parent, segmentType, description.getDisplayName(), source, description, runner);
    }

    public DataProviderTestDescriptor(TestDescriptor parent, String segmentType, String displayName, TestSource source,
            Description description, Runner runner) {
        super(parent.getUniqueId().append(segmentType, description.getDisplayName()), displayName);
        setSource(source);
        this.description = description;
        this.runner = runner;
    }

    @Override
    public boolean isContainer() {
        return description.isSuite();
    }

    @Override
    public boolean isTest() {
        return description.isTest();
    }

    public Description getDescription() {
        return description;
    }

    public Runner getRunner() {
        return runner;
    }
}
