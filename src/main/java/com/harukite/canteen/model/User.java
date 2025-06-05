package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 用户实体类，对应数据库中的 'users' 表。
 * 使用 Lombok 注解简化了 Getter/Setter、构造函数等 boilerplate 代码。
 */
@Entity
@Table(name = "users")
@Data // 自动生成 Getter, Setter, equals, hashCode, toString
@NoArgsConstructor // 自动生成无参构造函数
@AllArgsConstructor // 自动生成全参构造函数
public class User
{

    /**
     * 用户唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    /**
     * 用户名，不允许为空且唯一。
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * 用户密码的哈希值，不允许为空。
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 用户邮箱，唯一。
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * 用户电话号码，唯一。
     */
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    /**
     * 用户角色，使用枚举类型映射数据库的 ENUM。
     */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false)
    private UserRole role;

    /**
     * 用户创建时间，在创建时自动设置。
     */
    @CreationTimestamp // 自动设置创建时间
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 用户与饮食偏好 (DietaryTag) 的多对多关系。
     * 使用 @ManyToMany 和 @JoinTable 映射中间表 'user_dietary_preferences'。
     * 'joinColumns' 指向本实体 (User) 在中间表中的外键。
     * 'inverseJoinColumns' 指向关联实体 (DietaryTag) 在中间表中的外键。
     */
    @ManyToMany(fetch = FetchType.LAZY) // 懒加载，避免不必要的N+1查询
    @JoinTable(
            name = "user_dietary_preferences", // 中间表的名称
            joinColumns = @JoinColumn(name = "user_id"), // User 在中间表中的外键
            inverseJoinColumns = @JoinColumn(name = "tag_id") // DietaryTag 在中间表中的外键
    )
    private Set<DietaryTag> dietaryTags = new HashSet<>();

    /**
     * 用户与过敏原 (Allergen) 的多对多关系。
     * 使用 @ManyToMany 和 @JoinTable 映射中间表 'user_allergens'。
     * 'joinColumns' 指向本实体 (User) 在中间表中的外键。
     * 'inverseJoinColumns' 指向关联实体 (Allergen) 在中间表中的外键。
     */
    @ManyToMany(fetch = FetchType.LAZY) // 懒加载
    @JoinTable(
            name = "user_allergens", // 中间表的名称
            joinColumns = @JoinColumn(name = "user_id"), // User 在中间表中的外键
            inverseJoinColumns = @JoinColumn(name = "allergen_id") // Allergen 在中间表中的外键
    )
    private Set<Allergen> allergens = new HashSet<>();

    /**
     * 在实体持久化前，自动为 userId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.userId == null)
        {
            this.userId = UUID.randomUUID().toString();
        }
        // 对于 created_at，@CreationTimestamp 已经处理，这里不需要手动设置
    }
}

