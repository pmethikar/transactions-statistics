package com.n26.repository;

import com.n26.Application;
import com.n26.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository.removeAll();
    }

    @Test
    public void testAddTransactoins() {
        int numOfTransactions = new Random().nextInt(30);

        IntStream.range(0, numOfTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now())));

        assertEquals(numOfTransactions, transactionRepository.getValidTransactionList().size());
    }

    @Test
    public void testRemoveTransactions() {
        int numOfTransactions = new Random().nextInt(30);

        IntStream.range(0, numOfTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now())));

        transactionRepository.removeAll();

        assertEquals(0, transactionRepository.getTransactionList().size());
    }

    @Test
    public void testPurgeOldData() {
        int numOfValidTransactions = new Random().nextInt(30);
        int numOfVeryOldTransactions = new Random().nextInt(30);

        IntStream.range(0, numOfValidTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now())));
        IntStream.range(0, numOfVeryOldTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now().minusMillis(61000))));

        transactionRepository.purgeOldTransactions();

        assertEquals(numOfValidTransactions, transactionRepository.getTransactionList().size());
    }

    @Test
    public void testGetValidTransactionList() {
        int numOfValidTransactions = new Random().nextInt(30);
        int numOfVeryOldTransactions = new Random().nextInt(30);

        IntStream.range(0, numOfValidTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now())));
        IntStream.range(0, numOfVeryOldTransactions)
                .forEach(i -> transactionRepository.add(createTestTransaction(i, Instant.now().minusMillis(61000))));

        assertEquals(numOfValidTransactions, transactionRepository.getValidTransactionList().size());
    }

    private Transaction createTestTransaction(double amount, Instant timestamp) {
        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal(amount));
        t1.setTimestamp(timestamp);
        return t1;
    }

}
