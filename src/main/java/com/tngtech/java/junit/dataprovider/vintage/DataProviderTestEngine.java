package com.tngtech.java.junit.dataprovider.vintage;

import java.util.logging.Logger;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.vintage.engine.VintageTestEngine;

public class DataProviderTestEngine extends VintageTestEngine {

    private static final Logger logger = Logger.getLogger(DataProviderTestEngine.class.getName());

    @Override
    public String getId() {
        return "junit-dataprovider";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        EngineDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "JUnit dataprovider");
        new DataProviderDiscoveryRequestResolver(engineDescriptor, logger).resolve(discoveryRequest);
        return engineDescriptor;
    }
}
