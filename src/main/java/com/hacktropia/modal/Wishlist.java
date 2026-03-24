package com.hacktropia.modal;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Users users;

    @ManyToOne
    private Book book;

    @CreationTimestamp
    private LocalDateTime addedAt;

    @Column(length = 500)
    private String notes;
}
