package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.RunLogDto;
import com.codingame.codemachine.runner.junit.core.RunLogStackTraceDto;
import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

class TestResultDtoFactory {
    private RunLogDto parseThrowable(Throwable t) {
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

    TestResultDto create(boolean success, Throwable t) {
        TestResultDto result = new TestResultDto();
        result.setSuccess(success);
        result.setNotFound(false);
        if (t != null) {
            result.setLogs(singletonList(parseThrowable(t)));
        }
        return result;
    }
}
