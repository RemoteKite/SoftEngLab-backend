package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.RoomDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.Room;
import com.harukite.canteen.repository.CanteenRepository;
import com.harukite.canteen.repository.RoomRepository;
import com.harukite.canteen.service.CosService;
import com.harukite.canteen.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 宴会包厢服务接口的实现类。
 * 包含包厢的创建、查询、更新和删除业务逻辑。
 */
@Service
@RequiredArgsConstructor
@Slf4j // Lombok 注解，用于生成日志记录器
public class RoomServiceImpl implements RoomService
{

    private final RoomRepository roomRepository;
    private final CanteenRepository canteenRepository;
    private final CosService cosService; // 注入 CosService

    /**
     * 创建新包厢。
     *
     * @param roomDto   包含包厢信息的 DTO
     * @param imageFile 包厢图片文件（可选）
     * @return 创建成功的包厢 DTO
     * @throws ResourceNotFoundException 如果所属食堂不存在
     * @throws RuntimeException          如果图片上传失败
     */
    @Override
    @Transactional
    public RoomDto createRoom(RoomDto roomDto, MultipartFile imageFile)
    {
        Canteen canteen = canteenRepository.findById(roomDto.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + roomDto.getCanteenId()));

        Room room = new Room();
        room.setCanteen(canteen);
        getRoomsByCanteenId(canteen.getCanteenId()).stream()
                .filter(r -> r.getName().equals(roomDto.getName()))
                .findFirst()
                .ifPresent(r -> {
                    throw new DuplicateEntryException("Room with name '" + roomDto.getName() + "' already exists in this canteen.");
                });
        room.setName(roomDto.getName());
        room.setCapacity(roomDto.getCapacity());
        room.setDescription(roomDto.getDescription());
        room.setBaseFee(roomDto.getBaseFee());

        // 处理图片上传
        if (imageFile != null && !imageFile.isEmpty())
        {
            try
            {
                String imageUrl = cosService.uploadFile(imageFile, "rooms/"); // 上传到 rooms 文件夹
                room.setImageUrl(imageUrl);
            }
            catch (IOException e)
            {
                log.error("Failed to upload room image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload room image: " + e.getMessage(), e);
            }
        }
        else
        {
            room.setImageUrl(roomDto.getImageUrl()); // 如果没有新文件，使用DTO中可能已有的URL
        }

