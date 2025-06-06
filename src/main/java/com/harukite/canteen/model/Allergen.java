package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 过敏原实体类，对应数据库中的 'allergens' 表。
 */
@Entity
@Table(name = "allergens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Allergen
{

    /**
     * 过敏原唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "allergen_id", nullable = false, updatable = false)
    private String allergenId;

    /**
     * 过敏原名称，不允许为空且唯一。
     */
    @Column(name = "allergen_name", nullable = false, unique = true)
    private String allergenName;

    /**
     * 与菜品 (Dish) 的多对多关系。
     * mappedBy 指向 Dish 实体中拥有关系管理权的字段名称。
     */
    @ManyToMany(mappedBy = "allergens", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Dish> dishes = new HashSet<>();

    /**
     * 在实体持久化前，自动为 allergenId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.allergenId == null)
        {
            this.allergenId = UUID.randomUUID().toString();
        }
    }
}

