package com.harukite.canteen.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 订单详情实体类，对应数据库中的 'order_items' 表。
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem
{

    /**
     * 订单项唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "order_item_id", nullable = false, updatable = false)
    private String orderItemId;

    /**
     * 所属订单。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Order order;

    /**
     * 订单中的菜品。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    /**
     * 菜品数量。
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 该菜品小计金额。
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * 在实体持久化前，自动为 orderItemId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.orderItemId == null)
        {
            this.orderItemId = UUID.randomUUID().toString();
        }
    }
}
