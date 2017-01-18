package com.codingame.codemachine.runner.junit.core;

import java.util.List;

public class TestResultDto {
    private String testReference;
    private boolean success;
    private boolean notFound;
    private List<RunLogDto> logs;
    private String programStdout;
    private String programStderr;

    public String getTestReference() {
        return testReference;
    }

    public void setTestReference(String testReference) {
        this.testReference = testReference;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<RunLogDto> getLogs() {
        return logs;
    }

    public void setLogs(List<RunLogDto> logs) {
        this.logs = logs;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }

    public String getProgramStdout() {
        return programStdout;
    }

    public void setProgramStdout(String programStdout) {
        this.programStdout = programStdout;
    }

    public String getProgramStderr() {
        return programStderr;
    }

    public void setProgramStderr(String programStderr) {
        this.programStderr = programStderr;
    }
}
