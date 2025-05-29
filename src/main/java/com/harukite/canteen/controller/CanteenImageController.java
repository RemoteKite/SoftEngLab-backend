package com.harukite.canteen.controller;

import com.harukite.canteen.dto.CanteenImageDto;
import com.harukite.canteen.service.CanteenImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST 控制器，用于管理食堂的额外图片。
 * 提供图片的上传、查询、更新描述和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/canteen-images")
@RequiredArgsConstructor
public class CanteenImageController {

    private final CanteenImageService canteenImageService;

    /**
     * 为指定食堂上传并创建新的图片记录。
     * URL: POST /api/canteen-images/canteen/{canteenId}
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param canteenId 食堂ID
     * @param description 图片描述（可选，通过 @RequestPart 接收）
     * @param imageFile 图片文件（通过 @RequestPart 接收）
     * @return 创建成功的食堂图片 DTO
     */
    @PostMapping(value = "/canteen/{canteenId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能上传图片
    public ResponseEntity<CanteenImageDto> createCanteenImage(
            @PathVariable String canteenId,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart("image") MultipartFile imageFile) {
        CanteenImageDto createdImage = canteenImageService.createCanteenImage(canteenId, description, imageFile);
        return new ResponseEntity<>(createdImage, HttpStatus.CREATED);
    }

    /**
     * 根据图片ID获取图片详情。
     * URL: GET /api/canteen-images/{imageId}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param imageId 图片ID
     * @return 图片 DTO
     */
    @GetMapping("/{imageId}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<CanteenImageDto> getCanteenImageById(@PathVariable String imageId) {
        CanteenImageDto canteenImage = canteenImageService.getCanteenImageById(imageId);
        return ResponseEntity.ok(canteenImage);
    }

    /**
     * 根据食堂ID获取所有关联的图片列表。
     * URL: GET /api/canteen-images/canteen/{canteenId}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param canteenId 食堂ID
     * @return 图片 DTO 列表
     */
    @GetMapping("/canteen/{canteenId}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<CanteenImageDto>> getCanteenImagesByCanteenId(@PathVariable String canteenId) {
        List<CanteenImageDto> images = canteenImageService.getCanteenImagesByCanteenId(canteenId);
        return ResponseEntity.ok(images);
    }

    /**
     * 更新食堂图片信息（例如描述）。
     * URL: PUT /api/canteen-images/{imageId}/description
     * (需要管理员或工作人员权限)
     *
     * @param imageId 要更新的图片ID
     * @param updatedDescription 新的图片描述
     * @return 更新后的图片 DTO
     */
    @PutMapping("/{imageId}/description")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新图片描述
    public ResponseEntity<CanteenImageDto> updateCanteenImageDescription(
            @PathVariable String imageId,
            @RequestParam String updatedDescription) { // 简单更新描述，使用 @RequestParam
        CanteenImageDto updatedImage = canteenImageService.updateCanteenImage(imageId, updatedDescription);
        return ResponseEntity.ok(updatedImage);
    }

    /**
     * 根据图片ID删除食堂图片。
     * URL: DELETE /api/canteen-images/{imageId}
     * (需要管理员或工作人员权限)
     *
     * @param imageId 要删除的图片ID
     * @return 无内容响应
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除图片
    public ResponseEntity<Void> deleteCanteenImage(@PathVariable String imageId) {
        canteenImageService.deleteCanteenImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
