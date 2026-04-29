package edu.icet.ecom.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We will generate an ID like "EVT-10492" for the UI
    private String eventId;

    private LocalDateTime timestamp;
    private String logLevel; // INFO, WARN, CRITICAL, REFUND
    private String service;
    private String message;
    private String username;
}