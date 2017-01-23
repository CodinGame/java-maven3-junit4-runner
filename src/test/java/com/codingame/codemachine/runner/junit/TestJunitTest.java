package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.JUnitTest.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        List<TestCase> testCases = jUnitTest.findRequests(className);
        assertThat(testCases).hasSize(1);
        assertThat(testCases.get(0).exists()).isFalse();
    }

    @Test
    public void should_find_a_test_method() {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        String methodName = "myFirstTest";
        checkTestCase(className, methodName);
    }

    @Test
    public void should_find_many_test_methods() {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        String methodName0 = "myFirstTest";
        String methodName1 = "aSecondTest";
        List<TestCase> testCases =
            jUnitTest.findRequests(className + "#" + methodName0, className + "#" + methodName1);
        assertThat(testCases).hasSize(2);
        assertThat(testCases.get(0).exists()).isTrue();
        assertThat(testCases.get(0).description()).isEqualTo(className + "#" + methodName0);
        assertThat(testCases.get(1).exists()).isTrue();
        assertThat(testCases.get(1).description()).isEqualTo(className + "#" + methodName1);
    }

    @Test
    public void should_find_many_test_methods_even_with_errors() {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        String methodName0 = "myFirstTest";
        String methodName1 = "unknown";
        List<TestCase> testCases =
            jUnitTest.findRequests(className + "#" + methodName0, className + "#" + methodName1);
        assertThat(testCases).hasSize(2);
        assertThat(testCases.get(0).exists()).isTrue();
        assertThat(testCases.get(1).exists()).isFalse();
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
        List<TestCase> testCases = jUnitTest.findRequests(className + "#" + methodName);
        assertThat(testCases).hasSize(1);
        assertThat(testCases.get(0).exists()).isFalse();
    }

    private void checkTestCase(String className) throws ClassNotFoundException {
        List<TestCase> testCases = jUnitTest.findRequests(className);
        assertThat(testCases).hasSize(1);

        Class<?> clazz = Class.forName(className);
        int expectedTestCount = clazz.getDeclaredMethods().length;
        assertThat(testCases.get(0).exists()).isTrue();
        assertThat(testCases.get(0).description()).isEqualTo(className);
        int testCount = testCases.get(0).request().getRunner().testCount();
        assertThat(testCount).isEqualTo(expectedTestCount);
    }

    private void checkTestCase(String className, String methodName) {
        List<TestCase> testCases = jUnitTest.findRequests(className + "#" + methodName);
        assertThat(testCases).hasSize(1);
        assertThat(testCases.get(0).exists()).isTrue();
        assertThat(testCases.get(0).description()).isEqualTo(className + "#" + methodName);
        int testCount = testCases.get(0).request().getRunner().testCount();
        assertThat(testCount).isEqualTo(1);
    }

    @Test
    public void should_run_only_known_test_methods() {
        String className = "resources.simple.com.codingame.core.MyFirstTest";
        String methodName0 = "myFirstTest";
        String methodName1 = "unknown";

        JUnitTest jUnitTest = new JUnitTest();
        List<TestCase> testCases = jUnitTest.findRequests(className + "#" + methodName0, className + "#" + methodName1);
        jUnitTest.runTestCases(testCases);

        assertThat(jUnitTest.isOneFailure()).isTrue();
    }
}
