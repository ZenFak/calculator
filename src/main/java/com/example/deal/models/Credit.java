package com.example.deal.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.Entity;

@Entity
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID credit_id;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthly_payment;
    private BigDecimal rate;
    private BigDecimal psk;
    @Column(columnDefinition = "jsonb")
    private String payment_schedule;
    private Boolean insurance_enabled;
    private Boolean salary_client;
    @Enumerated(EnumType.STRING)
    private CreditStatus credit_status;


    public UUID getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(UUID credit_id) {
        this.credit_id = credit_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public BigDecimal getMonthly_payment() {
        return monthly_payment;
    }

    public void setMonthly_payment(BigDecimal monthly_payment) {
        this.monthly_payment = monthly_payment;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getPsk() {
        return psk;
    }

    public void setPsk(BigDecimal psk) {
        this.psk = psk;
    }

    public String getPayment_schedule() {
        return payment_schedule;
    }

    public void setPayment_schedule(String payment_schedule) {
        this.payment_schedule = payment_schedule;
    }

    public Boolean getInsurance_enabled() {
        return insurance_enabled;
    }

    public void setInsurance_enabled(Boolean insurance_enabled) {
        this.insurance_enabled = insurance_enabled;
    }

    public Boolean getSalary_client() {
        return salary_client;
    }

    public void setSalary_client(Boolean salary_client) {
        this.salary_client = salary_client;
    }

    public CreditStatus getCredit_status() {
        return credit_status;
    }

    public void setCredit_status(CreditStatus credit_status) {
        this.credit_status = credit_status;
    }
}
