package com.bdu.dishmind.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_behavior")
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "behavior_type", length = 20)
    private String behaviorType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    @CreationTimestamp
    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;
}
