package com.codingame.codemachine.runner.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

import static org.assertj.core.api.Assertions.assertThat;

public class TestJunitTest {

    private JUnitTest jUnitTest;

    @Before
    public void setUp() {
        jUnitTest = new JUnitTest();
    }

    @Test
    public void should_find_a_test_class() throws ClassNotFoundException {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        checkTestCase(className);
    }

    @Test
    public void should_not_find_test_class_if_it_does_not_exist() throws ClassNotFoundException {
        String className = "unknown.ClassTest";
        Request request = jUnitTest.findRequest(className);
        assertThat(request).isNull();
    }

    @Test
    public void should_find_a_test_method() {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        String methodName = "myFirstTest";
        checkTestCase(className, methodName);
    }

    @Test
    public void should_find_a_test_method_regardless_of_runWith() {
        String className = "resources.run_with.io.vertx.blog.first.MyFirstVerticleTest";
        String methodName = "testMyApplication";
        checkTestCase(className, methodName);
    }

    @Test
    public void does_not_work_with_parameterized_test_class() {
        String className = "resources.parameterized.com.codingame.core.FibonacciTest";
        String methodName = "test";
        Request request = jUnitTest.findRequest(className + "#" + methodName);
        assertThat(request).isNotNull();
        assertThat(request.getRunner().getDescription().getChildren().get(0).getDisplayName()).startsWith("initializationError");
    }

    private void checkTestCase(String className) throws ClassNotFoundException {
        Request request = jUnitTest.findRequest(className);

        assertThat(request).isNotNull();
        assertThat(request.getRunner().getDescription().getDisplayName()).isEqualTo(className);

        Class<?> clazz = Class.forName(className);
        int expectedTestCount = clazz.getDeclaredMethods().length;
        int testCount = request.getRunner().testCount();
        assertThat(testCount).isEqualTo(expectedTestCount);
    }

    private void checkTestCase(String className, String methodName) {
        Request request = jUnitTest.findRequest(className + "#" + methodName);
        assertThat(request).isNotNull();
        Runner runner = request.getRunner();
        Description description = runner.getDescription();
        assertThat(description.getDisplayName()).isEqualTo(className);
        assertThat(description.getChildren().get(0).getDisplayName()).startsWith(methodName);
        int testCount = runner.testCount();
        assertThat(testCount).isEqualTo(1);
    }
}
