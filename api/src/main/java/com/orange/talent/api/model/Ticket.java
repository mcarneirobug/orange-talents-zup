package com.orange.talent.api.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

@Entity
@Table(catalog = "lottery", name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "random_number", nullable = false)
    private Integer randomNumber = generatedSequence();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer generatedSequence() {
        Random code = new Random();
        return code.nextInt(10000000 - 1);
    }

}
