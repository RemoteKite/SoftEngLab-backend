package com.harukite.canteen.controller;

import com.harukite.canteen.dto.PackageDto;
import com.harukite.canteen.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 导入 PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理宴会套餐。
 * 提供套餐的创建、查询、更新和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    /**
     * 创建新套餐。
     * URL: POST /api/packages
     * (需要管理员或工作人员权限)
     *
     *
     * @param packageDto 包含套餐信息的 DTO
     * @return 创建成功的套餐 DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建套餐
    public ResponseEntity<PackageDto> createPackage(@Valid @RequestBody PackageDto packageDto) {
        PackageDto createdPackage = packageService.createPackage(packageDto);
        return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
    }

    /**
     * 根据套餐ID获取套餐详情。
     * URL: GET /api/packages/{id}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param id 套餐ID
     * @return 套餐 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<PackageDto> getPackageById(@PathVariable String id) {
        PackageDto packageDto = packageService.getPackageById(id);
        return ResponseEntity.ok(packageDto);
    }

    /**
     * 获取所有套餐列表。
     * URL: GET /api/packages
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @return 套餐 DTO 列表
     */
    @GetMapping
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<PackageDto>> getAllPackages() {
        List<PackageDto> packages = packageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    /**
     * 更新套餐信息。
     * URL: PUT /api/packages/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要更新的套餐ID
     * @param updatedPackageDto 包含更新信息的 DTO
     * @return 更新后的套餐 DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新套餐
    public ResponseEntity<PackageDto> updatePackage(@PathVariable String id, @Valid @RequestBody PackageDto updatedPackageDto) {
        PackageDto packageDto = packageService.updatePackage(id, updatedPackageDto);
        return ResponseEntity.ok(packageDto);
    }

    /**
     * 删除套餐。
     * URL: DELETE /api/packages/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要删除的套餐ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除套餐
    public ResponseEntity<Void> deletePackage(@PathVariable String id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