        Room savedRoom = roomRepository.save(room);
        return convertToDto(savedRoom);
    }

    /**
     * 根据包厢ID获取包厢详情。
     *
     * @param roomId 包厢ID
     * @return 包厢 DTO
     * @throws ResourceNotFoundException 如果包厢不存在
     */
    @Override
    @Transactional(readOnly = true)
    public RoomDto getRoomById(String roomId)
    {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));
        return convertToDto(room);
    }

    /**
     * 获取所有包厢列表。
     *
     * @return 包厢 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms()
    {
        return roomRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据食堂ID获取包厢列表。
     *
     * @param canteenId 食堂ID
     * @return 包厢 DTO 列表
     * @throws ResourceNotFoundException 如果食堂不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> getRoomsByCanteenId(String canteenId)
    {
        Canteen canteen = canteenRepository.findById(canteenId)
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + canteenId));
        List<Room> rooms = roomRepository.findByCanteen(canteen);
        return rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新包厢信息。
     *
     * @param roomId         要更新的包厢ID
     * @param updatedRoomDto 包含更新信息的包厢 DTO
     * @param imageFile      包厢图片文件（可选，如果提供则更新图片）
     * @return 更新后的包厢 DTO
     * @throws ResourceNotFoundException 如果包厢或所属食堂不存在
     * @throws RuntimeException          如果图片上传或删除失败
     */
    @Override
    @Transactional
    public RoomDto updateRoom(String roomId, RoomDto updatedRoomDto, MultipartFile imageFile)
    {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        // 更新所属食堂（如果提供了新的 canteenId）
        Canteen newCanteen;
        if (updatedRoomDto.getCanteenId() != null && !updatedRoomDto.getCanteenId().equals(existingRoom.getCanteen().getCanteenId()))
        {
            newCanteen = canteenRepository.findById(updatedRoomDto.getCanteenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + updatedRoomDto.getCanteenId()));
            existingRoom.setCanteen(newCanteen);
        }
        else
        {
            newCanteen = existingRoom.getCanteen(); // 保持原有食堂
        }

        if(updatedRoomDto.getName() != null && !updatedRoomDto.getName().equals(existingRoom.getName()))
        {
            // 检查更新后的名称是否在新的所属食堂内重复
            getRoomsByCanteenId(newCanteen.getCanteenId()).stream()
                    .filter(r -> r.getName().equals(updatedRoomDto.getName()) && !r.getRoomId().equals(roomId))
                    .findFirst()
                    .ifPresent(r -> {
                        throw new DuplicateEntryException("Room with name '" + updatedRoomDto.getName() + "' already exists in this canteen.");
                    });
        }


        // 更新基本信息
        if (updatedRoomDto.getName() != null)
        {
            existingRoom.setName(updatedRoomDto.getName());
        }
        if (updatedRoomDto.getCapacity() != null)
        {
            existingRoom.setCapacity(updatedRoomDto.getCapacity());
        }
        if (updatedRoomDto.getDescription() != null)
        {
            existingRoom.setDescription(updatedRoomDto.getDescription());
        }
        if (updatedRoomDto.getBaseFee() != null)
        {
            existingRoom.setBaseFee(updatedRoomDto.getBaseFee());
        }

        // 处理图片更新
        if (imageFile != null && !imageFile.isEmpty())
        {
            // 如果存在旧图片，先删除旧图片
            if (existingRoom.getImageUrl() != null && !existingRoom.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingRoom.getImageUrl());
            }
            try
            {
                String newImageUrl = cosService.uploadFile(imageFile, "rooms/");
                existingRoom.setImageUrl(newImageUrl);
            }
            catch (IOException e)
            {
                log.error("Failed to upload new room image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload new room image: " + e.getMessage(), e);
            }
        }
        else if (updatedRoomDto.getImageUrl() != null && updatedRoomDto.getImageUrl().isEmpty())
        {
            // 如果DTO中的imageUrl被显式设置为空字符串，表示清除图片
            if (existingRoom.getImageUrl() != null && !existingRoom.getImageUrl().isEmpty())
            {
                cosService.deleteFile(existingRoom.getImageUrl());
            }
            existingRoom.setImageUrl(null);
        }
        // 如果 imageFile 为 null 且 updatedRoomDto.getImageUrl() 也为 null，则保持不变

        Room savedRoom = roomRepository.save(existingRoom);
        return convertToDto(savedRoom);
    }

    /**
     * 删除包厢。
     * 同时删除 COS 中关联的图片。
     *
     * @param roomId 要删除的包厢ID
     * @throws ResourceNotFoundException 如果包厢不存在
     */
    @Override
    @Transactional
    public void deleteRoom(String roomId)
    {
        Room roomToDelete = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        // 删除 COS 中的图片
        if (roomToDelete.getImageUrl() != null && !roomToDelete.getImageUrl().isEmpty())
        {
            cosService.deleteFile(roomToDelete.getImageUrl());
        }

        roomRepository.delete(roomToDelete);
    }

    /**
     * 辅助方法：将 Room 实体转换为 RoomDto。
     *
     * @param room Room 实体
     * @return RoomDto
     */
    private RoomDto convertToDto(Room room)
    {
        return new RoomDto(
                room.getRoomId(),
                room.getCanteen().getCanteenId(),
                room.getCanteen().getName(),
                room.getName(),
                room.getCapacity(),
                room.getDescription(),
                room.getImageUrl(), // 使用数据库存储的 URL
                room.getBaseFee()
        );
    }
}
