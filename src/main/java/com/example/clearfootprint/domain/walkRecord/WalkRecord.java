package com.example.clearfootprint.domain.walkRecord;

import com.example.clearfootprint.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WalkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_walk_record_user"))
    private User user;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal distanceKm;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal targetDistanceKm;

    @Column(nullable = false)
    private LocalDate date;
}
