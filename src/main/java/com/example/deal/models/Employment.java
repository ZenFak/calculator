package com.example.deal.models;

import com.example.calculator.dto.EmploymentStatus;
import com.example.calculator.dto.Position;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employment_uuid;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;
    private String employment_inn;
    private BigDecimal salary;
    @Enumerated(EnumType.STRING)
    private Position position;
    private Integer work_experience_total;
    private Integer work_experience_current;
}
