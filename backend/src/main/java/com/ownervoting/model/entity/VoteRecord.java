package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vote_record")
public class VoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private VoteTopic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private VoteOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id")
    private Owner voter;

    private BigDecimal voteWeight = BigDecimal.ONE;

    private LocalDateTime voteTime;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 255)
    private String deviceInfo;

    @PrePersist
    public void prePersist() {
        voteTime = LocalDateTime.now();
    }

    public VoteTopic getVoteTopic() {
        return topic;
    }

    public Owner getOwner() {
        return voter;
    }
} 