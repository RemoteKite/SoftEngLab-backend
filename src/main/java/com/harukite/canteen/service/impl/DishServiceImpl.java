package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.*;
import com.harukite.canteen.repository.*;
import com.harukite.canteen.service.CosService;
import com.harukite.canteen.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜品服务接口的实现类。
 * 包含菜品的创建、查询、更新、删除和筛选业务逻辑。
 */
@Service
@RequiredArgsConstructor
@Slf4j // Lombok 注解，用于生成日志记录器
public class DishServiceImpl implements DishService
{

    private final DishRepository dishRepository;
    private final CanteenRepository canteenRepository;
    private final DietaryTagRepository dietaryTagRepository;
    private final AllergenRepository allergenRepository;
    private final RatingReviewRepository ratingReviewRepository;
    private final CosService cosService; // 注入 CosService

    /**
     * 创建新菜品。
     *
     * @param dishDto   包含菜品信息的 DTO
     * @param imageFile 菜品图片文件（可选）
     * @return 创建成功的菜品 DTO
     * @throws ResourceNotFoundException 如果所属食堂、饮食标签或过敏原不存在
     * @throws RuntimeException          如果图片上传失败
     */
    @Override
    @Transactional
    public DishDto createDish(DishDto dishDto, MultipartFile imageFile)
    {
        Canteen canteen = canteenRepository.findById(dishDto.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + dishDto.getCanteenId()));

        Dish dish = new Dish();
        dish.setCanteen(canteen);
        dish.setName(dishDto.getName());
        dish.setDescription(dishDto.getDescription());
        dish.setPrice(dishDto.getPrice());
        dish.setIsAvailable(dishDto.getIsAvailable() != null ? dishDto.getIsAvailable() : true); // 默认为可用

        // 处理图片上传
        if (imageFile != null && !imageFile.isEmpty())
        {
            try
            {
                String imageUrl = cosService.uploadFile(imageFile, "dishes/"); // 上传到 dishes 文件夹
                dish.setImageUrl(imageUrl);
            }
            catch (IOException e)
            {
                log.error("Failed to upload dish image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload dish image: " + e.getMessage(), e);
            }
        }
        else
        {
            dish.setImageUrl(dishDto.getImageUrl()); // 如果没有新文件，使用DTO中可能已有的URL
        }


        // 处理饮食标签
        if (dishDto.getDietaryTagIds() != null && !dishDto.getDietaryTagIds().isEmpty())
        {
            Set<DietaryTag> dietaryTags = dishDto.getDietaryTagIds().stream()
                    .map(tagId -> dietaryTagRepository.findById(tagId)
                            .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found: " + tagId)))
                    .collect(Collectors.toSet());
            dish.setDietaryTags(dietaryTags);
        }
        else
        {
            dish.setDietaryTags(new HashSet<>());
        }

        // 处理过敏原
        if (dishDto.getAllergenIds() != null && !dishDto.getAllergenIds().isEmpty())
        {
            Set<Allergen> allergens = dishDto.getAllergenIds().stream()
                    .map(allergenId -> allergenRepository.findById(allergenId)
                            .orElseThrow(() -> new ResourceNotFoundException("Allergen not found: " + allergenId)))
                    .collect(Collectors.toSet());
            dish.setAllergens(allergens);
        }
        else
        {
            dish.setAllergens(new HashSet<>());
        }

        Dish savedDish = dishRepository.save(dish);
        dishRepository.flush();
        return convertToDto(savedDish);
    }

    /**
     * 根据菜品ID获取菜品详情。
     *
     * @param dishId 菜品ID
     * @return 菜品 DTO
     * @throws ResourceNotFoundException 如果菜品不存在
     */
    @Override
    @Transactional(readOnly = true)
    public DishDto getDishById(String dishId)
    {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
        return convertToDto(dish);
    }

