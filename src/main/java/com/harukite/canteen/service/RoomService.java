package com.harukite.canteen.service;

import com.harukite.canteen.dto.RoomDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 宴会包厢服务接口。
 * 定义包厢的创建、查询、更新和删除操作。
 */
public interface RoomService
{

    /**
     * 创建新包厢。
     *
     * @param roomDto   包含包厢信息的 DTO
     * @param imageFile 包厢图片文件（可选）
     * @return 创建成功的包厢 DTO
     */
    RoomDto createRoom(RoomDto roomDto, MultipartFile imageFile);

    /**
     * 根据包厢ID获取包厢详情。
     *
     * @param roomId 包厢ID
     * @return 包厢 DTO
     */
    RoomDto getRoomById(String roomId);

    /**
     * 获取所有包厢列表。
     *
     * @return 包厢 DTO 列表
     */
    List<RoomDto> getAllRooms();

    /**
     * 根据食堂ID获取包厢列表。
     *
     * @param canteenId 食堂ID
     * @return 包厢 DTO 列表
     */
    List<RoomDto> getRoomsByCanteenId(String canteenId);

    /**
     * 更新包厢信息。
     *
     * @param roomId         要更新的包厢ID
     * @param updatedRoomDto 包含更新信息的包厢 DTO
     * @param imageFile      包厢图片文件（可选，如果提供则更新图片）
     * @return 更新后的包厢 DTO
     */
    RoomDto updateRoom(String roomId, RoomDto updatedRoomDto, MultipartFile imageFile);

    /**
     * 删除包厢。
     *
     * @param roomId 要删除的包厢ID
     */
    void deleteRoom(String roomId);
}

