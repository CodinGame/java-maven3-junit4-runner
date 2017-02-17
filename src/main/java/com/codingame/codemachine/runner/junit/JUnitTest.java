package com.codingame.codemachine.runner.junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;

class JUnitTest {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?<class>[^#]+)(?:#(?<method>[^#]+))?");

    private final JUnitCore jUnitCore;
    private int statusCode;

    JUnitTest() {
        jUnitCore = new JUnitCore();
    }

    int run(String testcaseSpecification) {
        TestCase testCase = findRequest(testcaseSpecification);
        runTestCase(testCase);

        return statusCode;
    }

    TestCase findRequest(String testcaseSpecification) {
        TestCase request = null;
        Matcher matcher = COMMAND_PATTERN.matcher(testcaseSpecification);
        if (matcher.matches()) {
            try {
                Class<?> clazz = Class.forName(matcher.group("class"));
                String method = matcher.group("method");
                if (method != null) {
                    request = TestCase.createTestCase(Request.method(clazz, method), testcaseSpecification);
                }
                else {
                    request = TestCase.createTestCase(Request.aClass(clazz), testcaseSpecification);
                }
            }
            catch (ClassNotFoundException ignored) {
                request = TestCase.createTestCase();
            }
        }
        return request;
    }

    private void runTestCase(TestCase testCase) {
        if (testCase.exists()) {
            jUnitCore.addListener(new TestResultFormatter());
            if (testCase.run(jUnitCore)) {
                statusCode = 0;
            } else {
                statusCode = 1;
            }
        }
        else {
            statusCode = 2;
            System.err.println("Testcase not found \""+ testCase.description() + "\"");
        }
    }

    static class TestCase {
        static TestCase createTestCase() {
            return new TestCase(null, null);
        }

        static TestCase createTestCase(Request request, String description) {
            TestCase testCase = new TestCase(null, null);
            if (request != null) {
                Runner runner = request.getRunner();
                if (!(runner instanceof ErrorReportingRunner)) {
                    testCase = new TestCase(request, description);
                }
            }
            return testCase;
        }

        private final Request request;
        private final String description;

        private TestCase(Request request, String description) {
            this.request = request;
            this.description = description;
        }

        Request request() {
            return this.request;
        }

        String description() {
            return this.description;
        }

        boolean exists() {
            return request() != null;
        }

        boolean run(final JUnitCore jUnitCore) {
            return jUnitCore.run(request).wasSuccessful();
        }
    }

}
