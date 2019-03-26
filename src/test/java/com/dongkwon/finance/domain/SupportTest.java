package com.dongkwon.finance.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class SupportTest {
    @Test
    public void illegalArgumentExceptionTest() {
        assertException(null, 1, 1.0, new Institute());
        assertException(2000, null, 1.0, new Institute());
        assertException(2000, 1, 1.0, null);
        assertException(2000, 1, null, new Institute());
        assertException(0, 1, 1.0, new Institute());
        assertException(2000, 0, 1.0, new Institute());
        assertException(2000, 13, 1.0, new Institute());
    }

    private static void assertException(Integer year, Integer month, Double amount, Institute institute) {
        try {
            Support.of(year, month, amount, institute);
            fail("Expected exception: IllegalArgumentException");
        } catch (Exception ex) {
            assertThat(ex instanceof IllegalArgumentException).isTrue();
        }
    }
}
