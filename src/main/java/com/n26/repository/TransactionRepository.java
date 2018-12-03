package com.n26.repository;

import com.n26.model.Transaction;
import com.n26.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TransactionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionRepository.class);

    private List<Transaction> transactionList = Collections.synchronizedList(new ArrayList<>());

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void add(Transaction transaction) {
        transactionList.add(transaction);
    }

    public void removeAll() {
        transactionList.clear();
    }

    public List<Transaction> getValidTransactionList() {
        purgeOldTransactions();
        LOG.debug("Number of valid transactions: {}", transactionList.size());
        return transactionList;
    }

    @Scheduled(fixedRateString = "${txstat.purge.delay.milis}")
    public synchronized void purgeOldTransactions() {
        int numOfOldTxs = transactionList.size();
        transactionList.removeIf(tx -> ValidationUtils.isVeryOldTransaction.apply(tx));
        LOG.info("Purged {} old transactions.", numOfOldTxs - transactionList.size());
    }

}