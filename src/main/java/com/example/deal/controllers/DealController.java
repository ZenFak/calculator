package com.example.deal.controllers;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal")
public class DealController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> generateLoanOffers(@Validated @RequestBody LoanStatementRequestDto requestDto) {
        List<LoanOfferDto> offers = loanService.generateLoanOffers(requestDto);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/offer/select")
    public ResponseEntity<Void> chooseLoanOffer(@Validated @RequestBody LoanOfferDto loanOfferDto) {
        loanService.selectLoanOffer(loanOfferDto); //TODO
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> finalizeCreditCalculation(@PathVariable String statementId,
                                                          @Validated @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto) {
        loanService.finalizeCreditCalculation(statementId, finishRegistrationRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}