package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 评价与反馈实体类，对应数据库中的 'ratings_reviews' 表。
 */
@Entity
@Table(name = "ratings_reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "dish_id"}) // 确保每个用户对每个菜品只有一份评价
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingReview
{

    /**
     * 评价唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "review_id", nullable = false, updatable = false)
    private String reviewId;

    /**
     * 评价用户。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 被评价菜品。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    /**
     * 评分（1-5星）。
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * 点评内容。
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * 评价时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "review_date", nullable = false, updatable = false)
    private LocalDateTime reviewDate;

    /**
     * 在实体持久化前，自动为 reviewId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.reviewId == null)
        {
            this.reviewId = UUID.randomUUID().toString();
        }
    }
}
