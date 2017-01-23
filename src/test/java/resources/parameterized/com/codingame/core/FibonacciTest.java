package resources.parameterized.com.codingame.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class FibonacciTest {

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
    }

    private int input;
    private int expected;

    public FibonacciTest(int input, int expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() {
        assertEquals(expected, Fibonacci.compute(input));
    }
}

class Fibonacci {
    static int compute(int n) {
        if (n <= 1) {
            return n;
        }
        return compute(n-1) + compute(n-2);
    }
}
