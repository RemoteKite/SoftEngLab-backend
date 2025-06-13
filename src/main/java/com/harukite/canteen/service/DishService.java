package com.harukite.canteen.service;

import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.dto.DishFilterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 菜品服务接口。
 * 定义菜品相关的业务操作，包括创建、查询、更新、删除和筛选。
 */
public interface DishService
{

    /**
     * 创建新菜品。
     *
     * @param dishDto   包含菜品信息的 DTO
     * @param imageFile 菜品图片文件（可选）
     * @return 创建成功的菜品 DTO
     */
    DishDto createDish(DishDto dishDto, MultipartFile imageFile);

    /**
     * 根据菜品ID获取菜品详情。
     *
     * @param dishId 菜品ID
     * @return 菜品 DTO
     */
    DishDto getDishById(String dishId);

    /**
     * 获取所有菜品列表。
     *
     * @return 菜品 DTO 列表
     */
    List<DishDto> getAllDishes();

    /**
     * 更新菜品信息。
     *
     * @param dishId         要更新的菜品ID
     * @param updatedDishDto 包含更新信息的菜品 DTO
     * @param imageFile      菜品图片文件（可选，如果提供则更新图片）
     * @return 更新后的菜品 DTO
     */
    DishDto updateDish(String dishId, DishDto updatedDishDto, MultipartFile imageFile);

    /**
     * 删除菜品。
     *
     * @param dishId 要删除的菜品ID
     */
    void deleteDish(String dishId);

    /**
     * 根据筛选条件查找菜品。
     *
     * @param filterRequest 菜品筛选请求 DTO
     * @return 符合条件的菜品 DTO 列表
     */
    List<DishDto> filterDishes(DishFilterRequest filterRequest);
}
