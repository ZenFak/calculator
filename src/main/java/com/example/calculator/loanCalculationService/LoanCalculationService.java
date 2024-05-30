package com.example.calculator.loanCalculationService;

import com.example.calculator.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoanCalculationService {
    private static final BigDecimal BASE_RATE = new BigDecimal("10.0");
    private static final BigDecimal INSURANCE_DISCOUNT = new BigDecimal("3.0");
    private static final BigDecimal SALARY_CLIENT_DISCOUNT = new BigDecimal("1.0");
    private static final BigDecimal INSURANCE_COST = new BigDecimal("100000");


    public List<LoanOfferDto> calculateLoanOffers(LoanStatementRequestDto request) {
        validatePreScoringRules(request);
        List<LoanOfferDto> loanOffers = new ArrayList<>();

        boolean[] boolValues = {false, true};

        for (boolean insuranceEnabled : boolValues) {
            for (boolean salaryClient : boolValues) {
                LoanOfferDto offer = new LoanOfferDto(); // создаем новое кредитное предложение

                offer.setStatementId(UUID.randomUUID()); // уникальный идентификатор заявки
                offer.setRequestedAmount(request.getAmount()); // запрошенная сумму кредита
                offer.setTerm(request.getTerm()); // запрошенный срок кредита

                BigDecimal rate = BASE_RATE; // базовая процентная ставка
                BigDecimal totalAmount = request.getAmount(); // общая сумма кредита

                if (insuranceEnabled) { // если страховка включена
                    totalAmount = totalAmount.add(INSURANCE_COST); // добавляем страховку к общей сумме
                    rate = rate.subtract(INSURANCE_DISCOUNT); // уменьшаем процентную ставку
                }

                if (salaryClient) { // если клиент зарплатный
                    rate = rate.subtract(SALARY_CLIENT_DISCOUNT); // уменьшаем процентную ставку
                }

                BigDecimal monthPayment = calculateMonthPayment(totalAmount, rate, request.getTerm()); // ежемесячный платеж

                // параметры кредитного предложения
                offer.setTotalAmount(totalAmount);
                offer.setMonthlyPayment(monthPayment);
                offer.setRate(rate);
                offer.setInsuranceEnabled(insuranceEnabled);
                offer.setSalaryClient(salaryClient);

                loanOffers.add(offer);
            }
        }
        return loanOffers;
    }

    public CreditDto calculateCredit(ScoringDataDto scoringData) {
        validateScoringRules(scoringData);

        CreditDto creditDto = new CreditDto();

        BigDecimal rate = BASE_RATE; // базовая процентная ставка
        BigDecimal totalAmount = scoringData.getAmount(); // общая сумма кредита

        if (scoringData.getInsuranceEnabled()) { // если страховка включена
            totalAmount = totalAmount.add(INSURANCE_COST); // добавляем стоимость страховки к общей сумме кредита
            rate = rate.subtract(INSURANCE_DISCOUNT); // уменьшаем процентную ставку на скидку за страховку
        }
        if (scoringData.getSalaryClient()) { // если клиент - зарплатный
            rate = rate.subtract(SALARY_CLIENT_DISCOUNT); // уменьшаем процентную ставку на скидку для зарплатных клиентов
        }
        rate = applyScoringAdjustments(scoringData, rate); // корректировки к процентной ставке

        BigDecimal monthlyPayment = calculateMonthPayment(totalAmount, rate, scoringData.getTerm()); // вычисляем ежемесячный платеж
        BigDecimal psk = calculatePsk(totalAmount, rate, scoringData.getTerm()); // вычисляем полную стоимость кредита

        creditDto.setAmount(scoringData.getAmount());
        creditDto.setTerm(scoringData.getTerm());
        creditDto.setMonthlyPayment(monthlyPayment);
        creditDto.setRate(rate);
        creditDto.setPsk(psk);
        creditDto.setInsuranceEnabled(scoringData.getInsuranceEnabled());
        creditDto.setSalaryClient(scoringData.getSalaryClient());
        creditDto.setPaymentSchedule(generatePaymentSchedule(totalAmount, rate, scoringData.getTerm()));

        return creditDto;
    }

    private void validateScoringRules(ScoringDataDto scoringData) {
        if (scoringData.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new IllegalArgumentException("С таким статусом кредит не выдадим(");
        }
        if (scoringData.getAmount().compareTo(scoringData.getEmployment().getSalary().multiply(new BigDecimal("25"))) > 0) {
            throw new IllegalArgumentException("Слишком большая сумма. Должна быть <25 месячных зарплат");
        }
        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            throw new IllegalArgumentException("Возраст должен быть от 20 до 65 лет");
        }
        if (scoringData.getEmployment().getWorkExperienceTotal() < 18 || scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new IllegalArgumentException("Общий стаж должен быть > 18 месяцев. Текущий - > 3 месяцев");
        }
    }

    private BigDecimal applyScoringAdjustments(ScoringDataDto scoringData, BigDecimal rate) {
        switch (scoringData.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED:
                rate = rate.add(new BigDecimal("1.0"));
                break;
            case BUSINESS_OWNER:
                rate = rate.add(new BigDecimal("2.0"));
                break;
            default:
                break;
        }

        switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE_MANAGER:
                rate = rate.subtract(new BigDecimal("2.0"));
                break;
            case TOP_MANAGER:
                rate = rate.subtract(new BigDecimal("3.0"));
                break;
            default:
                break;
        }

        switch (scoringData.getMaritalStatus()) {
            case MARRIED:
                rate = rate.subtract(new BigDecimal("3.0"));
                break;
            case DIVORCED:
                rate = rate.add(new BigDecimal("1.0"));
                break;
            default:
                break;
        }

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        switch (scoringData.getGender()) {
            case FEMALE:
                if (age >= 32 && age <= 60) {
                    rate = rate.subtract(new BigDecimal("3.0"));
                }
                break;
            case MALE:
                if (age >= 30 && age <= 55) {
                    rate = rate.subtract(new BigDecimal("3.0"));
                }
                break;
            case NON_BINARY:
                rate = rate.add(new BigDecimal("7.0"));
                break;
            default:
                break;
        }

        return rate;
    }

    private BigDecimal calculatePsk(BigDecimal amount, BigDecimal rate, int term) { //
        return amount.multiply(rate).divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(term)).divide(new BigDecimal("12"), BigDecimal.ROUND_HALF_UP);
        // полная стоимость кредита
    }

    private List<PaymentScheduleElementDto> generatePaymentSchedule(BigDecimal amount, BigDecimal rate, int term) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>(); // платежи
        BigDecimal monthlyPayment = calculateMonthPayment(amount, rate, term); // ежемесячный платеж
        BigDecimal remainingDebt = amount; // оставшийся долг

        for (int i = 1; i <= term; i++) { // проход по всему сроку кредита
            PaymentScheduleElementDto element = new PaymentScheduleElementDto();
            element.setNumber(i);
            element.setDate(LocalDate.now().plusMonths(i)); // платеж за данный месяц

            BigDecimal interestPayment = remainingDebt.multiply(rate)
                    .divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP)
                    .divide(new BigDecimal("12"), BigDecimal.ROUND_HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment); // платеж в месяц

            element.setTotalPayment(monthlyPayment);
            element.setInterestPayment(interestPayment);
            element.setDebtPayment(debtPayment);
            element.setRemainingDebt(remainingDebt);

            schedule.add(element);
        }

        return schedule;
    }


    public BigDecimal calculateMonthPayment(BigDecimal amount, BigDecimal rate, int term) {
        BigDecimal monthlyRate = rate.divide(new BigDecimal("12"), BigDecimal.ROUND_HALF_UP)
                .divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP); // процентная ставка в виде десятичной дроби

        BigDecimal divisor = BigDecimal.ONE.subtract((BigDecimal.ONE.add(monthlyRate)).pow(-term));
        // 1-(1+monthlyRate)^(-term)

        return amount.multiply(monthlyRate).divide(divisor, BigDecimal.ROUND_HALF_UP);
    }

    private void validatePreScoringRules(LoanStatementRequestDto request) {
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
