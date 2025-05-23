package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 宴会包厢实体类，对应数据库中的 'rooms' 表。
 */
@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room
{

    /**
     * 包厢唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "room_id", nullable = false, updatable = false)
    private String roomId;

    /**
     * 所属食堂。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    /**
     * 包厢名称，不允许为空。
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 包厢容纳人数，不允许为空。
     */
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * 包厢描述。
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 包厢图片URL。
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 包厢基础费用，不允许为空。
     */
    @Column(name = "base_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFee;

    /**
     * 在实体持久化前，自动为 roomId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate()
    {
        if (this.roomId == null)
        {
            this.roomId = UUID.randomUUID().toString();
        }
    }
}
