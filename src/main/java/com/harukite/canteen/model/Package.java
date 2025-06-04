package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 宴会套餐实体类，对应数据库中的 'packages' 表。
 */
@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Package {

    /**
     * 套餐唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "package_id", nullable = false, updatable = false)
    private String packageId;

    /**
     * 所属食堂。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false) // 新增：关联食堂
    private Canteen canteen;

    /**
     * 套餐名称，不允许为空。
     * 注意：name 不再是全局唯一的，而是相对于所属食堂唯一。
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 套餐描述。
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 套餐价格，不允许为空。
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 与菜品 (Dish) 的多对多关系。
     * 拥有方，负责维护中间表 'package_dishes'。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "package_dishes",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private Set<Dish> dishes = new HashSet<>();

    /**
     * 在实体持久化前，自动为 packageId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate() {
        if (this.packageId == null) {
            this.packageId = UUID.randomUUID().toString();
        }
    }
}
