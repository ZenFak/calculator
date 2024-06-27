package com.example.statement.controllers;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.statement.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statement")
public class StatementController {

    @Autowired
    StatementService statementService;

    @PostMapping
    public List<LoanOfferDto> getLoanOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return statementService.generateLoanOffers(loanStatementRequestDto);
    }

    @PostMapping("/offer")
    public void selectLoanOffer(@RequestBody LoanOfferDto loanOfferDto) {
        statementService.selectLoanOffer(loanOfferDto);
    }
}
