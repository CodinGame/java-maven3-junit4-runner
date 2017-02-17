package com.codingame.codemachine.runner.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JUnitTest {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?<class>[^#]+)(?:#(?<method>[^#]+))?");

    int run(String testcaseSpecification) {
        Request request = findRequest(testcaseSpecification);
        if (request == null) {
            System.err.println(String.format("Testcase \"%s\" not found", testcaseSpecification));
            return 2;
        }
        return runTestCase(request);
    }

    Request findRequest(String testcaseSpecification) {
        Request request = null;
        Matcher matcher = COMMAND_PATTERN.matcher(testcaseSpecification);
        if (matcher.matches()) {
            try {
                Class<?> clazz = Class.forName(matcher.group("class"));
                String method = matcher.group("method");
                if (method != null) {
                    request = Request.method(clazz, method);
                }
                else {
                    request = Request.aClass(clazz);
                }
            }
            catch (ClassNotFoundException ignored) {}
        }
        return request;
    }

    private int runTestCase(Request request) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new TestResultFormatter());
        return jUnitCore.run(request).wasSuccessful() ? 0 : 1;
    }
}
