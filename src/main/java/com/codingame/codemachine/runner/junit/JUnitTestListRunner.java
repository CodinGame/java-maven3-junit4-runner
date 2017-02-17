package com.codingame.codemachine.runner.junit;

public class JUnitTestListRunner {
    public static void main(String... args) {
        System.exit(new JUnitTest().run(args[0]));
    }
}
