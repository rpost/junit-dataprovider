package com.tngtech.java.junit.dataprovider.vintage;

import java.util.Set;
import java.util.logging.Logger;

import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.vintage.engine.discovery.JUnit4DiscoveryRequestResolver;
import org.junit.vintage.engine.discovery.TestClassRequest;

public class DataProviderDiscoveryRequestResolver extends JUnit4DiscoveryRequestResolver {

    public DataProviderDiscoveryRequestResolver(EngineDescriptor engineDescriptor, Logger logger) {
        super(engineDescriptor, logger);
    }

    @Override
    protected void populateEngineDescriptor(Set<TestClassRequest> requests) {
        new DataProviderTestClassRequestResolver(engineDescriptor, logger).populateEngineDescriptorFrom(requests);
    }
}
