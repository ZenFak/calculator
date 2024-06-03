package com.example.calculator.controllers;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.loanCalculationService.LoanCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "LoanCalculation API", description = "APIs for loan calculation")
public class LoanCalculationController {
    @Autowired
    private LoanCalculationService loanCalculationService;

    @PostMapping("/offers")
    @Operation(summary = "Calculate possible loan offers",
            description = "Calculates possible loan offers based on the loan statement request data.")
    public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(@RequestBody @Validated LoanStatementRequestDto request) {
        List<LoanOfferDto> loanOffers = loanCalculationService.calculateLoanOffers(request);
        return ResponseEntity.ok(loanOffers);
    }

    @PostMapping("/calc")
    @Operation(summary = "Validate and calculate full loan parameters",
            description = "Validates the scoring data and calculates full loan parameters including monthly payments and payment schedule.")
    public ResponseEntity<CreditDto> calculateCredit(@RequestBody @Validated ScoringDataDto scoringData) {
        CreditDto creditDto = loanCalculationService.calculateCredit(scoringData);
        return ResponseEntity.ok(creditDto);
    }
}
