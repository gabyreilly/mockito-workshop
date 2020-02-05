package com.gabyreilly.workshop.second;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitTest {

    @Test
    public void greenTest() {
        String a = "hello";
        assertEquals("hello", a);
    }

    @Test
    public void failedAssertion() {
        String a = "hello";
        assertEquals("goodbye", a);
    }

    @Test
    public void failedWithException() {
        int a = 10;
        int b = 0;
        int c = a/b;
        assertEquals(1, c);
    }
}
