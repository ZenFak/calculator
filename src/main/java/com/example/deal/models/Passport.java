package com.example.deal.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;
import java.util.UUID;

@Entity
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID passport_uuid;
    private String series;
    private String number;
    private String issue_branch;
    private Date issue_date;

    public UUID getPassport_uuid() {
        return passport_uuid;
    }

    public void setPassport_uuid(UUID passport_uuid) {
        this.passport_uuid = passport_uuid;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIssue_branch() {
        return issue_branch;
    }

    public void setIssue_branch(String issue_branch) {
        this.issue_branch = issue_branch;
    }

    public Date getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }
}
