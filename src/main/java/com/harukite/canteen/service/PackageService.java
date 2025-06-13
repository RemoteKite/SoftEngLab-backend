package com.harukite.canteen.service;

import com.harukite.canteen.dto.PackageDto;

import java.util.List;

/**
 * 宴会套餐服务接口。
 * 定义套餐的创建、查询、更新和删除操作。
 */
public interface PackageService {

    /**
     * 创建新套餐。
     *
     * @param packageDto 包含套餐信息的 DTO
     * @return 创建成功的套餐 DTO
     */
    PackageDto createPackage(PackageDto packageDto);

    /**
     * 根据套餐ID获取套餐详情。
     *
     * @param packageId 套餐ID
     * @return 套餐 DTO
     */
    PackageDto getPackageById(String packageId);

    /**
     * 获取所有套餐列表。
     *
     * @return 套餐 DTO 列表
     */
    List<PackageDto> getAllPackages();

    /**
     * 更新套餐信息。
     *
     * @param packageId 要更新的套餐ID
     * @param updatedPackageDto 包含更新信息的套餐 DTO
     * @return 更新后的套餐 DTO
     */
    PackageDto updatePackage(String packageId, PackageDto updatedPackageDto);

    /**
     * 删除套餐。
     *
     * @param packageId 要删除的套餐ID
     */
    void deletePackage(String packageId);
}
