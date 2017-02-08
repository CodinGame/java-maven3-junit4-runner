package com.codingame.codemachine.runner.junit.core;

import java.util.ArrayList;
import java.util.List;

public class TestResultDto {
    private boolean success;
    private boolean notFound;
    private List<RunLogDto> logs;
    private String stdout = "";
    private String stderr = "";

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<RunLogDto> getLogs() {
        if (logs == null) {
            setLogs(new ArrayList<>());
        }
        return logs;
    }

    public void setLogs(List<RunLogDto> logs) {
        this.logs = logs;
    }

    public void appendLogs(List<RunLogDto> logs) {
        getLogs().addAll(logs);
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound(boolean notFound) {
        this.notFound = notFound;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void appendStdout(String stdsout) {
        setStdout(getStdout() + stdsout);
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public void appendStderr(String stderr) {
        setStderr(getStderr() + stderr);
    }
}
