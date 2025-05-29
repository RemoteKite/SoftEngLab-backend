package com.harukite.canteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 食堂图片实体类，对应数据库中的 'canteen_images' 表。
 * 用于存储食堂的额外图片。
 */
@Entity
@Table(name = "canteen_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenImage {

    /**
     * 图片唯一ID，作为主键。
     * 在持久化前自动生成 UUID。
     */
    @Id
    @Column(name = "image_id", nullable = false, updatable = false)
    private String imageId;

    /**
     * 所属食堂。多对一关系。
     * 当食堂被删除时，关联的图片也会被删除 (ON DELETE CASCADE)。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    /**
     * 图片的 URL。
     */
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /**
     * 图片的描述。
     */
    @Column(name = "description")
    private String description;

    /**
     * 图片上传时间，在创建时自动设置。
     */
    @CreationTimestamp
    @Column(name = "upload_time", nullable = false, updatable = false)
    private LocalDateTime uploadTime;

    /**
     * 在实体持久化前，自动为 imageId 生成一个 UUID。
     */
    @PrePersist
    protected void onCreate() {
        if (this.imageId == null) {
            this.imageId = UUID.randomUUID().toString();
        }
    }
}

