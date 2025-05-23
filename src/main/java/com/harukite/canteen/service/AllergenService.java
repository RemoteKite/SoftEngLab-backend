package com.harukite.canteen.service;

import com.harukite.canteen.dto.AllergenDto;

import java.util.List;

/**
 * 过敏原服务接口。
 * 定义过敏原的创建、查询、更新和删除操作。
 */
public interface AllergenService
{

    /**
     * 创建一个新的过敏原。
     *
     * @param allergenDto 包含过敏原名称的 DTO
     * @return 创建成功的过敏原 DTO
     */
    AllergenDto createAllergen(AllergenDto allergenDto);

    /**
     * 根据 ID 获取过敏原详情。
     *
     * @param id 过敏原 ID
     * @return 对应的过敏原 DTO
     */
    AllergenDto getAllergenById(String id);

    /**
     * 获取所有过敏原列表。
     *
     * @return 过敏原 DTO 列表
     */
    List<AllergenDto> getAllAllergens();

    /**
     * 更新一个现有过敏原。
     *
     * @param id                 要更新的过敏原 ID
     * @param updatedAllergenDto 包含更新信息的 DTO
     * @return 更新后的过敏原 DTO
     */
    AllergenDto updateAllergen(String id, AllergenDto updatedAllergenDto);

    /**
     * 根据 ID 删除一个过敏原。
     *
     * @param id 要删除的过敏原 ID
     */
    void deleteAllergen(String id);
}
