package com.example.deal.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.sql.Timestamp;

public class StatusHistory {
    private String status;
    private Timestamp time;
    @Enumerated(EnumType.STRING)
    private ChangeType change_type;
}
