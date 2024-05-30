package com.example.calculator.controllers;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.loanCalculationService.LoanCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/calculator")
public class LoanCalculationController {
    @Autowired
    private LoanCalculationService loanCalculationService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@RequestBody @Validated LoanStatementRequestDto request) {
        List<LoanOfferDto> loanOffers = loanCalculationService.calculateLoanOffers(request);
        return ResponseEntity.ok(loanOffers);
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calculateCredit(@RequestBody @Validated ScoringDataDto scoringData) {
        CreditDto creditDto = loanCalculationService.calculateCredit(scoringData);
        return ResponseEntity.ok(creditDto);
    }
}
