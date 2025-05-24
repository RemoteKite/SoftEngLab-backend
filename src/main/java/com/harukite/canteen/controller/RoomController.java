package com.harukite.canteen.controller;

import com.harukite.canteen.dto.RoomDto;
import com.harukite.canteen.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST 控制器，用于管理宴会包厢。
 * 提供包厢的创建、查询、更新和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController
{

    private final RoomService roomService;

    /**
     * 创建新包厢。
     * URL: POST /api/rooms
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param roomDto   包含包厢信息的 DTO
     * @param imageFile 包厢图片文件（可选）
     * @return 创建成功的包厢 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建包厢
    public ResponseEntity<RoomDto> createRoom(
            @RequestPart("room") @Valid RoomDto roomDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    {
        RoomDto createdRoom = roomService.createRoom(roomDto, imageFile);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    /**
     * 根据包厢ID获取包厢详情。
     * URL: GET /api/rooms/{id}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param id 包厢ID
     * @return 包厢 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<RoomDto> getRoomById(@PathVariable String id)
    {
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    /**
     * 获取所有包厢列表。
     * URL: GET /api/rooms
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @return 包厢 DTO 列表
     */
    @GetMapping
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<RoomDto>> getAllRooms()
    {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 根据食堂ID获取包厢列表。
     * URL: GET /api/rooms/canteen/{canteenId}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param canteenId 食堂ID
     * @return 包厢 DTO 列表
     */
    @GetMapping("/canteen/{canteenId}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<RoomDto>> getRoomsByCanteenId(@PathVariable String canteenId)
    {
        List<RoomDto> rooms = roomService.getRoomsByCanteenId(canteenId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 更新包厢信息。
     * URL: PUT /api/rooms/{id}
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param id             要更新的包厢ID
     * @param updatedRoomDto 包含更新信息的 DTO
     * @param imageFile      包厢图片文件（可选，如果提供则更新图片）
     * @return 更新后的包厢 DTO
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新包厢
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable String id,
            @RequestPart("room") @Valid RoomDto updatedRoomDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    {
        RoomDto room = roomService.updateRoom(id, updatedRoomDto, imageFile);
        return ResponseEntity.ok(room);
    }

    /**
     * 删除包厢。
     * URL: DELETE /api/rooms/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要删除的包厢ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除包厢
    public ResponseEntity<Void> deleteRoom(@PathVariable String id)
    {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
