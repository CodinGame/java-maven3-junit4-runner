package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.TestResultDto;
import junit.framework.TestFailure;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestResultProviderTest {

    @Test
    public void should_create_TestResultDto_when_testFinished() throws Exception {
        String className = "com.codingame.test.MyTest";
        String test = "theTest";
        Description testDescription = Description.createTestDescription(className, test);

        TestResultDtoFactory testResultDtoFactory = mock(TestResultDtoFactory.class);
        List<TestResultDto> results = new ArrayList<>();
        TestResultProvider testResultProvider = new TestResultProvider(results, testResultDtoFactory);
        TestResultDto testResultDto = new TestResultDto();
        when(testResultDtoFactory.create(true, testDescription, null)).thenReturn(testResultDto);

        testResultProvider.testStarted(testDescription);
        String stdOutput = "This is a test output on stdout";
        System.out.println(stdOutput);
        String stdError = "This is a test output on stderr";
        System.err.println(stdError);
        testResultProvider.testFinished(testDescription);

        assertThat(results).hasSize(1);
        TestResultDto actualTestResultDto = results.get(0);
        assertThat(actualTestResultDto).isSameAs(testResultDto);
        assertThat(actualTestResultDto.getProgramStdout()).isEqualTo(stdOutput + "\n");
        assertThat(actualTestResultDto.getProgramStderr()).isEqualTo(stdError + "\n");
    }

    @Test
    public void should_create_TestResultDto_when_testFailure() throws Exception {
        String className = "com.codingame.test.MyTest";
        String test = "theTest";
        Description testDescription = Description.createTestDescription(className, test);

        TestResultDtoFactory testResultDtoFactory = mock(TestResultDtoFactory.class);
        List<TestResultDto> results = new ArrayList<>();
        TestResultProvider testResultProvider = new TestResultProvider(results, testResultDtoFactory);
        Throwable problem = new Throwable("Problem");
        TestResultDto testResultDto = new TestResultDto();
        when(testResultDtoFactory.create(false, testDescription, problem)).thenReturn(testResultDto);

        Failure failure = new Failure(testDescription, problem);
        testResultProvider.testStarted(testDescription);
        testResultProvider.testFailure(failure);
        testResultProvider.testFinished(testDescription);

        assertThat(results).hasSize(1);
        TestResultDto actualTestResultDto = results.get(0);
        assertThat(actualTestResultDto).isSameAs(testResultDto);
    }
}
