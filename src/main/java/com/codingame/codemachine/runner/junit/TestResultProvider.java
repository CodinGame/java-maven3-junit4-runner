package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class TestResultProvider extends RunListener {
    private final TestResultDtoFactory testResultDtoFactory;
    private final List<TestResultDto> results;

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private TestResultDto currentResult;

    TestResultProvider(List<TestResultDto> results) {
        this(results, null);
    }

    TestResultProvider(List<TestResultDto> results, TestResultDtoFactory testResultDtoFactory) {
        this.results = results;
        this.testResultDtoFactory = testResultDtoFactory != null ? testResultDtoFactory : new TestResultDtoFactory();
    }

    public void testStarted(Description description) {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(err, true));
        currentResult = null;
    }

    public void testFailure(Failure failure) {
        currentResult = testResultDtoFactory.create(false, failure.getDescription(), failure.getException());
    }

    public void testFinished(Description description) throws Exception {
        if (currentResult == null) {
            currentResult = testResultDtoFactory.create(true, description, null);
        }
        currentResult.setProgramStderr(new String(err.toByteArray()));
        currentResult.setProgramStdout(new String(out.toByteArray()));
        results.add(currentResult);
        err.close();
        out.close();
    }
}
