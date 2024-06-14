package com.example.deal.service;

import com.example.calculator.dto.CreditDto;
import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.models.*;
import com.example.deal.repositories.ClientRepository;
import com.example.deal.repositories.StatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String CALCULATOR_SERVICE_URL = "http://localhost:8080/calculator";


    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto requestDto) {
        Client client = new Client();
        client.setFirst_name(requestDto.getFirstName());
        client.setLast_name(requestDto.getLastName());
        client.setMiddle_name(requestDto.getMiddleName());
        client.setBirth_date((convertToDate(requestDto.getBirthdate())));
        client.setEmail(requestDto.getEmail());
        clientRepository.save(client);

        Statement statement = new Statement();
        statement.setClient_id(client);
        statement.setCreation_date(Timestamp.valueOf(LocalDateTime.now()));
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statementRepository.save(statement);

        List<LoanOfferDto> loanOffers = restTemplate.postForObject(
                CALCULATOR_SERVICE_URL + "/offers", requestDto, List.class
        );

        if (loanOffers != null) {
            loanOffers = loanOffers.stream()
                    .map(offer -> {
                        offer.setStatementId(statement.getStatement_id());
                        return offer;
                    })
                    .sorted((o1, o2) -> o2.getRate().compareTo(o1.getRate()))
                    .collect(Collectors.toList());
        }
        return loanOffers;
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        Statement statement = statementRepository.findById(loanOfferDto.getStatementId())
                .orElseThrow(() -> new IllegalArgumentException("Не существует записи с данным Id"));
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setApplied_offer(loanOfferDto.toString());    //to JSON string
        statementRepository.save(statement);
    }

    public void finalizeCreditCalculation(String statementId,
                                          FinishRegistrationRequestDto finishRegistrationRequest) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new IllegalArgumentException("Не существует записи с данным Id"));

        Client client = statement.getClient_id();

        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setGender(finishRegistrationRequest.getGender());
        scoringData.setMaritalStatus(finishRegistrationRequest.getMaritalStatus());
        scoringData.setDependentAmount(finishRegistrationRequest.getDependentAmount());
        scoringData.setPassportIssueDate(finishRegistrationRequest.getPassportIssueDate());
        scoringData.setPassportIssueBranch(finishRegistrationRequest.getPassportIssueBranch());
        scoringData.setEmployment(finishRegistrationRequest.getEmployment());
        scoringData.setAccountNumber(finishRegistrationRequest.getAccountNumber());

        scoringData.setFirstName(client.getFirst_name());
        scoringData.setLastName(client.getLast_name());
        scoringData.setMiddleName(client.getMiddle_name());
        scoringData.setBirthdate(convertToLocalDateViaInstant(client.getBirth_date()));
        scoringData.setPassportSeries(client.getPassport_id().getSeries());
        scoringData.setPassportNumber(client.getPassport_id().getNumber());


        CreditDto creditDto = restTemplate.postForObject(
                CALCULATOR_SERVICE_URL + "/calc", scoringData, CreditDto.class
        );

        Credit credit = new Credit();
        if (creditDto != null) {
            credit.setAmount(creditDto.getAmount());
            credit.setTerm(creditDto.getTerm());
            credit.setMonthly_payment(creditDto.getMonthlyPayment());
            credit.setRate(creditDto.getRate());
            credit.setPsk(creditDto.getPsk());

            ObjectMapper objectMapper = new ObjectMapper();
            try{
                String paymentSchedule = objectMapper.writeValueAsString(creditDto.getPaymentSchedule());
                credit.setPayment_schedule(paymentSchedule);
            } catch (JsonProcessingException e){
                throw new RuntimeException("Ошибка преобразования в JSON", e);
            }

            credit.setInsurance_enabled(creditDto.getInsuranceEnabled());
            credit.setSalary_client(creditDto.getSalaryClient());
            credit.setCredit_status(CreditStatus.CALCULATED);
        }

        statement.setCredit_id(credit);
        statement.setStatus(ApplicationStatus.CREDIT_ISSUED);
        statementRepository.save(statement);
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
