package com.example.calculator.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.example.calculator.dto.*;
import com.example.calculator.loanCalculationService.LoanCalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LoanCalculationControllerTest {
    //@Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanCalculationService loanCalculationService;

    @InjectMocks
    private LoanCalculationController loanCalculationController;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loanCalculationController).build();
    }


    @Test
    public void testCalculateLoanOffers() throws Exception {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        requestDto.setAmount(new BigDecimal("100000"));
        requestDto.setTerm(18);
        requestDto.setFirstName("Иванов");
        requestDto.setLastName("Иван");
        requestDto.setMiddleName("Иванович");
        requestDto.setEmail("ivan@test.com");
        requestDto.setBirthdate(LocalDate.of(2000, 1, 1));
        requestDto.setPassportSeries("1234");
        requestDto.setPassportNumber("567890");

        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setStatementId(UUID.randomUUID());
        offerDto.setRequestedAmount(new BigDecimal("50000"));
        offerDto.setTotalAmount(new BigDecimal("52000"));
        offerDto.setTerm(12);
        offerDto.setMonthlyPayment(new BigDecimal("4333.33"));
        offerDto.setRate(new BigDecimal("10"));
        offerDto.setInsuranceEnabled(false);
        offerDto.setSalaryClient(false);

        List<LoanOfferDto> offers = Arrays.asList(offerDto);

        when(loanCalculationService.calculateLoanOffers(any(LoanStatementRequestDto.class))).thenReturn(offers);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCalculateCredit() throws Exception {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(new BigDecimal("100000"));
        scoringDataDto.setTerm(12);
        scoringDataDto.setFirstName("Иванов");
        scoringDataDto.setLastName("Иван");
        scoringDataDto.setMiddleName("Иванович");
        scoringDataDto.setGender(Gender.MALE);
        scoringDataDto.setBirthdate(LocalDate.of(1990, 1,  1));
        scoringDataDto.setPassportSeries("1234");
        scoringDataDto.setPassportNumber("567890");
        scoringDataDto.setPassportIssueDate(LocalDate.of(2010, 1, 1));
        scoringDataDto.setPassportIssueBranch("Воронежское");
        scoringDataDto.setMaritalStatus(MaritalStatus.SINGLE);
        scoringDataDto.setDependentAmount(0);
        scoringDataDto.setEmployment(new EmploymentDto());
        scoringDataDto.setAccountNumber("1");
        scoringDataDto.setInsuranceEnabled(true);
        scoringDataDto.setSalaryClient(true);

        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(new BigDecimal("50000"));
        creditDto.setTerm(12);
        creditDto.setMonthlyPayment(new BigDecimal("4333.33"));
        creditDto.setRate(new BigDecimal("10"));
        creditDto.setPsk(new BigDecimal("52000"));
        creditDto.setInsuranceEnabled(true);
        creditDto.setSalaryClient(true);
        creditDto.setPaymentSchedule(Arrays.asList(new PaymentScheduleElementDto()));

        when(loanCalculationService.calculateCredit(any(ScoringDataDto.class))).thenReturn(creditDto);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringDataDto)))
                .andExpect(status().isOk());
    }
}