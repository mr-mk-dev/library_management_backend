package com.hacktropia.modal;

import com.hacktropia.domain.FineStatus;
import com.hacktropia.domain.FineType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Users users;

    @ManyToOne
    private BookLoan bookLoan;

    @Enumerated(EnumType.STRING)
    private FineType type;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    private FineStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    private Users waivedBy;

    private LocalDateTime waivedAt;

    private String waiverReason;

    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users processedBy;

    private String transactionId;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void applyPayment(Long paymentAmount){
        if(paymentAmount==null || paymentAmount<=0){
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.status=FineStatus.PAID;
        this.paidAt=LocalDateTime.now();

    }
    public void waive(Users admin, String reason){
        this.status=FineStatus.WAIVED;
        this.waivedBy=admin;
        this.waivedAt=LocalDateTime.now();
        this.waiverReason=reason;
    }


}

