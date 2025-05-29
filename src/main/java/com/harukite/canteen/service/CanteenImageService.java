package com.harukite.canteen.service;

import com.harukite.canteen.dto.CanteenImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 食堂图片服务接口。
 * 定义食堂图片的创建、查询、更新和删除操作。
 */
public interface CanteenImageService {

    /**
     * 为指定食堂上传并创建新的图片记录。
     *
     * @param canteenId 食堂ID
     * @param description 图片描述（可选）
     * @param imageFile 图片文件
     * @return 创建成功的食堂图片 DTO
     */
    CanteenImageDto createCanteenImage(String canteenId, String description, MultipartFile imageFile);

    /**
     * 根据图片ID获取图片详情。
     *
     * @param imageId 图片ID
     * @return 图片 DTO
     */
    CanteenImageDto getCanteenImageById(String imageId);

    /**
     * 根据食堂ID获取所有关联的图片列表。
     *
     * @param canteenId 食堂ID
     * @return 图片 DTO 列表
     */
    List<CanteenImageDto> getCanteenImagesByCanteenId(String canteenId);

    /**
     * 更新食堂图片信息（例如描述）。
     * 不支持直接替换图片文件，如果需要替换，请删除旧的再上传新的。
     *
     * @param imageId 要更新的图片ID
     * @param updatedDescription 新的图片描述（如果为 null 则不更新）
     * @return 更新后的图片 DTO
     */
    CanteenImageDto updateCanteenImage(String imageId, String updatedDescription);

    /**
     * 根据图片ID删除食堂图片。
     * 同时会删除 COS 中的实际图片文件。
     *
     * @param imageId 要删除的图片ID
     */
    void deleteCanteenImage(String imageId);
}
