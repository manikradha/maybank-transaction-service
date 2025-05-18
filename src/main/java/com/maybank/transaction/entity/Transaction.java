package com.maybank.transaction.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "trx_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal trxAmount;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "trx_date", nullable = false)
    private LocalDate trxDate;

    @Column(name = "trx_time", nullable = false)
    private LocalTime trxTime;

    @Column(name = "customer_id", nullable = false, length = 20)
    private String customerId;

    @Version
    private Long version;
}