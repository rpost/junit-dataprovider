package com.tngtech.java.junit.dataprovider.engine;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.descriptor.JavaClassSource;
import org.junit.platform.engine.support.descriptor.JavaMethodSource;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

public class DataProviderTestEngine implements TestEngine {

    private static final Logger logger = Logger.getLogger(DataProviderTestEngine.class.getName());

    private static final String ENGINE_ID = "junit-dataprovider";

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        EngineDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "JUnit dataprovider");

        // TestClassCollector collector = collectTestClasses(discoveryRequest);
        // Set<TestClassRequest> requests = filterAndConvertToTestClassRequests(discoveryRequest, collector);
        List<Class<?>> testClasses = discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
                .map(selector -> selector.getJavaClass()).collect(toList());
        // TODO other selectors possible with @RunWith(JUnitPlattform.class) ??? => warning or exception if other selector is present
        // TODO search for type DiscoverySelector
        for (Class<?> testClass : testClasses) {
            DataProviderRunner runner;
            try {
                runner = new DataProviderRunner(testClass);
            } catch (InitializationError e) {
                throw new JUnitException(e.getMessage(), e);
            }

            DataProviderTestDescriptor classChild = new DataProviderTestDescriptor(engineDescriptor, "class", testClass.getSimpleName(),
                    new JavaClassSource(testClass), runner.getDescription(), runner);
            engineDescriptor.addChild(classChild);

            for (FrameworkMethod testMethod : runner.computeTestMethods()) {
                Description child = runner.describeChild(testMethod);
                classChild.addChild(
                        new DataProviderTestDescriptor(classChild, "method", new JavaMethodSource(testMethod.getMethod()), child, runner));
            }
        }
        // TODO Filters?
        return engineDescriptor;
    }

    @Override
    public void execute(ExecutionRequest request) {
        EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
        TestDescriptor engineTestDescriptor = request.getRootTestDescriptor();
        engineExecutionListener.executionStarted(engineTestDescriptor);

        // RunnerExecutor runnerExecutor = new RunnerExecutor(engineExecutionListener, logger);

        // executeAllChildren(runnerExecutor, engineTestDescriptor);
        // @formatter:off
        engineTestDescriptor.getChildren()
        .stream()
        .map(DataProviderTestDescriptor.class::cast)
        //            @Override
        //            public Set<TestTag> getTags() {
        //                Set<TestTag> result = new LinkedHashSet<>();
        //                getParent().ifPresent(parent -> result.addAll(parent.getTags()));
        //                // @formatter:off
        //                getDeclaredCategories().ifPresent(categoryClasses ->
        //                    stream(categoryClasses)
        //                        .map(ReflectionUtils::getAllAssignmentCompatibleClasses)
        //                        .flatMap(Collection::stream)
        //                        .distinct()
        //                        .map(Class::getName)
        //                        .map(TestTag::create)
        //                        .forEachOrdered(result::add)
        //                );
        //                // @formatter:on
        // return result;
        // }
        //
        // private Optional<Class<?>[]> getDeclaredCategories() {
        // Category annotation = description.getAnnotation(Category.class);
        // return Optional.ofNullable(annotation).map(Category::value);
        // }
        //
        // private static Optional<JavaSource> toJavaSource(Description description) {
        // Class<?> testClass = description.getTestClass();
        // if (testClass != null) {
        // String methodName = description.getMethodName();
        // if (methodName != null) {
        // JavaMethodSource javaMethodSource = toJavaMethodSource(testClass, methodName);
        // if (javaMethodSource != null) {
        // return Optional.of(javaMethodSource);
        // }
        // }
        // return Optional.of(new JavaClassSource(testClass));
        // }
        // return Optional.empty();
        // }
        //
        // private static JavaMethodSource toJavaMethodSource(Class<?> testClass, String methodName) {
        // if (methodName.contains("[") && methodName.endsWith("]")) {
        // // special case for parameterized tests
        // return toJavaMethodSource(testClass, methodName.substring(0, methodName.indexOf("[")));
        // }
        // else {
        // List<Method> methods = findMethods(testClass, where(Method::getName, isEqual(methodName)));
        // return (methods.size() == 1) ? new JavaMethodSource(getOnlyElement(methods)) : null;
        // }
        // }
        .forEach(runnerTestDescriptor -> { // TODO name it runner/classTestDescriptor?
            Set<? extends TestDescriptor> classDescendants = runnerTestDescriptor.getAllDescendants();

            // @formatter:off
            Map<Description, List<DataProviderTestDescriptor>> descriptionToDescriptors = concat(Stream.of(runnerTestDescriptor), classDescendants.stream())
                    .map(DataProviderTestDescriptor.class::cast)
                    .collect(groupingBy(DataProviderTestDescriptor::getDescription));
            // @formatter:on

            // TestRun testRun = new TestRun(runnerTestDescriptor, logger);
            JUnitCore core = new JUnitCore();
            // core.addListener(new RunListenerAdapter(testRun, engineExecutionListener)); // TODO
            core.addListener(new DataProviderRunListener(runnerTestDescriptor, engineExecutionListener));
            engineExecutionListener.executionStarted(runnerTestDescriptor);
            try {
                core.run(Request.runner(runnerTestDescriptor.getRunner())); // TODO runner from description
            } catch (Throwable t) {
                // reportUnexpectedFailure(testRun, runnerTestDescriptor, TestExecutionResult.failed(t));
                TestExecutionResult result = TestExecutionResult.failed(t);

                // if (testRun.isNotStarted(runnerTestDescriptor)) { // TODO
                // engineExecutionListener.executionStarted(runnerTestDescriptor);
                // }
                engineExecutionListener.executionFinished(runnerTestDescriptor, result);
            }
        });
        // @formatter:on

        engineExecutionListener.executionFinished(engineTestDescriptor, TestExecutionResult.successful());
    }
}
