package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.CanteenImageDto;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.CanteenImage;
import com.harukite.canteen.repository.CanteenImageRepository;
import com.harukite.canteen.repository.CanteenRepository;
import com.harukite.canteen.service.CanteenImageService;
import com.harukite.canteen.service.CosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食堂图片服务接口的实现类。
 * 包含食堂图片的创建、查询、更新和删除业务逻辑。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CanteenImageServiceImpl implements CanteenImageService
{

    private final CanteenImageRepository canteenImageRepository;
    private final CanteenRepository canteenRepository;
    private final CosService cosService; // 注入 COS 服务

    /**
     * 为指定食堂上传并创建新的图片记录。
     *
     * @param canteenId 食堂ID
     * @param description 图片描述（可选）
     * @param imageFile 图片文件
     * @return 创建成功的食堂图片 DTO
     * @throws ResourceNotFoundException 如果食堂不存在
     * @throws RuntimeException 如果图片上传失败
     */
    @Override
    @Transactional
    public CanteenImageDto createCanteenImage(String canteenId, String description, MultipartFile imageFile) {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty.");
        }

        String imageUrl;
        try {
            imageUrl = cosService.uploadFile(imageFile, "canteen-additional-images/"); // 上传到专门的文件夹
        } catch (IOException e) {
            log.error("Failed to upload canteen additional image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload canteen additional image: " + e.getMessage(), e);
        }

        CanteenImage canteenImage = new CanteenImage();
        canteenImage.setCanteen(canteen);
        canteenImage.setImageUrl(imageUrl);
        canteenImage.setDescription(description);

        CanteenImage savedImage = canteenImageRepository.save(canteenImage);
        return convertToDto(savedImage);
    }

    /**
     * 根据图片ID获取图片详情。
     *
     * @param imageId 图片ID
     * @return 图片 DTO
     * @throws ResourceNotFoundException 如果图片不存在
     */
    @Override
    @Transactional(readOnly = true)
    public CanteenImageDto getCanteenImageById(String imageId) {
        CanteenImage canteenImage = canteenImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen image not found with ID: " + imageId));
        return convertToDto(canteenImage);
    }

    /**
     * 根据食堂ID获取所有关联的图片列表。
     *
     * @param canteenId 食堂ID
     * @return 图片 DTO 列表
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<CanteenImageDto> getCanteenImagesByCanteenId(String canteenId) {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));
        List<CanteenImage> images = canteenImageRepository.findByCanteen(canteen);
        return images.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新食堂图片信息（例如描述）。
     * 不支持直接替换图片文件，如果需要替换，请删除旧的再上传新的。
     *
     * @param imageId 要更新的图片ID
     * @param updatedDescription 新的图片描述（如果为 null 则不更新）
     * @return 更新后的图片 DTO
     * @throws ResourceNotFoundException 如果图片不存在
     */
    @Override
    @Transactional
    public CanteenImageDto updateCanteenImage(String imageId, String updatedDescription) {
        CanteenImage existingImage = canteenImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen image not found with ID: " + imageId));

        if (updatedDescription != null) {
            existingImage.setDescription(updatedDescription);
        }
        // 图片 URL 不在此方法中更新，因为文件替换应通过删除和重新创建实现

        CanteenImage savedImage = canteenImageRepository.save(existingImage);
        return convertToDto(savedImage);
    }

    /**
     * 根据图片ID删除食堂图片。
     * 同时会删除 COS 中的实际图片文件。
     *
     * @param imageId 要删除的图片ID
     * @throws ResourceNotFoundException 如果图片不存在
     */
    @Override
    @Transactional
    public void deleteCanteenImage(String imageId) {
        CanteenImage imageToDelete = canteenImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen image not found with ID: " + imageId));

        // 删除 COS 中的图片
        if (imageToDelete.getImageUrl() != null && !imageToDelete.getImageUrl().isEmpty()) {
            cosService.deleteFile(imageToDelete.getImageUrl());
        }

        canteenImageRepository.delete(imageToDelete);
    }

    /**
     * 辅助方法：将 CanteenImage 实体转换为 CanteenImageDto。
     *
     * @param canteenImage CanteenImage 实体
     * @return CanteenImageDto
     */
    private CanteenImageDto convertToDto(CanteenImage canteenImage) {
        return new CanteenImageDto(
                canteenImage.getImageId(),
                canteenImage.getCanteen().getCanteenId(),
                canteenImage.getImageUrl(),
                canteenImage.getDescription(),
                canteenImage.getUploadTime()
        );
    }
}