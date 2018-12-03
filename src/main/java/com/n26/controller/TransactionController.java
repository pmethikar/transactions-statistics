package com.n26.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import com.n26.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity create(@RequestBody Transaction transaction) {
        if(transaction.getTimestamp() == null
                || transaction.getAmount() == null
                || ValidationUtils.isFutureTransaction.apply(transaction)) {
            return new ResponseEntity(UNPROCESSABLE_ENTITY);
        }
        if (ValidationUtils.isVeryOldTransaction.apply(transaction)) {
            return new ResponseEntity(NO_CONTENT);
        }
        transactionService.add(transaction);
        return new ResponseEntity(CREATED);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        transactionService.delete();
    }

    @ExceptionHandler({JsonParseException.class, MismatchedInputException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleInvalidJson(HttpServletResponse response) {
    }

    @ExceptionHandler({InvalidFormatException.class, UnrecognizedPropertyException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    void handleNonParsableData(HttpServletResponse response) {
    }

}
