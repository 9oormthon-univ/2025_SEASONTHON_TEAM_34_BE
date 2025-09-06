package com.example.clearfootprint.domain.walkRecord;

import com.example.clearfootprint.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "walk_records",
        uniqueConstraints = @UniqueConstraint(name = "uk_walk_record_user_date", columnNames = {"user_id", "date"}),
        indexes = @Index(name = "idx_walk_user_date", columnList = "user_id,date")
)
@Setter
@Getter
@NoArgsConstructor
public class WalkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_walk_record_user"))
    private User user;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal distanceM;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal targetDistanceM;

    @Column(nullable = false)
    private LocalDate date;
}
