package com.n26.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {

    BigDecimal amount;

    Instant timestamp;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
