package com.harukite.canteen.controller;

import com.harukite.canteen.dto.DietaryTagDto;
import com.harukite.canteen.service.DietaryTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理饮食习惯标签。
 * 提供创建、查询、更新和删除饮食习惯标签的 API 接口。
 */
@RestController
@RequestMapping("/api/dietary-tags")
@RequiredArgsConstructor
public class DietaryTagController
{

    private final DietaryTagService dietaryTagService;

    /**
     * 创建一个新的饮食习惯标签。
     * URL: POST /api/dietary-tags
     *
     * @param dietaryTagDto 包含标签名称的 DTO
     * @return 创建成功的饮食习惯标签 DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建饮食习惯标签
    public ResponseEntity<DietaryTagDto> createDietaryTag(@Valid @RequestBody DietaryTagDto dietaryTagDto)
    {
        DietaryTagDto createdDietaryTag = dietaryTagService.createDietaryTag(dietaryTagDto);
        return new ResponseEntity<>(createdDietaryTag, HttpStatus.CREATED);
    }

    /**
     * 根据 ID 获取饮食习惯标签详情。
     * URL: GET /api/dietary-tags/{id}
     *
     * @param id 饮食习惯标签 ID
     * @return 对应的饮食习惯标签 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<DietaryTagDto> getDietaryTagById(@PathVariable String id)
    {
        DietaryTagDto dietaryTag = dietaryTagService.getDietaryTagById(id);
        return ResponseEntity.ok(dietaryTag);
    }

    /**
     * 获取所有饮食习惯标签列表。
     * URL: GET /api/dietary-tags
     *
     * @return 饮食习惯标签 DTO 列表
     */
    @GetMapping
    public ResponseEntity<List<DietaryTagDto>> getAllDietaryTags()
    {
        List<DietaryTagDto> dietaryTags = dietaryTagService.getAllDietaryTags();
        return ResponseEntity.ok(dietaryTags);
    }

    /**
     * 更新一个现有饮食习惯标签。
     * URL: PUT /api/dietary-tags/{id}
     *
     * @param id                   要更新的饮食习惯标签 ID
     * @param updatedDietaryTagDto 包含更新信息的 DTO
     * @return 更新后的饮食习惯标签 DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新饮食习惯标签
    public ResponseEntity<DietaryTagDto> updateDietaryTag(@PathVariable String id, @Valid @RequestBody DietaryTagDto updatedDietaryTagDto)
    {
        DietaryTagDto dietaryTag = dietaryTagService.updateDietaryTag(id, updatedDietaryTagDto);
        return ResponseEntity.ok(dietaryTag);
    }

    /**
     * 根据 ID 删除一个饮食习惯标签。
     * URL: DELETE /api/dietary-tags/{id}
     *
     * @param id 要删除的饮食习惯标签 ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除饮食习惯标签
    public ResponseEntity<Void> deleteDietaryTag(@PathVariable String id)
    {
        dietaryTagService.deleteDietaryTag(id);
        return ResponseEntity.noContent().build();
    }
}
