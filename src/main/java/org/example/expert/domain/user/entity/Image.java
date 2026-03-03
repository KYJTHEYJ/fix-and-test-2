package org.example.expert.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String email;
    @Column(length = 4096, nullable = false)
    private String imageKey;

    private Image(Long userId, String email, String imageKey) {
        this.userId = userId;
        this.email = email;
        this.imageKey = imageKey;
    }

    public static Image register(Long userId, String email, String imageKey) {
        return new Image(userId, email, imageKey);
    }
}
