package com.harukite.canteen.service;

import com.harukite.canteen.dto.CanteenDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 食堂服务接口。
 * 定义食堂相关的业务操作。
 */
public interface CanteenService
{

    /**
     * 创建新食堂。
     *
     * @param canteenDto 包含食堂信息的 DTO
     * @param imageFile  食堂图片文件（可选）
     * @return 创建成功的食堂 DTO
     */
    CanteenDto createCanteen(CanteenDto canteenDto, MultipartFile imageFile); // 修改方法签名

    /**
     * 根据ID获取食堂详情。
     *
     * @param canteenId 食堂ID
     * @return 食堂 DTO
     */
    CanteenDto getCanteenById(String canteenId);

    /**
     * 获取所有食堂列表。
     *
     * @return 食堂 DTO 列表
     */
    List<CanteenDto> getAllCanteens();

    /**
     * 更新食堂信息。
     *
     * @param canteenId         要更新的食堂ID
     * @param updatedCanteenDto 包含更新信息的食堂 DTO
     * @param imageFile         食堂图片文件（可选，如果提供则更新图片）
     * @return 更新后的食堂 DTO
     */
    CanteenDto updateCanteen(String canteenId, CanteenDto updatedCanteenDto, MultipartFile imageFile); // 修改方法签名

    /**
     * 删除食堂。
     *
     * @param canteenId 要删除的食堂ID
     */
    void deleteCanteen(String canteenId);
}
