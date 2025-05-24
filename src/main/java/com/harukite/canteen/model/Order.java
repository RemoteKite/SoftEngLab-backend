package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 餐品预订实体类，对应数据库中的 'orders' 表。
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order
{

    /**
     * 订单唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    /**
     * 预订用户。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 预订食堂。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    /**
     * 预订日期。
     */
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * 取餐时间。
     */
    @Column(name = "pickup_time", nullable = false)
    private LocalTime pickupTime;

    /**
     * 订单总金额。
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 订单状态，使用枚举类型映射数据库的 ENUM。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * 订单创建时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 订单包含的菜品项。一对多关系。
     * mappedBy 指向 OrderItem 实体中拥有关系管理权的字段名称。
     * CascadeType.ALL 表示对 Order 的操作（如保存、删除）会级联到 OrderItem。
     * orphanRemoval = true 表示如果从集合中移除 OrderItem，它也会从数据库中删除。
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    /**
     * 在实体持久化前，自动为 orderId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.orderId == null)
        {
            this.orderId = UUID.randomUUID().toString();
        }
    }
}
