package com.tngtech.java.junit.dataprovider.engine;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.junit.platform.engine.TestExecutionResult.successful;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

//@ThreadSafe // TODO
public class DataProviderRunListener extends RunListener {

    private static final Logger logger = Logger.getLogger(DataProviderRunListener.class.getName());

    private final DataProviderTestDescriptor runnerTestDescriptor;
    private final EngineExecutionListener engineExecutionListener;

    private final Map<Description, DataProviderTestDescriptor> descriptionMapping;

    private final Map<TestDescriptor, TestExecutionResult> executionResults = new LinkedHashMap<>();
    private final Set<TestDescriptor> skippedDescriptors = new LinkedHashSet<>();
    private final Set<TestDescriptor> startedDescriptors = new LinkedHashSet<>();
    private final Set<TestDescriptor> finishedDescriptors = new LinkedHashSet<>();

    public DataProviderRunListener(DataProviderTestDescriptor runnerTestDescriptor, EngineExecutionListener engineExecutionListener) {
        this.runnerTestDescriptor = runnerTestDescriptor;
        this.engineExecutionListener = engineExecutionListener;

        descriptionMapping = calculateDescriptionMapping(runnerTestDescriptor);
    }

    private Map<Description, DataProviderTestDescriptor> calculateDescriptionMapping(DataProviderTestDescriptor runnerTestDescriptor) {
        Map<Description, List<DataProviderTestDescriptor>> mapping = concat(of(runnerTestDescriptor),
                runnerTestDescriptor.getAllDescendants().stream()).map(DataProviderTestDescriptor.class::cast)
                        .collect(groupingBy(DataProviderTestDescriptor::getDescription));
        Stream<Entry<Description, List<DataProviderTestDescriptor>>> filtered = mapping.entrySet().stream()
                .filter(e -> e.getValue().size() != 1);
        if (filtered.count() > 0) {
            String message = filtered.map(e -> String.format("Id '%s' for descriptions '%s'", e.getKey(), e.getValue()))
                    .reduce("Multiple descriptions with the same identifier found:", (a, b) -> a + "\n  * " + b);
            logger.severe(message);
            throw new PreconditionViolationException(message);
        }
        return mapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().get(0)));
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
        // TODO Auto-generated method stub
        System.out.println("started class " + description);

        engineExecutionListener.executionStarted(runnerTestDescriptor); // TODO what if description does not match?
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        engineExecutionListener.executionStarted(runnerTestDescriptor); // TODO NPE ???

        System.out.println("finished class with " + result);
        super.testRunFinished(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        // TODO Auto-generated method stub
        System.out.println("started " + description);

        engineExecutionListener.executionStarted(descriptionMapping.get(description)); // TODO NPE ???
    }

    @Override
    public void testFinished(Description description) throws Exception {
        engineExecutionListener.executionFinished(descriptionMapping.get(description), TestExecutionResult.successful()); // TODO NPE ???

        System.out.println("finished " + description);
        // TODO Auto-generated method stub
        super.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        engineExecutionListener.executionFinished(descriptionMapping.get(failure.getDescription()),
                TestExecutionResult.failed(failure.getException()));
        // TODO NPE ??? and failure contains more good information?

        System.out.println("failed " + failure.getDescription());
        // TODO Auto-generated method stub
        super.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        // TODO Auto-generated method stub
        super.testAssumptionFailure(failure);
        System.out.println("aborted " + failure.getDescription());

        engineExecutionListener.executionFinished(descriptionMapping.get(failure.getDescription()),
                TestExecutionResult.aborted(failure.getException()));
        // TODO NPE ??? and failure contains more good information?
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        // TODO Auto-generated method stub
        super.testIgnored(description);
        System.out.println("ignored " + description);

        engineExecutionListener.executionSkipped(descriptionMapping.get(description), "TODO reason"); // TODO
        // TODO NPE ??? and reason com from annotation?
    }


    // TODO
    // /**
    // * Returns the {@link TestDescriptor} that represents the specified {@link Description}.
    // *
    // * <p>
    // * There are edge cases where multiple {@link Description Descriptions} with the same {@code uniqueId} exist, e.g. when using
    // overloaded
    // * methods to define {@linkplain org.junit.experimental.theories.Theory theories}. In this case, we try to find the correct
    // * {@link TestDescriptor} by checking for object identity on the {@link Description} it represents.
    // *
    // * @param description the {@code Description} to look up
    // */
    // Optional<? extends TestDescriptor> lookupTestDescriptor(Description description) {
    // Optional<? extends TestDescriptor> testDescriptor = lookupInternal(description);
    // if (!testDescriptor.isPresent()) {
    // logger.warning(() -> format("Runner %s on class %s reported event for unknown Description: %s. It will be ignored.",
    // runnerTestDescriptor.getRunner().getClass().getName(), //
    // runnerTestDescriptor.getTestClass().getName(), //
    // description));
    // }
    // return testDescriptor;
    // }

    // private Optional<? extends TestDescriptor> lookupInternal(Description description) {
    // List<VintageTestDescriptor> descriptors = descriptionToDescriptors.get(description);
    // if (descriptors == null) {
    // return Optional.empty();
    // }
    // if (descriptors.size() == 1) {
    // return Optional.of(getOnlyElement(descriptors));
    // }
    //        // @formatter:off
    //        return descriptors.stream()
    //                .filter(testDescriptor -> description == testDescriptor.getDescription())
    //                .findFirst();
    //        // @formatter:on
    // }

    void markSkipped(TestDescriptor testDescriptor) {
        skippedDescriptors.add(testDescriptor);
    }

    boolean isNotSkipped(TestDescriptor testDescriptor) {
        return !isSkipped(testDescriptor);
    }

    boolean isSkipped(TestDescriptor testDescriptor) {
        return skippedDescriptors.contains(testDescriptor);
    }

    void markStarted(TestDescriptor testDescriptor) {
        startedDescriptors.add(testDescriptor);
    }

    boolean isNotStarted(TestDescriptor testDescriptor) {
        return !startedDescriptors.contains(testDescriptor);
    }

    void markFinished(TestDescriptor testDescriptor) {
        finishedDescriptors.add(testDescriptor);
    }

    boolean isNotFinished(TestDescriptor testDescriptor) {
        return !isFinished(testDescriptor);
    }

    boolean isFinished(TestDescriptor testDescriptor) {
        return finishedDescriptors.contains(testDescriptor);
    }

    boolean areAllFinishedOrSkipped(Set<? extends TestDescriptor> testDescriptors) {
        return testDescriptors.stream().allMatch(this::isFinishedOrSkipped);
    }

    boolean isFinishedOrSkipped(TestDescriptor testDescriptor) {
        return isFinished(testDescriptor) || isSkipped(testDescriptor);
    }

    void storeResult(TestDescriptor testDescriptor, TestExecutionResult result) {
        executionResults.put(testDescriptor, result);
    }

    TestExecutionResult getStoredResultOrSuccessful(TestDescriptor testDescriptor) {
        return executionResults.getOrDefault(testDescriptor, successful());
    }

}