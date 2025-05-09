package com.ddbb.dingdong.infrastructure.notification.firebase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "fcmtoken",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"token"}),
            @UniqueConstraint(columnNames = {"user_id"})
    }
)
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private boolean isActive;

    @Column
    private LocalDateTime lastModifiedAt;

    @Column
    private Long userId;

    public FCMToken(Long userId, String token) {
        this.token = token;
        this.isActive = true;
        this.lastModifiedAt = LocalDateTime.now();
        this.userId = userId;
    }

    public void inactivate() {
        this.isActive = false;
    }
}
