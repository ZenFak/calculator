package com.example.deal.controllers;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal")
@Tag(name = "Deal", description = "APIs for loan deals")
public class DealController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/statement")
    @Operation(summary = "Calculate loan offers", description = "Calculates possible loan offers")
    public ResponseEntity<List<LoanOfferDto>> generateLoanOffers(@Validated @RequestBody LoanStatementRequestDto requestDto) {
        List<LoanOfferDto> offers = loanService.generateLoanOffers(requestDto);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/offer/select")
    @Operation(summary = "Select a loan offer", description = "Selects one of the loan offers")
    public ResponseEntity<Void> chooseLoanOffer(@Validated @RequestBody LoanOfferDto loanOfferDto) {
        loanService.selectLoanOffer(loanOfferDto); //TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/calculate/{statementId}")
    @Operation(summary = "Finalize registration and calculate credit",
            description = "Finalizes the registration and calculates full credit parameters")
    public ResponseEntity<Void> finalizeCreditCalculation(@PathVariable String statementId,
                                                          @Validated @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto) {
        loanService.finalizeCreditCalculation(statementId, finishRegistrationRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}