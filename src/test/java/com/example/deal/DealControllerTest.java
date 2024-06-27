package com.example.deal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.calculator.dto.*;
import com.example.deal.controllers.DealController;
import com.example.deal.dto.*;
import com.example.deal.service.LoanService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DealControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private DealController dealController;
    private LoanStatementRequestDto loanStatementRequestDto;
    private LoanOfferDto loanOfferDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;

    @BeforeEach
    void setUp() {
        loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setAmount(new BigDecimal("10000"));
        loanStatementRequestDto.setTerm(12);
        loanStatementRequestDto.setFirstName("Иван");
        loanStatementRequestDto.setLastName("Иванов");
        loanStatementRequestDto.setMiddleName("И");
        loanStatementRequestDto.setEmail("ivan@test.com");
        loanStatementRequestDto.setBirthdate(LocalDate.of(1990, 1, 1));
        loanStatementRequestDto.setPassportSeries("1234");
        loanStatementRequestDto.setPassportNumber("567890");

        loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(UUID.randomUUID());
        loanOfferDto.setRequestedAmount(new BigDecimal("10000"));
        loanOfferDto.setTotalAmount(new BigDecimal("11000"));
        loanOfferDto.setTerm(12);
        loanOfferDto.setMonthlyPayment(new BigDecimal("916.67"));
        loanOfferDto.setRate(new BigDecimal("10"));
        loanOfferDto.setInsuranceEnabled(true);
        loanOfferDto.setSalaryClient(false);

        finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        finishRegistrationRequestDto.setGender(Gender.MALE);
        finishRegistrationRequestDto.setMaritalStatus(MaritalStatus.SINGLE);
        finishRegistrationRequestDto.setDependentAmount(0);
        finishRegistrationRequestDto.setPassportIssueDate(LocalDate.of(2010, 1, 1));
        finishRegistrationRequestDto.setPassportIssueBranch("Воронежское");
        finishRegistrationRequestDto.setEmployment(new EmploymentDto());
        finishRegistrationRequestDto.setAccountNumber("1");




        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(dealController).build();
    }

    @Test
    void testGenerateLoanOffers() throws Exception {
        List<LoanOfferDto> loanOffers = Arrays.asList(loanOfferDto);

        when(loanService.generateLoanOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(loanOffers);

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(loanOffers)));
    }

    @Test
    void testSelectLoanOffer() throws Exception {
        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanOfferDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testCalculateLoan() throws Exception {
        mockMvc.perform(post("/deal/calculate/{statementId}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finishRegistrationRequestDto)))
                .andExpect(status().isOk());
    }
}
