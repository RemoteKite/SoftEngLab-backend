package com.harukite.canteen.controller;

import com.harukite.canteen.dto.CanteenDto;
import com.harukite.canteen.service.CanteenService;
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
 * REST 控制器，用于管理食堂。
 * 提供食堂的创建、查询、更新和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/canteens")
@RequiredArgsConstructor
public class CanteenController
{

    private final CanteenService canteenService;

    /**
     * 创建新食堂。
     * URL: POST /api/canteens
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param canteenDto 包含食堂信息的 DTO
     * @param imageFile  食堂图片文件（可选）
     * @return 创建成功的食堂 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 声明接收 multipart/form-data
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建食堂
    public ResponseEntity<CanteenDto> createCanteen(
            @RequestPart("canteen") @Valid CanteenDto canteenDto, // 接收 DTO 部分
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    { // 接收文件部分，可选
        CanteenDto createdCanteen = canteenService.createCanteen(canteenDto, imageFile);
        return new ResponseEntity<>(createdCanteen, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取食堂详情。
     * URL: GET /api/canteens/{id}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param id 食堂ID
     * @return 食堂 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<CanteenDto> getCanteenById(@PathVariable String id)
    {
        CanteenDto canteen = canteenService.getCanteenById(id);
        return ResponseEntity.ok(canteen);
    }

    /**
     * 获取所有食堂列表。
     * URL: GET /api/canteens
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @return 食堂 DTO 列表
     */
    @GetMapping
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<CanteenDto>> getAllCanteens()
    {
        List<CanteenDto> canteens = canteenService.getAllCanteens();
        return ResponseEntity.ok(canteens);
    }

    /**
     * 更新食堂信息。
     * URL: PUT /api/canteens/{id}
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param id                要更新的食堂ID
     * @param updatedCanteenDto 包含更新信息的 DTO
     * @param imageFile         食堂图片文件（可选，如果提供则更新图片）
     * @return 更新后的食堂 DTO
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新食堂
    public ResponseEntity<CanteenDto> updateCanteen(
            @PathVariable String id,
            @RequestPart("canteen") @Valid CanteenDto updatedCanteenDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    {
        CanteenDto canteen = canteenService.updateCanteen(id, updatedCanteenDto, imageFile);
        return ResponseEntity.ok(canteen);
    }

    /**
     * 删除食堂。
     * URL: DELETE /api/canteens/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要删除的食堂ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除食堂
    public ResponseEntity<Void> deleteCanteen(@PathVariable String id)
    {
        canteenService.deleteCanteen(id);
        return ResponseEntity.noContent().build();
    }
}
