package com.probation.example.mc1.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "mc1_timestamp")
    private LocalDateTime mc1Timestamp;

    @Column(name = "mc2_timestamp")
    private LocalDateTime mc2Timestamp;

    @Column(name = "mc3_timestamp")
    private LocalDateTime mc3Timestamp;

    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;
}
