package com.harukite.canteen.service;

import com.harukite.canteen.dto.DietaryTagDto;

import java.util.List;

/**
 * 饮食习惯标签服务接口。
 * 定义饮食习惯标签的创建、查询、更新和删除操作。
 */
public interface DietaryTagService
{

    /**
     * 创建一个新的饮食习惯标签。
     *
     * @param dietaryTagDto 包含标签名称的 DTO
     * @return 创建成功的饮食习惯标签 DTO
     */
    DietaryTagDto createDietaryTag(DietaryTagDto dietaryTagDto);

    /**
     * 根据 ID 获取饮食习惯标签详情。
     *
     * @param id 饮食习惯标签 ID
     * @return 对应的饮食习惯标签 DTO
     */
    DietaryTagDto getDietaryTagById(String id);

    /**
     * 获取所有饮食习惯标签列表。
     *
     * @return 饮食习惯标签 DTO 列表
     */
    List<DietaryTagDto> getAllDietaryTags();

    /**
     * 更新一个现有饮食习惯标签。
     *
     * @param id                   要更新的饮食习惯标签 ID
     * @param updatedDietaryTagDto 包含更新信息的 DTO
     * @return 更新后的饮食习惯标签 DTO
     */
    DietaryTagDto updateDietaryTag(String id, DietaryTagDto updatedDietaryTagDto);

    /**
     * 根据 ID 删除一个饮食习惯标签。
     *
     * @param id 要删除的饮食习惯标签 ID
     */
    void deleteDietaryTag(String id);
}
