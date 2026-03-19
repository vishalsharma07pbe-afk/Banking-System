package com.vishal.bankingsystem.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(
        name = "user_session",
        indexes = {
                @Index(name = "idx_user_session_user_id", columnList = "user_id"),
                @Index(name = "idx_user_session_expiry_date", columnList = "expiry_date")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token_hash", unique = true, nullable = false, length = 64)
    private String refreshTokenHash;

    @ElementCollection
    @CollectionTable(
            name = "user_session_rotated_tokens",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "token_hash", nullable = false, length = 64)
    @Builder.Default
    private Set<String> rotatedRefreshTokenHashes = new HashSet<>();

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
