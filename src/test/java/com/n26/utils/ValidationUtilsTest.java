package com.n26.utils;

import com.n26.model.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {
    private static Transaction tx = new Transaction();

    @Before
    public void setUp() {
        tx.setAmount(new BigDecimal("100.9809"));
        tx.setTimestamp(Instant.now());
    }

    @Test
    public void testIsFutureTransaction() {
        tx.setTimestamp(Instant.now().plusMillis(10L));
        assertTrue(ValidationUtils.isFutureTransaction.apply(tx));
    }

    @Test
    public void testIsVeryOldTransaction() {
        assertFalse(ValidationUtils.isVeryOldTransaction.apply(tx));

        tx.setTimestamp(Instant.now().minusMillis(60010L));
        assertTrue(ValidationUtils.isVeryOldTransaction.apply(tx));

        tx.setTimestamp(Instant.now().plusMillis(60000L));
        assertFalse(ValidationUtils.isVeryOldTransaction.apply(tx));
    }

}
