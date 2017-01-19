package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.RunLogDto;
import com.codingame.codemachine.runner.junit.core.RunLogStackTraceDto;
import com.codingame.codemachine.runner.junit.core.TestResultDto;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

public class JUnitTestListRunner {
    private static final String DEFAULT_OUTPUT = "-";

    private static RunLogDto parseThrowable(Throwable t) {
        RunLogDto runLog = new RunLogDto();
        runLog.setMessage(t.getMessage());
        List<RunLogStackTraceDto> stack = new ArrayList<>();
        for (StackTraceElement item : t.getStackTrace()) {
            RunLogStackTraceDto stackItem = new RunLogStackTraceDto();
            stackItem.setLine(item.getLineNumber());
            stackItem.setContainer(item.getClassName());
            stackItem.setFunction(item.getMethodName());
            stack.add(stackItem);
        }
        runLog.setStacktrace(stack);

        if (t.getCause() != null) {
            runLog.setCause(parseThrowable(t.getCause()));
        }
        return runLog;
    }

    private static TestResultDto parseTestResult(boolean success, Description description, Throwable t) {
        TestResultDto result = new TestResultDto();
        result.setSuccess(success);
        result.setNotFound(false);
        result.setTestReference(description.getClassName() + "#" + description.getMethodName());
        if (t != null) {
            result.setLogs(singletonList(parseThrowable(t)));
        }
        return result;
    }

    private static TestResultDto createTestNotFoundResult(String testReference) {
        TestResultDto result = new TestResultDto();
        result.setSuccess(false);
        result.setNotFound(true);
        result.setTestReference(testReference);
        return result;
    }

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?<class>[^#]+)(?:#(?<method>[^#]+))?");

    private static ByteArrayOutputStream out, err;

    public static void main(String... args) {
        PrintStream realOut = System.out;
        PrintStream realErr = System.err;

        List<TestResultDto> results = new ArrayList<>();

        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new RunListener() {
            private TestResultDto currentResult;

            public void testStarted(Description description) {
                out = new ByteArrayOutputStream();
                err = new ByteArrayOutputStream();
                System.setOut(new PrintStream(out, true));
                System.setErr(new PrintStream(err, true));
                currentResult = null;
            }

            public void testFailure(Failure failure) throws Exception {
                currentResult = parseTestResult(false, failure.getDescription(), failure.getException());
            }

            public void testFinished(Description description) throws Exception {
                if (currentResult == null) {
                    currentResult = parseTestResult(true, description, null);
                }
                currentResult.setProgramStderr(new String(err.toByteArray()));
                currentResult.setProgramStdout(new String(out.toByteArray()));
                err.close();
                out.close();
                results.add(currentResult);
            }
        });

        boolean successful = true;
        for (String arg : args) {
            Matcher matcher = COMMAND_PATTERN.matcher(arg);
            if (matcher.matches()) {
                try {
                    Class<?> clazz = Class.forName(matcher.group("class"));
                    String method = matcher.group("method");
                    Request request = null;
                    if (method != null) {
                        try {
                            clazz.getMethod(method);
                            request = Request.method(clazz, method);
                        }
                        catch (NoSuchMethodException | SecurityException ignored) {
                        }
                    }
                    else {
                        request = Request.aClass(clazz);
                    }

                    if (request != null) {
                        Result result = jUnitCore.run(request);
                        if (!result.wasSuccessful()) {
                            successful = false;
                        }
                    }
                    else {
                        results.add(createTestNotFoundResult(arg));
                        successful = false;
                    }
                }
                catch (ClassNotFoundException e) {
                    results.add(createTestNotFoundResult(arg));
                    successful = false;
                }
            }
            else {
                results.add(createTestNotFoundResult(arg));
                successful = false;
            }
        }
        int statusCode = successful ? 0 : 1;

        String resultOutput = System.getProperty("codingame.junit-runner.output", DEFAULT_OUTPUT);
        String resultStr = new Gson().toJson(results);
        if (DEFAULT_OUTPUT.equals(resultOutput)) {
            realOut.println(resultStr);
        }
        else {
            try {
                FileUtils.writeStringToFile(new File(resultOutput), resultStr);
            } catch (IOException e) {
                realErr.println(e.getMessage());
                statusCode = 3;
            }
        }

        System.exit(statusCode);
    }
}
