package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.CanteenDto;
import com.harukite.canteen.dto.CanteenImageDto; // 导入 CanteenImageDto
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.repository.CanteenRepository;
import com.harukite.canteen.service.CanteenImageService;
import com.harukite.canteen.service.CanteenService;
import com.harukite.canteen.service.CosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 导入Slf4j
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // 导入 MultipartFile

import java.io.IOException; // 导入 IOException
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食堂服务接口的实现类。
 * 包含食堂的创建、查询、更新和删除业务逻辑。
 */
@Service
@RequiredArgsConstructor
@Slf4j // Lombok 注解，用于生成日志记录器
public class CanteenServiceImpl implements CanteenService
{

    private final CanteenRepository canteenRepository;
    private final CosService cosService; // 注入 CosService
    private final CanteenImageService canteenImageService; // 注入 CanteenImageService

    /**
     * 创建新食堂。
     *
     * @param canteenDto 包含食堂信息的 DTO
     * @param imageFile 食堂图片文件（可选）
     * @return 创建成功的食堂 DTO
     * @throws DuplicateEntryException 如果食堂名称已存在
     * @throws RuntimeException 如果图片上传失败
     */
    @Override
    @Transactional
    public CanteenDto createCanteen(CanteenDto canteenDto, MultipartFile imageFile) {
        // 检查食堂名称是否已存在
        if (canteenRepository.findByName(canteenDto.getName()).isPresent()) {
            throw new DuplicateEntryException("Canteen with name '" + canteenDto.getName() + "' already exists.");
        }

        Canteen canteen = new Canteen();
        // canteen.canteenId 会在 @PrePersist 中自动生成
        canteen.setName(canteenDto.getName());
        canteen.setDescription(canteenDto.getDescription());
        canteen.setLocation(canteenDto.getLocation());
        canteen.setOpeningHours(canteenDto.getOpeningHours());
        canteen.setContactPhone(canteenDto.getContactPhone());

        // 处理主图片上传
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cosService.uploadFile(imageFile, "canteens/"); // 上传到 canteens 文件夹
                canteen.setImageUrl(imageUrl);
            } catch (IOException e) {
                log.error("Failed to upload canteen main image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload canteen main image: " + e.getMessage(), e);
            }
        } else {
            canteen.setImageUrl(canteenDto.getImageUrl()); // 如果没有新文件，使用DTO中可能已有的URL
        }

        // 注意：这里只处理主图片。额外的图片将通过 CanteenImageController/Service 独立管理。
        // 如果 canteenDto 中包含 additionalImages，可以在这里处理，但通常创建时只上传主图，额外图后续添加。

        Canteen savedCanteen = canteenRepository.save(canteen);
        return convertToDto(savedCanteen);
    }

    /**
     * 根据ID获取食堂详情。
     *
     * @param canteenId 食堂ID
     * @return 食堂 DTO
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public CanteenDto getCanteenById(String canteenId)
    {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));
        return convertToDto(canteen);
    }

    /**
     * 获取所有食堂列表。
     *
     * @return 食堂 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<CanteenDto> getAllCanteens()
    {
        return canteenRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新食堂信息。
     *
     * @param canteenId 要更新的食堂ID
     * @param updatedCanteenDto 包含更新信息的食堂 DTO
     * @param imageFile 食堂图片文件（可选，如果提供则更新图片）
     * @return 更新后的食堂 DTO
     * @throws ResourceNotFoundException 如果食堂不存在
     * @throws DuplicateEntryException 如果更新后的食堂名称已存在且不属于当前食堂
     * @throws RuntimeException 如果图片上传或删除失败
     */
    @Override
    @Transactional
    public CanteenDto updateCanteen(String canteenId, CanteenDto updatedCanteenDto, MultipartFile imageFile)
    {
        Canteen existingCanteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));

        // 检查更新后的食堂名称是否已存在且不属于当前食堂
        if (updatedCanteenDto.getName() != null && !updatedCanteenDto.getName().equals(existingCanteen.getName()))
        {
            canteenRepository.findByName(updatedCanteenDto.getName()).ifPresent(canteen -> {
                if (!canteen.getCanteenId().equals(canteenId))
                {
                    throw new DuplicateEntryException("Canteen with name '" + updatedCanteenDto.getName() + "' already exists.");
                }
            });
        }

        existingCanteen.setName(updatedCanteenDto.getName());
        existingCanteen.setDescription(updatedCanteenDto.getDescription());
        existingCanteen.setLocation(updatedCanteenDto.getLocation());
        existingCanteen.setOpeningHours(updatedCanteenDto.getOpeningHours());
        existingCanteen.setContactPhone(updatedCanteenDto.getContactPhone());

        // 处理图片更新
        if (imageFile != null && !imageFile.isEmpty())
        {
            // 如果存在旧图片，先删除旧图片
            if (existingCanteen.getImageUrl() != null && !existingCanteen.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingCanteen.getImageUrl());
            }
            try
            {
                String newImageUrl = cosService.uploadFile(imageFile, "canteens/");
                existingCanteen.setImageUrl(newImageUrl);
            }
            catch (IOException e)
            {
                log.error("Failed to upload new canteen image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload new canteen image: " + e.getMessage(), e);
            }
        }
        else if (updatedCanteenDto.getImageUrl() != null && updatedCanteenDto.getImageUrl().isEmpty())
        {
            // 如果DTO中的imageUrl被显式设置为空字符串，表示清除图片
            if (existingCanteen.getImageUrl() != null && !existingCanteen.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingCanteen.getImageUrl());
            }
            existingCanteen.setImageUrl(null);
        }
        // 如果 imageFile 为 null 且 updatedCanteenDto.getImageUrl() 也为 null，则保持不变

        Canteen savedCanteen = canteenRepository.save(existingCanteen);
        return convertToDto(savedCanteen);
    }

    /**
     * 删除食堂。
     * 同时删除 COS 中关联的主图片和所有额外图片。
     *
     * @param canteenId 要删除的食堂ID
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional
    public void deleteCanteen(String canteenId) {
        Canteen canteenToDelete = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));

        // 删除 COS 中的主图片
        if (canteenToDelete.getImageUrl() != null && !canteenToDelete.getImageUrl().isEmpty()) {
            cosService.deleteFile(canteenToDelete.getImageUrl());
        }

        // 删除所有关联的额外图片 (级联删除会处理数据库记录，这里需要手动删除 COS 文件)
        if (canteenToDelete.getAdditionalImages() != null) {
            canteenToDelete.getAdditionalImages().forEach(image -> {
                if (image.getImageUrl() != null && !image.getImageUrl().isEmpty()) {
                    cosService.deleteFile(image.getImageUrl());
                }
            });
        }

        canteenRepository.delete(canteenToDelete);
    }

    /**
     * 辅助方法：将 Canteen 实体转换为 CanteenDto。
     * 同时获取所有关联的额外图片。
     *
     * @param canteen Canteen 实体
     * @return CanteenDto
     */
    private CanteenDto convertToDto(Canteen canteen) {
        // 获取额外图片 DTO 列表
        List<CanteenImageDto> additionalImageDtos = canteen.getAdditionalImages().stream()
                .map(image -> new CanteenImageDto(
                        image.getImageId(),
                        image.getCanteen().getCanteenId(),
                        image.getImageUrl(),
                        image.getDescription(),
                        image.getUploadTime()
                ))
                .collect(Collectors.toList());

        return new CanteenDto(
                canteen.getCanteenId(),
                canteen.getName(),
                canteen.getDescription(),
                canteen.getLocation(),
                canteen.getOpeningHours(),
                canteen.getContactPhone(),
                canteen.getImageUrl(), // 主图片URL
                additionalImageDtos // 额外图片列表
        );
    }
}