    /**
     * 获取所有菜品列表。
     *
     * @return 菜品 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getAllDishes()
    {
        return dishRepository.findAllWithDetails().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新菜品信息。
     *
     * @param dishId         要更新的菜品ID
     * @param updatedDishDto 包含更新信息的菜品 DTO
     * @param imageFile      菜品图片文件（可选，如果提供则更新图片）
     * @return 更新后的菜品 DTO
     * @throws ResourceNotFoundException 如果菜品、所属食堂、饮食标签或过敏原不存在
     * @throws RuntimeException          如果图片上传失败或删除失败
     */
    @Override
    @Transactional
    public DishDto updateDish(String dishId, DishDto updatedDishDto, MultipartFile imageFile)
    {
        Dish existingDish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));

        // 更新所属食堂（如果提供了新的 canteenId）
        if (updatedDishDto.getCanteenId() != null && !updatedDishDto.getCanteenId().equals(existingDish.getCanteen().getCanteenId()))
        {
            Canteen newCanteen = canteenRepository.findById(updatedDishDto.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + updatedDishDto.getCanteenId()));
            existingDish.setCanteen(newCanteen);
        }

        // 更新基本信息
        if (updatedDishDto.getName() != null)
        {
            existingDish.setName(updatedDishDto.getName());
        }
        if (updatedDishDto.getDescription() != null)
        {
            existingDish.setDescription(updatedDishDto.getDescription());
        }
        if (updatedDishDto.getPrice() != null)
        {
            existingDish.setPrice(updatedDishDto.getPrice());
        }
        if (updatedDishDto.getIsAvailable() != null)
        {
            existingDish.setIsAvailable(updatedDishDto.getIsAvailable());
        }

        // 处理图片更新
        if (imageFile != null && !imageFile.isEmpty())
        {
            // 如果存在旧图片，先删除旧图片
            if (existingDish.getImageUrl() != null && !existingDish.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingDish.getImageUrl());
            }
            try
            {
                String newImageUrl = cosService.uploadFile(imageFile, "dishes/");
                existingDish.setImageUrl(newImageUrl);
            }
            catch (IOException e)
            {
                log.error("Failed to upload new dish image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload new dish image: " + e.getMessage(), e);
            }
        }
        else if (updatedDishDto.getImageUrl() != null && updatedDishDto.getImageUrl().isEmpty())
        {
            // 如果DTO中的imageUrl被显式设置为空字符串，表示清除图片
            if (existingDish.getImageUrl() != null && !existingDish.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingDish.getImageUrl());
            }
            existingDish.setImageUrl(null);
        }
        // 如果 imageFile 为 null 且 updatedDishDto.getImageUrl() 也为 null，则保持不变

        // 更新饮食标签
        if (updatedDishDto.getDietaryTagIds() != null)
        { // 如果提供了，则更新；如果为null，则清空
            Set<DietaryTag> newTags = updatedDishDto.getDietaryTagIds().stream()
                    .map(tagId -> dietaryTagRepository.findById(tagId)
                            .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found: " + tagId)))
                    .collect(Collectors.toSet());
            existingDish.setDietaryTags(newTags);
        }
        else
        {
            existingDish.setDietaryTags(new HashSet<>()); // 如果传入null，清空所有标签
        }

        // 更新过敏原
        if (updatedDishDto.getAllergenIds() != null)
        { // 如果提供了，则更新；如果为null，则清空
            Set<Allergen> newAllergens = updatedDishDto.getAllergenIds().stream()
                    .map(allergenId -> allergenRepository.findById(allergenId)
                            .orElseThrow(() -> new ResourceNotFoundException("Allergen not found: " + allergenId)))
                    .collect(Collectors.toSet());
            existingDish.setAllergens(newAllergens);
        }
        else
        {
            existingDish.setAllergens(new HashSet<>()); // 如果传入null，清空所有过敏原
        }

        Dish savedDish = dishRepository.save(existingDish);
        dishRepository.flush(); // 确保所有更改都被持久化到数据库
        return convertToDto(savedDish);
    }

    /**
     * 删除菜品。
     * 同时删除 COS 中关联的图片。
     *
     * @param dishId 要删除的菜品ID
     * @throws ResourceNotFoundException 如果菜品不存在
     */
    @Override
    @Transactional
    public void deleteDish(String dishId)
    {
        Dish dishToDelete = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));

        // 删除 COS 中的图片
        if (dishToDelete.getImageUrl() != null && !dishToDelete.getImageUrl().isEmpty())
        {
            cosService.deleteFile(dishToDelete.getImageUrl());
        }

        dishRepository.delete(dishToDelete);
        dishRepository.flush(); // 确保所有更改都被持久化到数据库
    }

    /**
     * 辅助方法：将 Dish 实体转换为 DishDto。
     * 同时计算菜品的平均评分。
     *
     * @param dish Dish 实体
     * @return DishDto
     */
    private DishDto convertToDto(Dish dish)
    {
        Set<DietaryTag> dietaryTags = dish.getDietaryTags();
        Set<Allergen> allergens = dish.getAllergens();

        // 获取饮食标签Id
        List<String> dietaryTagIds = dietaryTags.stream()
                .map(DietaryTag::getTagId)
                .collect(Collectors.toList());
        // 获取过敏原Id
        List<String> allergenIds = allergens.stream()
                .map(Allergen::getAllergenId)
                .collect(Collectors.toList());

        // 获取饮食标签名称
        List<String> dietaryTagNames = dietaryTags.stream()
                .map(DietaryTag::getTagName)
                .collect(Collectors.toList());

        // 获取过敏原名称
        List<String> allergenNames = allergens.stream()
                .map(Allergen::getAllergenName)
                .collect(Collectors.toList());

        // 计算平均评分
        List<RatingReview> reviews = ratingReviewRepository.findByDish(dish);
        double averageRating = 0.0;
        if (!reviews.isEmpty())
        {
            double sumRatings = reviews.stream()
                    .mapToInt(RatingReview::getRating)
                    .sum();
            averageRating = sumRatings / reviews.size();
        }

        return new DishDto(
                dish.getCanteen().getCanteenId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getImageUrl(), // 使用数据库存储的 URL
                dietaryTagIds,
                allergenIds,
                dish.getDishId(),
                dish.getIsAvailable(),
                dish.getCreatedAt(),
                dietaryTagNames,
                allergenNames,
                averageRating
        );
    }
}
