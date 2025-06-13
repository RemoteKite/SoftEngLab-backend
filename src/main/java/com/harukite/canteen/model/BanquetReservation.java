package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal; // 导入 BigDecimal
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 宴会预订实体类，对应数据库中的 'banquet_reservations' 表。
 */
@Entity
@Table(name = "banquet_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanquetReservation {

    /**
     * 宴会预订唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "banquet_id", nullable = false, updatable = false)
    private String banquetId;

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
     * 预订包厢。多对一关系。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id") // room_id 可以为空，如果预订不指定包厢
    private Room room;

    /**
     * 宴会日期，不允许为空。
     */
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    /**
     * 宴会时间，不允许为空。
     */
    @Column(name = "event_time", nullable = false)
    private LocalTime eventTime;

    /**
     * 宾客人数，不允许为空。
     */
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;

    /**
     * 联系人姓名，不允许为空。
     */
    @Column(name = "contact_name", nullable = false)
    private String contactName;

    /**
     * 联系人手机号，不允许为空。
     */
    @Column(name = "contact_phone_number", nullable = false)
    private String contactPhoneNumber;

    /**
     * 宴会目的。
     */
    @Column(name = "purpose")
    private String purpose;

    /**
     * 定制菜单请求的文字描述。
     * 这个字段可能不再需要，因为详细的定制菜单将通过 selectedDishItems 表示。
     * 可以考虑移除或将其用于存储自由文本的额外菜单请求。
     */
    @Column(name = "custom_menu_request", columnDefinition = "TEXT")
    private String customMenuRequest;

    /**
     * 是否包含生日蛋糕，默认为 false。
     */
    @Column(name = "has_birthday_cake")
    private Boolean hasBirthdayCake = false;

    /**
     * 特殊需求。
     */
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    /**
     * 宴会总价，不允许为空。
     */
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * 预订状态，使用枚举类型映射数据库的 ENUM。
     */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private BanquetStatus status = BanquetStatus.PENDING;

    /**
     * 确认时间。
     */
    @Column(name = "confirmation_date")
    private LocalDateTime confirmationDate;

    /**
     * 预订创建时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 宴会预订中选择的定制菜品项。一对多关系。
     * mappedBy 指向 BanquetReservationDishItem 实体中拥有关系管理权的字段名称 ("banquetReservation")。
     * CascadeType.ALL 表示对 BanquetReservation 的操作会级联到 BanquetReservationDishItem。
     * orphanRemoval = true 表示如果从集合中移除 BanquetReservationDishItem，它也会从数据库中删除。
     */
    @OneToMany(mappedBy = "banquetReservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<BanquetReservationDishItem> selectedDishItems = new HashSet<>(); // 新增：定制菜品项集合

    /**
     * 宴会预订中选择的套餐。多对多关系。
     * 拥有方，负责维护中间表 'banquet_reservation_packages'。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "banquet_reservation_packages",
            joinColumns = @JoinColumn(name = "banquet_id"),
            inverseJoinColumns = @JoinColumn(name = "package_id")
    )
    private Set<Package> selectedPackages = new HashSet<>();

    /**
     * 在实体持久化前，自动为 banquetId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate() {
        if (this.banquetId == null) {
            this.banquetId = UUID.randomUUID().toString();
        }
    }
}
