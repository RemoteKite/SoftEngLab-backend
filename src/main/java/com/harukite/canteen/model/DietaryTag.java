package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 饮食标签实体类，对应数据库中的 'dietary_tags' 表。
 */
@Entity
@Table(name = "dietary_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaryTag
{

    /**
     * 饮食标签唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "tag_id", nullable = false, updatable = false)
    private String tagId;

    /**
     * 饮食标签名称，不允许为空且唯一。
     */
    @Column(name = "tag_name", nullable = false, unique = true)
    private String tagName;

    /**
     * 与菜品 (Dish) 的多对多关系。
     * mappedBy 指向 Dish 实体中拥有关系管理权的字段名称。
     */
    @ManyToMany(mappedBy = "dietaryTags", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Dish> dishes = new HashSet<>();

    /**
     * 在实体持久化前，自动为 tagId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.tagId == null)
        {
            this.tagId = UUID.randomUUID().toString();
        }
    }
}

