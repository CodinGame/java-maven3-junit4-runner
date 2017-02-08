package com.codingame.codemachine.runner.junit;

import com.codingame.codemachine.runner.junit.core.RunLogDto;
import com.codingame.codemachine.runner.junit.core.TestResultDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResultDtoFactoryTest {

    @Test
    public void should_parse_a_simple_testsuite_success() {
        TestResultDto testResultDto = new TestResultDtoFactory().create(true, null);
        assertThat(testResultDto.isSuccess()).isTrue();
        assertThat(testResultDto.isNotFound()).isFalse();
    }

    @Test
    public void should_parse_a_simple_testsuite_fail() {
        TestResultDto testResultDto = new TestResultDtoFactory().create(false, null);
        assertThat(testResultDto.isSuccess()).isFalse();
        assertThat(testResultDto.isNotFound()).isFalse();
    }

    @Test
    public void should_parse_a_full_fail() {
        RuntimeException cause = new RuntimeException("Cause");
        TestResultDto testResultDto =
            new TestResultDtoFactory().create(false, new Throwable("Problem", cause));
        assertThat(testResultDto.isSuccess()).isFalse();
        assertThat(testResultDto.isNotFound()).isFalse();
        assertThat(testResultDto.getLogs()).hasSize(1);
        RunLogDto runLogDto = testResultDto.getLogs().get(0);
        assertThat(runLogDto.getMessage()).isEqualTo("Problem");
        assertThat(runLogDto.getCause().getMessage()).isEqualTo("Cause");
    }
}
