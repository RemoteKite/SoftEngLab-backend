package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // 导入 BigDecimal
import java.util.UUID;

/**
 * 宴会预订菜品项实体类，对应数据库中的 'banquet_reservation_dishes' 表。
 * 用于存储宴会预订中定制菜品的数量。
 */
@Entity
@Table(name = "banquet_reservation_dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanquetReservationDishItem {

    /**
     * 宴会预订菜品项唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "banquet_reservation_dish_item_id", nullable = false, updatable = false)
    private String banquetReservationDishItemId;

    /**
     * 所属宴会预订。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banquet_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private BanquetReservation banquetReservation;

    /**
     * 菜品。多对一关系。
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
     * 该菜品项的小计金额 (可选，可以在每次获取时计算)。
     * 为了简化，我们直接存储。
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * 在实体持久化前，自动为 banquetReservationDishItemId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate() {
        if (this.banquetReservationDishItemId == null) {
            this.banquetReservationDishItemId = UUID.randomUUID().toString();
        }
    }
}

