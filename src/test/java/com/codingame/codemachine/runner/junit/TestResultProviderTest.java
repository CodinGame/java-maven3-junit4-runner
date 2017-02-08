package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.RunLogDto;
import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestResultProviderTest {

    @Test
    public void should_create_TestResultDto_when_testFinished() throws Exception {
        TestResultDtoFactory testResultDtoFactory = mock(TestResultDtoFactory.class);
        TestResultDto result = new TestResultDto();
        TestResultProvider testResultProvider = new TestResultProvider(result, testResultDtoFactory);
        TestResultDto testResultDto = new TestResultDto();
        testResultDto.setSuccess(true);
        when(testResultDtoFactory.create(true, null)).thenReturn(testResultDto);

        String className = "com.codingame.test.MyTest";
        String test = "theTest";
        Description testDescription = Description.createTestDescription(className, test);
        testResultProvider.testStarted(testDescription);
        String stdOutput = "This is a test output on stdout";
        System.out.println(stdOutput);
        String stdError = "This is a test output on stderr";
        System.err.println(stdError);
        testResultProvider.testFinished(testDescription);

        assertThat(result.getStdout()).isEqualTo(stdOutput + "\n");
        assertThat(result.getStderr()).isEqualTo(stdError + "\n");
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void should_create_TestResultDto_when_testFailure() throws Exception {
        TestResultDtoFactory testResultDtoFactory = mock(TestResultDtoFactory.class);
        TestResultDto result = new TestResultDto();
        TestResultProvider testResultProvider = new TestResultProvider(result, testResultDtoFactory);
        Throwable problem = new Throwable("Problem");
        RunLogDto log = new RunLogDto();
        log.setMessage("Fake Log");
        TestResultDto testResultDto = new TestResultDto();
        testResultDto.setLogs(Collections.singletonList(log));
        testResultDto.setSuccess(false);
        when(testResultDtoFactory.create(false, problem)).thenReturn(testResultDto);

        String className = "com.codingame.test.MyTest";
        String test = "theTest";
        Description testDescription = Description.createTestDescription(className, test);
        Failure failure = new Failure(testDescription, problem);
        testResultProvider.testStarted(testDescription);
        testResultProvider.testFailure(failure);
        testResultProvider.testFinished(testDescription);

        assertThat(result.getLogs()).hasSize(1);
        assertThat(result.getLogs().get(0).getMessage()).isEqualTo("Fake Log");
        assertThat(result.isSuccess()).isFalse();
    }
}
