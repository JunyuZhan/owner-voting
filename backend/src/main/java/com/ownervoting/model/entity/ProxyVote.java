package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "proxy_vote")
public class ProxyVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private VoteTopic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private VoteOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private Owner voter; // 被代理的业主

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house; // 投票房屋

    // 代理投票人（管理员）
    @Column(length = 50, nullable = false)
    private String proxyName;

    // 投票权重
    private BigDecimal voteWeight = BigDecimal.ONE;

    // 代理投票时间
    @Column(nullable = false)
    private LocalDateTime proxyTime;

    // 代理原因
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private ProxyReason reason;

    // 备注说明
    @Column(columnDefinition = "TEXT")
    private String remark;

    // IP地址
    @Column(length = 50)
    private String ipAddress;

    public enum ProxyReason {
        OFFLINE_REQUEST,    // 业主线下要求代投
        TECHNICAL_ISSUE,    // 技术问题无法在线投票
        DISABILITY,         // 身体不便
        OTHER              // 其他原因
    }

    @PrePersist
    public void prePersist() {
        proxyTime = LocalDateTime.now();
    }
} 