package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

/**
 * 食堂实体类，对应数据库中的 'canteens' 表。
 */
@Entity
@Table(name = "canteens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Canteen
{

    /**
     * 食堂唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "canteen_id", nullable = false, updatable = false)
    private String canteenId;

    /**
     * 食堂名称，不允许为空且唯一。
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * 食堂介绍。
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 食堂位置。
     */
    @Column(name = "location")
    private String location;

    /**
     * 食堂开放时间。
     */
    @Column(name = "opening_hours")
    private String openingHours;

    /**
     * 食堂联系电话。
     */
    @Column(name = "contact_phone")
    private String contactPhone;

    /**
     * 食堂图片URL。
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 与食堂额外图片 (CanteenImage) 的一对多关系。
     * mappedBy 指向 CanteenImage 实体中拥有关系管理权的字段名称 ("canteen")。
     * CascadeType.ALL 表示对 Canteen 的操作（如保存、删除）会级联到 CanteenImage。
     * orphanRemoval = true 表示如果从集合中移除 CanteenImage，它也会从数据库中删除。
     */
    @OneToMany(mappedBy = "canteen", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CanteenImage> additionalImages = new HashSet<>(); // 新增：额外图片集合

    /**
     * 在实体持久化前，自动为 canteenId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.canteenId == null)
        {
            this.canteenId = UUID.randomUUID().toString();
        }
    }
}

