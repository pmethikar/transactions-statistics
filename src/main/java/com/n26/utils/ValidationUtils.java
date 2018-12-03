package com.n26.utils;

import com.n26.model.Transaction;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class ValidationUtils {

    public static final long TX_VALIDITY_MILIS = 60000L;

    public static Function<Transaction, Boolean> isVeryOldTransaction = tx ->
            getDurationPassedFromNow(tx.getTimestamp()) > TX_VALIDITY_MILIS ? true : false;

    public static Function<Transaction, Boolean> isFutureTransaction = tx ->
            getDurationPassedFromNow(tx.getTimestamp()) < 0 ? true : false;


    private static long getDurationPassedFromNow(Instant timestamp) {
        return Duration.between(timestamp, Instant.now()).toMillis();
    }
}
