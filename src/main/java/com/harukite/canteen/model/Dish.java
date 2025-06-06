package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 菜品实体类，对应数据库中的 'dishes' 表。
 */
@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish
{

    /**
     * 菜品唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "dish_id", nullable = false, updatable = false)
    private String dishId;

    /**
     * 所属食堂。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    /**
     * 菜品名称，不允许为空。
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 菜品描述。
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 菜品价格，不允许为空。
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 菜品图片URL。
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 菜品是否可用，默认为 true。
     */
    @Column(name = "is_available")
    private Boolean isAvailable = true;

    /**
     * 菜品创建时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 与饮食标签 (DietaryTag) 的多对多关系。
     * 拥有方，负责维护中间表 'dish_dietary_tags'。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dish_dietary_tags",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<DietaryTag> dietaryTags = new HashSet<>();

    /**
     * 与过敏原 (Allergen) 的多对多关系。
     * 拥有方，负责维护中间表 'dish_allergens'。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dish_allergens",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Allergen> allergens = new HashSet<>();

    /**
     * 在实体持久化前，自动为 dishId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.dishId == null)
        {
            this.dishId = UUID.randomUUID().toString();
        }
    }
}
