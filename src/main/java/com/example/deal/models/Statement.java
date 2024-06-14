package com.example.deal.models;

import com.example.deal.converters.StatusHistoryConverter;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statement_id;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Client client_id;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Credit credit_id;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private Timestamp creation_date;
    @Column(columnDefinition = "jsonb")
    private String applied_offer;
    private Timestamp sign_date;
    private String ses_code;


    @Convert(converter = StatusHistoryConverter.class)
    @Column(columnDefinition = "jsonb")
    private StatusHistory status_history;


    public UUID getStatement_id() {
        return statement_id;
    }

    public void setStatement_id(UUID statement_id) {
        this.statement_id = statement_id;
    }

    public Client getClient_id() {
        return client_id;
    }

    public void setClient_id(Client client_id) {
        this.client_id = client_id;
    }

    public Credit getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(Credit credit_id) {
        this.credit_id = credit_id;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Timestamp getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Timestamp creation_date) {
        this.creation_date = creation_date;
    }

    public String getApplied_offer() {
        return applied_offer;
    }

    public void setApplied_offer(String applied_offer) {
        this.applied_offer = applied_offer;
    }

    public Timestamp getSign_date() {
        return sign_date;
    }

    public void setSign_date(Timestamp sign_date) {
        this.sign_date = sign_date;
    }

    public String getSes_code() {
        return ses_code;
    }

    public void setSes_code(String ses_code) {
        this.ses_code = ses_code;
    }

    public StatusHistory getStatus_history() {
        return status_history;
    }

    public void setStatus_history(StatusHistory status_history) {
        this.status_history = status_history;
    }
}
