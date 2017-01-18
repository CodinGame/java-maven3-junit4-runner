package com.codingame.codemachine.runner.junit.core;

import java.util.List;

public class RunLogDto {
    private String message;
    private List<RunLogStackTraceDto> stacktrace;
    private RunLogDto cause;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RunLogStackTraceDto> getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(List<RunLogStackTraceDto> stacktrace) {
        this.stacktrace = stacktrace;
    }

    public RunLogDto getCause() {
        return cause;
    }

    public void setCause(RunLogDto cause) {
        this.cause = cause;
    }
}
