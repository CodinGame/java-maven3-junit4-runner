package com.codingame.codemachine.runner.junit;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestResultFormatter extends RunListener {
    @Override
    public void testFailure(Failure failure) {
        failure.getException().printStackTrace();
    }
}
