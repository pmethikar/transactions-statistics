package com.n26.controller;

import com.n26.Application;
import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    private MockMvc restMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository.removeAll();
    }

    @Test
    public void testWithNoTransactions() throws Exception {
        restMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.sum").value("0.00"))
                .andExpect(jsonPath("$.avg").value("0.00"))
                .andExpect(jsonPath("$.max").value("0.00"))
                .andExpect(jsonPath("$.min").value("0.00"))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    public void testShouldConsiderOnlyValidTransactions() throws Exception {
        createTestData();

        restMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.sum").value("2500.00"))
                .andExpect(jsonPath("$.avg").value("625.00"))
                .andExpect(jsonPath("$.max").value("1500.00"))
                .andExpect(jsonPath("$.min").value("200.00"))
                .andExpect(jsonPath("$.count").value(4));
    }

    private void createTestData() {
        List<Transaction> txList = Arrays.asList(
                createTestTransaction(500, Instant.now()),
                createTestTransaction(1500, Instant.now()),
                createTestTransaction(200, Instant.now()),
                createTestTransaction(300, Instant.now()),
                createTestTransaction(300, Instant.now().minusMillis(60010L)));

        txList.forEach(tx -> transactionRepository.add(tx));
    }

    private Transaction createTestTransaction(double amount, Instant timestamp) {
        Transaction tx = new Transaction();
        tx.setAmount(new BigDecimal(amount));
        tx.setTimestamp(timestamp);
        return tx;
    }
}
