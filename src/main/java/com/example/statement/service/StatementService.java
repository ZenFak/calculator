package com.example.statement.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
public class StatementService {

    private static final Logger logger = LoggerFactory.getLogger(StatementService.class);


    @Autowired
    private RestTemplate restTemplate;


    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto loanStatementRequestDto){

        validatePreScoringRules(loanStatementRequestDto);

        // Логгирование запроса
        logger.info("Отправка запроса на получение кредита: {}", loanStatementRequestDto);

        // Отправка запроса в deal/statement
        List<LoanOfferDto> loanOffers = restTemplate.postForObject(
                "http://localhost:8080/deal/statement",
                loanStatementRequestDto,
                List.class
        );

        // Присваивание statementId к каждому предложению
        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(UUID.randomUUID()));
        }

        // Логгирование ответа
        logger.info("Получены кредитные предложения: {}", loanOffers);

        return loanOffers;
    }

    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        // Логгирование запроса
        logger.info("Отправка кредитного предложения: {}", loanOfferDto);

        // Отправка запроса в deal/offer/select
        restTemplate.postForObject(
                "http://localhost:8080/deal/offer/select",
                loanOfferDto,
                Void.class
        );

        // Логгирование завершения
        logger.info("Выбранное кредитное предложение: {}", loanOfferDto);
    }


    private void validatePreScoringRules(LoanStatementRequestDto request) { //
        if (request.getFirstName().length() < 2 || request.getFirstName().length() > 30 || !request.getFirstName().matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Ошибка в вводе имени!");
        }
        if (request.getLastName().length() < 2 || request.getLastName().length() > 30 || !request.getLastName().matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Ошибка в вводе фамилии!");
        }
        if (request.getMiddleName() != null && (request.getMiddleName().length() < 2 || request.getMiddleName().length() > 30 || !request.getMiddleName().matches("[a-zA-Z]+"))) {
            throw new IllegalArgumentException("Ошибка в вводе middle name");
        }
        if (request.getAmount().compareTo(new BigDecimal("30000")) < 0) {
            throw new IllegalArgumentException("Сумма кредита >= 30000 рублей");
        }
        if (request.getTerm() < 6) {
            throw new IllegalArgumentException("Срок кредита должен быть >= 6 месяцев");
        }
        if (Period.between(request.getBirthdate(), LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Должно быть больше 18 лет");
        }
        if (!request.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new IllegalArgumentException("Неверны адрес электронной почты");
        }
        if (!request.getPassportSeries().matches("\\d{4}")) {
            throw new IllegalArgumentException("Серия должна состоять из 4 цифр");
        }
        if (!request.getPassportNumber().matches("\\d{6}")) {
            throw new IllegalArgumentException("Номер должен состоять из 6 цифр");
        }
    }
}
