package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.RunLogDto;
import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.Test;
import org.junit.runner.Description;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResultDtoFactoryTest {

    @Test
    public void should_parse_a_simple_testsuite_success() {
        String testCase = "com.codingame.test.MyTest";
        Description testDescription = Description.createSuiteDescription(testCase);
        TestResultDto testResultDto = new TestResultDtoFactory().create(true, testDescription, null);
        assertThat(testResultDto.isSuccess()).isTrue();
        assertThat(testResultDto.isNotFound()).isFalse();
        assertThat(testResultDto.getTestReference()).isEqualTo(testCase);
    }

    @Test
    public void should_parse_a_simple_testcase_success() {
        String className = "com.codingame.test.MyTest";
        String test = "theTest";
        Description testDescription = Description.createTestDescription(className, test);
        TestResultDto testResultDto = new TestResultDtoFactory().create(true, testDescription, null);
        assertThat(testResultDto.isSuccess()).isTrue();
        assertThat(testResultDto.isNotFound()).isFalse();
        assertThat(testResultDto.getTestReference()).isEqualTo(className + "#" + test);
    }

    @Test
    public void should_parse_a_simple_fail() {
        String testCase = "com.codingame.test.MyTest";
        Description testDescription = Description.createSuiteDescription(testCase);
        TestResultDto testResultDto = new TestResultDtoFactory().create(false, testDescription, null);
        assertThat(testResultDto.isSuccess()).isFalse();
        assertThat(testResultDto.isNotFound()).isFalse();
        assertThat(testResultDto.getTestReference()).isEqualTo(testCase);
    }

    @Test
    public void should_parse_a_full_fail() {
        String testCase = "com.codingame.test.MyTest";
        Description testDescription = Description.createSuiteDescription(testCase);
        RuntimeException cause = new RuntimeException("Cause");
        TestResultDto testResultDto =
            new TestResultDtoFactory().create(false, testDescription, new Throwable("Problem", cause));
        assertThat(testResultDto.isSuccess()).isFalse();
        assertThat(testResultDto.isNotFound()).isFalse();
        assertThat(testResultDto.getTestReference()).isEqualTo(testCase);
        assertThat(testResultDto.getLogs()).hasSize(1);
        RunLogDto runLogDto = testResultDto.getLogs().get(0);
        assertThat(runLogDto.getMessage()).isEqualTo("Problem");
        assertThat(runLogDto.getCause().getMessage()).isEqualTo("Cause");
    }
}
