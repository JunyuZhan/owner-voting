package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vote_option")
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private VoteTopic topic;

    @Column(length = 255)
    private String optionText;

    private Integer sortOrder = 0;

    public VoteTopic getVoteTopic() {
        return topic;
    }
} 