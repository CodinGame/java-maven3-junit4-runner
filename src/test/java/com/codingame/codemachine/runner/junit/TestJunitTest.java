package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.JUnitTest.TestCase;
import org.junit.Before;
import org.junit.Test;

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
        TestCase testCase = jUnitTest.findRequest(className);
        assertThat(testCase.exists()).isFalse();
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
        TestCase testCase = jUnitTest.findRequest(className + "#" + methodName);
        assertThat(testCase).isNotNull();
        assertThat(testCase.exists()).isFalse();
    }

    private void checkTestCase(String className) throws ClassNotFoundException {
        TestCase testCase = jUnitTest.findRequest(className);
        assertThat(testCase).isNotNull();

        Class<?> clazz = Class.forName(className);
        int expectedTestCount = clazz.getDeclaredMethods().length;
        assertThat(testCase.exists()).isTrue();
        assertThat(testCase.description()).isEqualTo(className);
        int testCount = testCase.request().getRunner().testCount();
        assertThat(testCount).isEqualTo(expectedTestCount);
    }

    private void checkTestCase(String className, String methodName) {
        TestCase testCase = jUnitTest.findRequest(className + "#" + methodName);
        assertThat(testCase).isNotNull();
        assertThat(testCase.exists()).isTrue();
        assertThat(testCase.description()).isEqualTo(className + "#" + methodName);
        int testCount = testCase.request().getRunner().testCount();
        assertThat(testCount).isEqualTo(1);
    }
}
