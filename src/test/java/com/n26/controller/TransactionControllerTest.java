package com.n26.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.Application;
import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    private static final int NO_CONTENT = HttpStatus.NO_CONTENT.value();
    private static final int CREATED = HttpStatus.CREATED.value();
    private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    private static final int UNPROCESSABLE_ENTITY = HttpStatus.UNPROCESSABLE_ENTITY.value();

    @Autowired
    private MockMvc restMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        transactionRepository.removeAll();
    }

    @Test
    public void testDeleteWithNoTransactions() throws Exception {
        restMvc.perform(delete("/transactions"))
                .andExpect(status().is(NO_CONTENT));
    }

    @Test
    public void testDeleteWithTransactions() throws Exception {
        createTestData();
        restMvc.perform(delete("/transactions"))
                .andExpect(status().is(NO_CONTENT));
    }

    @Test
    public void testCreateTransaction_ValidData() throws Exception {
        postAndVerify(objectMapper.writeValueAsString(createTestTransaction(100.78, Instant.now())), CREATED);
    }

    @Test
    public void testCreateTransaction_ValidData_LoadTest() throws Exception {
        long start = System.currentTimeMillis();
        LongStream.range(0, 10_000_00).forEach(i -> {
            try {
                postAndVerify(objectMapper.writeValueAsString(createTestTransaction(100+i, Instant.now())), CREATED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        long end = System.currentTimeMillis();
        System.out.print("Time taken: "+ (end-start));
    }

    @Test
    public void testCreateTransaction_InvalidJson() throws Exception {
        postAndVerify("Just random object", BAD_REQUEST);
    }

    @Test
    public void testCreateTransaction_UnparsableFields() throws Exception {
        postAndVerify("{\"timestamp\": \"2018-11-07T19:32:51.312Z\", \"amount\":\"abjh\",\"additionalField\":3}",
                UNPROCESSABLE_ENTITY);
    }

    @Test
    public void testCreateTransaction_InvalidAmount() throws Exception {
        postAndVerify("{\"timestamp\": \"2018-11-07T19:32:51.312Z\", \"amount\":\"Hundred\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"2018-11-07T19:32:51.312Z\", \"amount\":\"\"}", UNPROCESSABLE_ENTITY);
    }

    @Test
    public void testCreateTransaction_InvalidTimestamp() throws Exception {
        postAndVerify("{\"timestamp\": \"2018-11-07T19:32:51.312\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"2018-11-07 19:32:51.312Z\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"2018-31-07T19:32:51.312Z\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"150255890329\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"5 PM\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
        postAndVerify("{\"timestamp\": \"\", \"amount\":\"78.0095\"}", UNPROCESSABLE_ENTITY);
    }

    @Test
    public void testCreateTransaction_VeryOldTransaction() throws Exception {
        postAndVerify(objectMapper.writeValueAsString(createTestTransaction(100.78, Instant.now().minusMillis(60100))),
                NO_CONTENT);
    }

    @Test
    public void testCreateTransaction_FutureTransaction() throws Exception {
        postAndVerify(objectMapper.writeValueAsString(createTestTransaction(100.78, Instant.now().plusMillis(100))),
                UNPROCESSABLE_ENTITY);
    }

    private void postAndVerify(String content, int expectedStatusCode) throws Exception {
        restMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(status().is(expectedStatusCode));
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
