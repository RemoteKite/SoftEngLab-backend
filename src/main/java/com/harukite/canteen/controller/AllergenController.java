package com.harukite.canteen.controller;

import com.harukite.canteen.dto.AllergenDto;
import com.harukite.canteen.service.AllergenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理过敏原。
 * 提供创建、查询、更新和删除过敏原的 API 接口。
 */
@RestController
@RequestMapping("/api/allergens")
@RequiredArgsConstructor
public class AllergenController
{

    private final AllergenService allergenService;

    /**
     * 创建一个新的过敏原。
     * URL: POST /api/allergens
     *
     * @param allergenDto 包含过敏原名称的 DTO
     * @return 创建成功的过敏原 DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建过敏原
    public ResponseEntity<AllergenDto> createAllergen(@Valid @RequestBody AllergenDto allergenDto)
    {
        AllergenDto createdAllergen = allergenService.createAllergen(allergenDto);
        return new ResponseEntity<>(createdAllergen, HttpStatus.CREATED);
    }

    /**
     * 根据 ID 获取过敏原详情。
     * URL: GET /api/allergens/{id}
     *
     * @param id 过敏原 ID
     * @return 对应的过敏原 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<AllergenDto> getAllergenById(@PathVariable String id)
    {
        AllergenDto allergen = allergenService.getAllergenById(id);
        return ResponseEntity.ok(allergen);
    }

    /**
     * 获取所有过敏原列表。
     * URL: GET /api/allergens
     *
     * @return 过敏原 DTO 列表
     */
    @GetMapping
    public ResponseEntity<List<AllergenDto>> getAllAllergens()
    {
        List<AllergenDto> allergens = allergenService.getAllAllergens();
        return ResponseEntity.ok(allergens);
    }

    /**
     * 更新一个现有过敏原。
     * URL: PUT /api/allergens/{id}
     *
     * @param id                 要更新的过敏原 ID
     * @param updatedAllergenDto 包含更新信息的 DTO
     * @return 更新后的过敏原 DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新过敏原
    public ResponseEntity<AllergenDto> updateAllergen(@PathVariable String id, @Valid @RequestBody AllergenDto updatedAllergenDto)
    {
        AllergenDto allergen = allergenService.updateAllergen(id, updatedAllergenDto);
        return ResponseEntity.ok(allergen);
    }

    /**
     * 根据 ID 删除一个过敏原。
     * URL: DELETE /api/allergens/{id}
     *
     * @param id 要删除的过敏原 ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除过敏原
    public ResponseEntity<Void> deleteAllergen(@PathVariable String id)
    {
        allergenService.deleteAllergen(id);
        return ResponseEntity.noContent().build();
    }
}
