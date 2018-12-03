package com.n26.service;

import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepo;

    public void add(Transaction transaction) {
        transactionRepo.add(transaction);
    }

    public void delete() {
        transactionRepo.removeAll();
    }
}
