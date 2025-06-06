package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.DietaryTagDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.DietaryTag;
import com.harukite.canteen.repository.DietaryTagRepository;
import com.harukite.canteen.repository.DishRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.DietaryTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 饮食习惯标签服务接口的实现类。
 * 包含饮食习惯标签的创建、查询、更新和删除等业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class DietaryTagServiceImpl implements DietaryTagService
{

    private final DietaryTagRepository dietaryTagRepository;
    private final DishRepository dishRepository; // 注入 DishRepository
    private final UserRepository userRepository; // 注入 UserRepository

    /**
     * 创建一个新的饮食习惯标签。
     *
     * @param dietaryTagDto 包含标签名称的 DTO
     * @return 创建成功的饮食习惯标签 DTO
     * @throws DuplicateEntryException 如果标签名称已存在
     */
    @Override
    @Transactional
    public DietaryTagDto createDietaryTag(DietaryTagDto dietaryTagDto)
    {
        // 检查标签名称是否已存在
        Optional<DietaryTag> existingTag = dietaryTagRepository.findByTagName(dietaryTagDto.getTagName());
        if (existingTag.isPresent())
        {
            throw new DuplicateEntryException("Dietary tag with name '" + dietaryTagDto.getTagName() + "' already exists.");
        }

        DietaryTag dietaryTag = new DietaryTag();
        dietaryTag.setTagId(UUID.randomUUID().toString()); // 生成新的ID
        dietaryTag.setTagName(dietaryTagDto.getTagName());
        DietaryTag savedDietaryTag = dietaryTagRepository.save(dietaryTag);
        return convertToDto(savedDietaryTag);
    }

    /**
     * 根据 ID 获取饮食习惯标签详情。
     *
     * @param id 饮食习惯标签 ID
     * @return 对应的饮食习惯标签 DTO
     * @throws ResourceNotFoundException 如果标签不存在
     */
    @Override
    @Transactional(readOnly = true)
    public DietaryTagDto getDietaryTagById(String id)
    {
        DietaryTag dietaryTag = dietaryTagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found with ID: " + id));
        return convertToDto(dietaryTag);
    }

    /**
     * 获取所有饮食习惯标签列表。
     *
     * @return 饮食习惯标签 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<DietaryTagDto> getAllDietaryTags()
    {
        return dietaryTagRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新一个现有饮食习惯标签。
     *
     * @param id                   要更新的饮食习惯标签 ID
     * @param updatedDietaryTagDto 包含更新信息的 DTO
     * @return 更新后的饮食习惯标签 DTO
     * @throws ResourceNotFoundException 如果标签不存在
     * @throws DuplicateEntryException   如果更新后的名称已存在于其他标签
     */
    @Override
    @Transactional
    public DietaryTagDto updateDietaryTag(String id, DietaryTagDto updatedDietaryTagDto)
    {
        DietaryTag existingDietaryTag = dietaryTagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found with ID: " + id));

        // 如果名称有变化，检查新名称是否已被其他标签占用
        if (!existingDietaryTag.getTagName().equals(updatedDietaryTagDto.getTagName()))
        {
            Optional<DietaryTag> tagWithNewName = dietaryTagRepository.findByTagName(updatedDietaryTagDto.getTagName());
            if (tagWithNewName.isPresent() && !tagWithNewName.get().getTagId().equals(id))
            {
                throw new DuplicateEntryException("Dietary tag with name '" + updatedDietaryTagDto.getTagName() + "' already exists.");
            }
        }

        existingDietaryTag.setTagName(updatedDietaryTagDto.getTagName());
        DietaryTag savedDietaryTag = dietaryTagRepository.save(existingDietaryTag);
        return convertToDto(savedDietaryTag);
    }

    /**
     * 根据 ID 删除一个饮食习惯标签。
     *
     * @param id 要删除的饮食习惯标签 ID
     * @throws ResourceNotFoundException 如果标签不存在
     * @throws IllegalStateException     如果标签被菜品或用户引用，不能直接删除
     */
    @Override
    @Transactional
    public void deleteDietaryTag(String id)
    {
        DietaryTag dietaryTag = dietaryTagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found with ID: " + id));

        // 检查此饮食习惯标签是否被任何 Dish 引用
        // 由于 DietaryTag 是 Dish 的 mappedBy 端，可以直接通过 getDishes() 检查
        if (!dietaryTag.getDishes().isEmpty())
        {
            throw new IllegalStateException("Dietary tag is currently associated with dishes and cannot be deleted.");
        }

    }

    /**
     * 辅助方法：将 DietaryTag 实体转换为 DietaryTagDto。
     *
     * @param dietaryTag DietaryTag 实体
     * @return DietaryTagDto
     */
    private DietaryTagDto convertToDto(DietaryTag dietaryTag)
    {
        return new DietaryTagDto(dietaryTag.getTagId(), dietaryTag.getTagName());
    }
}
