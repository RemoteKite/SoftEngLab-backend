package com.harukite.canteen.controller;

import com.harukite.canteen.dto.DishDto;
import com.harukite.canteen.dto.DishFilterRequest;
import com.harukite.canteen.service.DishService;
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
 * REST 控制器，用于管理菜品。
 * 提供菜品的创建、查询、更新、删除和筛选的 API 接口。
 */
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController
{

    private final DishService dishService;

    /**
     * 创建新菜品。目前的实现允许菜品重名
     * URL: POST /api/dishes
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param dishDto   包含菜品信息的 JSON 字符串 (通过 @RequestPart 接收)
     * @param imageFile 菜品图片文件 (通过 @RequestPart 接收)
     * @return 创建成功的菜品 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能创建菜品
    public ResponseEntity<DishDto> createDish(
            @RequestPart("dish") @Valid DishDto dishDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    {
        DishDto createdDish = dishService.createDish(dishDto, imageFile);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);
    }

    /**
     * 根据菜品ID获取菜品详情。
     * URL: GET /api/dishes/{id}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param id 菜品ID
     * @return 菜品 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<DishDto> getDishById(@PathVariable String id)
    {
        DishDto dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    /**
     * 获取所有菜品列表。
     * URL: GET /api/dishes/all
     * (保留此接口以提供全局视图，但通常更推荐按食堂查询)
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @return 菜品 DTO 列表
     */
    @GetMapping("/all") // 将原有的 /api/dishes 路径改为 /api/dishes/all，以区分
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<DishDto>> getAllDishes()
    {
        List<DishDto> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    /**
     * 根据食堂ID获取菜品列表。
     * URL: GET /api/dishes/canteen/{canteenId}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param canteenId 食堂ID
     * @return 菜品 DTO 列表
     */
    @GetMapping("/canteen/{canteenId}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<DishDto>> getDishesByCanteenId(@PathVariable String canteenId)
    {
        DishFilterRequest filterRequest = new DishFilterRequest();
        filterRequest.setCanteenId(canteenId);
        List<DishDto> dishes = dishService.filterDishes(filterRequest);
        return ResponseEntity.ok(dishes);
    }

    /**
     * 更新菜品信息。
     * URL: PUT /api/dishes/{id}
     * Content-Type: multipart/form-data
     * (需要管理员或工作人员权限)
     *
     * @param id             要更新的菜品ID
     * @param updatedDishDto 包含更新信息的 JSON 字符串 (通过 @RequestPart 接收)
     * @param imageFile      菜品图片文件（可选，如果提供则更新图片）
     * @return 更新后的菜品 DTO
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新菜品
    public ResponseEntity<DishDto> updateDish(
            @PathVariable String id,
            @RequestPart("dish") @Valid DishDto updatedDishDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile)
    {
        DishDto dish = dishService.updateDish(id, updatedDishDto, imageFile);
        return ResponseEntity.ok(dish);
    }

    /**
     * 删除菜品。
     * URL: DELETE /api/dishes/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要删除的菜品ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除菜品
    public ResponseEntity<Void> deleteDish(@PathVariable String id)
    {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据筛选条件查找菜品。
     * URL: POST /api/dishes/filter
     * (对于复杂的筛选条件，使用 POST 请求体更符合 RESTful 实践，任何已认证用户或匿名用户都可以查看)
     *
     * @param filterRequest 菜品筛选请求 DTO (通过 @RequestBody 接收)
     * @return 符合条件的菜品 DTO 列表
     */
    @PostMapping("/filter")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<DishDto>> filterDishes(@RequestBody DishFilterRequest filterRequest)
    {
        List<DishDto> dishes = dishService.filterDishes(filterRequest);
        return ResponseEntity.ok(dishes);
    }
}
