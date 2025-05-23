package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 每日菜谱实体类，对应数据库中的 'daily_menus' 表。
 */
@Entity
@Table(name = "daily_menus", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"canteen_id", "menu_date", "start_time", "end_time"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMenu
{

    /**
     * 菜谱唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "menu_id", nullable = false, updatable = false)
    private String menuId;

    /**
     * 所属食堂。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    /**
     * 菜谱日期。
     */
    @Column(name = "menu_date", nullable = false)
    private LocalDate menuDate;

    /**
     * 菜谱开始时间。
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * 菜谱结束时间。
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 发布菜谱的管理员。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by_user_id")
    private User publishedBy;

    /**
     * 菜谱发布时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "published_at", nullable = false, updatable = false)
    private LocalDateTime publishedAt;

    /**
     * 菜谱包含的菜品。多对多关系。
     * 拥有方，负责维护中间表 'menu_dishes'。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_dishes",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private Set<Dish> dishes = new HashSet<>();

    /**
     * 在实体持久化前，自动为 menuId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.menuId == null)
        {
            this.menuId = UUID.randomUUID().toString();
        }
    }
}

