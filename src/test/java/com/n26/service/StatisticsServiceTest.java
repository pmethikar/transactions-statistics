package com.n26.service;

import com.n26.Application;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class StatisticsServiceTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    StatisticsService statisticsService;

    @Before
    public void setUp() {
        transactionRepository.removeAll();
    }

    @Test
    public void testGetStatisticsWithRounding() {
        createTestData();

        Statistics stats = statisticsService.getStatistics();

        assertEquals("9508.66", stats.getSum());
        assertEquals("1188.58", stats.getAvg());
        assertEquals("3416.99", stats.getMax());
        assertEquals("500.00", stats.getMin());
        assertEquals(8, stats.getCount());
    }

    private void createTestData() {
        List<Transaction> txList = Arrays.asList(
                createTestTransaction(500),
                createTestTransaction(1500),
                createTestTransaction(578),
                createTestTransaction(500.986),
                createTestTransaction(1234.56),
                createTestTransaction(3416.989),
                createTestTransaction(778.12),
                createTestTransaction(1000)
        );

        txList.forEach(tx -> transactionRepository.add(tx));
    }

    private Transaction createTestTransaction(double amount) {
        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal(amount));
        t1.setTimestamp(Instant.now());
        return t1;
    }
}
