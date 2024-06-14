package com.example.deal.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LoanService {
    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto requestDto) {
        return null;
    }


    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
    }


    public void finalizeCreditCalculation(String statementId,
                                          FinishRegistrationRequestDto finishRegistrationRequest) {
    }
}
