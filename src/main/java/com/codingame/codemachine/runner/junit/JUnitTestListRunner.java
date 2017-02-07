package com.codingame.codemachine.runner.junit;

public class JUnitTestListRunner {
    public static void main(String... args) {
        JUnitTest jUnitTest = new JUnitTest();
        int statusCode = jUnitTest.run(args);
        System.exit(statusCode);
    }
}
