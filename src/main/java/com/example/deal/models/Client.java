package com.example.deal.models;

import com.example.calculator.dto.Gender;
import com.example.calculator.dto.MaritalStatus;
import com.example.deal.converters.EmploymentConverter;
import com.example.deal.converters.PassportConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID client_id;
    private String last_name;
    private String first_name;
    private String middle_name;
    private Date birth_date;
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private MaritalStatus marital_status;
    private Integer dependent_amount;

    @Convert(converter = PassportConverter.class)
    @Column(columnDefinition = "jsonb")
    private Passport passport_id;

    @Convert(converter = EmploymentConverter.class)
    @Column(columnDefinition = "jsonb")
    private Employment employment_id;
    private String account_number;


    public UUID getClient_id() {
        return client_id;
    }

    public void setClient_id(UUID client_id) {
        this.client_id = client_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(MaritalStatus marital_status) {
        this.marital_status = marital_status;
    }

    public Integer getDependent_amount() {
        return dependent_amount;
    }

    public void setDependent_amount(Integer dependent_amount) {
        this.dependent_amount = dependent_amount;
    }

    public Passport getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(Passport passport_id) {
        this.passport_id = passport_id;
    }

    public Employment getEmployment_id() {
        return employment_id;
    }

    public void setEmployment_id(Employment employment_id) {
        this.employment_id = employment_id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
}
