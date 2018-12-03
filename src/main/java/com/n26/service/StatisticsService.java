package com.n26.service;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static java.util.stream.Collectors.summarizingDouble;

@Service
public class StatisticsService {

    @Autowired
    TransactionRepository transactionRepository;

    public Statistics getStatistics() {
        return calculateStatistics();
    }

    private Statistics calculateStatistics() {
        List<Transaction> transactionList = transactionRepository.getValidTransactionList();
        DoubleSummaryStatistics statistics = transactionList.parallelStream()
                .mapToDouble(tx -> tx.getAmount().doubleValue())
                .summaryStatistics();

        String sum = formatAmount(statistics.getSum());
        String avg = formatAmount(statistics.getAverage());
        String max = formatAmount(statistics.getMax());
        String min = formatAmount(statistics.getMin());

        return new Statistics(sum, avg, max, min, statistics.getCount());
    }

    private String formatAmount(Double amount) {
        if (amount.equals(Double.NaN)
                || amount.equals(Double.NEGATIVE_INFINITY)
                || amount.equals(Double.POSITIVE_INFINITY)) {
            return "0.00";
        }
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toString();
    }

}
