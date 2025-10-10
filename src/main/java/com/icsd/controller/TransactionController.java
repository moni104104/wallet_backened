package com.icsd.controller;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.FundTransferRequestDTO;
import com.icsd.dto.request.TransactionDepositRequestDTO;
import com.icsd.model.Transaction;
import com.icsd.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping(value = "/transaction/deposit")
    public ResponseEntity<ApiResponse> depoistAmountInAccount(@RequestBody @Valid TransactionDepositRequestDTO tdReq) {
        log.info("inside deposit amount in acccount - inside transaction controller");
        System.out.println(tdReq);
        Transaction trans = transactionService.depositAmountInAccount(tdReq);
        ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.TRANSACTION_COMPLETED,
                trans.getTransactionId());
        return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);

    }

    @PostMapping(value = "/transaction/withdraw")
    public ResponseEntity<ApiResponse> withDrawAmountInAccount(@RequestBody @Valid TransactionDepositRequestDTO tdReq) {
        log.info("inside withdraw amount in acccount - inside transaction controller");
        System.out.println(tdReq);
        Transaction trans = transactionService.WithDrawAmountfromAccount(tdReq);
        ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.TRANSACTION_COMPLETED,
                trans.getTransactionId());
        return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);

    }

    @PostMapping(value = "/transaction/transfer")
    public ResponseEntity<ApiResponse> fundTransfer(@RequestBody @Valid FundTransferRequestDTO tdReq) {
        log.info("inside fundTransfer amount in acccount - inside transaction controller");
        System.out.println(tdReq);
        Transaction trans = transactionService.fundTransferFunction(tdReq);
        ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.TRANSACTION_COMPLETED,
                trans.getTransactionId());
        return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
    }

    @GetMapping(value = "/transactions/{customerId}")
    public ResponseEntity<ApiResponse> getTransactionByCustomerId(@PathVariable("customerId") int customerId)
            throws Exception {
        log.info("get list of transactions by customer id " + customerId);
        List<Transaction> listTransactions = transactionService.getTransactionsByCustomerId(customerId);

        if (listTransactions.isEmpty()) {
            log.info("No Transactions found for customer id " + customerId);
            ApiResponse apirsponse = new ApiResponse(HttpStatus.NO_CONTENT.value(), messages.TRANSACTION_NOT_FOUND,
                    null);
            return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
        }
        ApiResponse apirsponse = new ApiResponse(HttpStatus.FOUND.value(), messages.TRANSACTION_FOUND,
                listTransactions);

        return new ResponseEntity<ApiResponse>(apirsponse, HttpStatus.OK);
    }
}
