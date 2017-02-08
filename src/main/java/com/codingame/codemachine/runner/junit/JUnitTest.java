package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.TestResultDto;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JUnitTest {
    private static final String DEFAULT_OUTPUT = "-";
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?<class>[^#]+)(?:#(?<method>[^#]+))?");

    private final PrintStream realOut;
    private final PrintStream realErr;
    private final JUnitCore jUnitCore;
    private TestResultDto result;
    private boolean oneFailure;

    JUnitTest() {
        realOut = System.out;
        realErr = System.err;
        jUnitCore = new JUnitCore();
        oneFailure = false;
    }

    private boolean isOneFailure() {
        return this.oneFailure;
    }

    int run(String testcaseSpecification) {
        TestCase testCase = findRequest(testcaseSpecification);
        runTestCase(testCase);

        int statusCode = isOneFailure() ? 1 : 0;
        statusCode = generateResult() ? statusCode : 3;

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
            result = new TestResultDto();
            jUnitCore.addListener(new TestResultProvider(result));
            if (!testCase.run(jUnitCore)) {
                oneFailure = true;
            }
        }
        else {
            result = createTestNotFoundResult();
            oneFailure = true;
        }
    }

    private TestResultDto createTestNotFoundResult() {
        TestResultDto result = new TestResultDto();
        result.setSuccess(false);
        result.setNotFound(true);
        return result;
    }

    private boolean generateResult() {
        String resultOutput = System.getProperty("codingame.junit-runner.output", DEFAULT_OUTPUT);
        String resultStr = new Gson().toJson(result);
        if (DEFAULT_OUTPUT.equals(resultOutput)) {
            realOut.println(resultStr);
        }
        else {
            try {
                FileUtils.writeStringToFile(new File(resultOutput), resultStr);
            }
            catch (IOException e) {
                realErr.println(e.getMessage());
                return false;
            }
        }
        return true;
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
