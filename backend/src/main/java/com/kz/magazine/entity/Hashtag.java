package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hashtags")
public class Hashtag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashtagId;

    @Column(nullable = false, unique = true)
    private String hashtagName;

    @Column(nullable = false)
    private Long usageCount = 0L;

    public Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
        this.usageCount = 0L;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public void decrementUsage() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }
}
