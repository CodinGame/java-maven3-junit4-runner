package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestResultProvider extends RunListener {
    private final TestResultDtoFactory testResultDtoFactory;
    private final TestResultDto result;

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private TestResultDto currentResult;

    TestResultProvider(TestResultDto result) {
        this(result, null);
    }

    TestResultProvider(TestResultDto result, TestResultDtoFactory testResultDtoFactory) {
        this.result = result;
        this.result.setSuccess(true);
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
        currentResult = testResultDtoFactory.create(false, failure.getException());
    }

    public void testFinished(Description description) throws Exception {
        if (currentResult == null) {
            currentResult = testResultDtoFactory.create(true, null);
        }
        result.appendStderr(new String(err.toByteArray()));
        result.appendStdout(new String(out.toByteArray()));
        result.appendLogs(currentResult.getLogs());
        result.setSuccess(result.isSuccess() && currentResult.isSuccess());
        err.close();
        out.close();
    }
}
