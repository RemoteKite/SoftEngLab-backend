package com.harukite.canteen.controller;

import com.harukite.canteen.dto.DailyMenuRequest;
import com.harukite.canteen.dto.DailyMenuResponse;
import com.harukite.canteen.service.DailyMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST 控制器，用于管理每日菜谱。
 * 提供每日菜谱的发布、查询、更新和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class DailyMenuController
{

    private final DailyMenuService dailyMenuService;

    /**
     * 发布每日菜谱。
     * URL: POST /api/menu
     * (需要管理员或工作人员权限)
     *
     * @param request 菜谱发布请求 DTO
     * @return 发布成功的菜谱响应 DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能发布菜谱
    public ResponseEntity<DailyMenuResponse> publishDailyMenu(@Valid @RequestBody DailyMenuRequest request)
    {
        // 假设 publisherName 从认证上下文中获取
        String publisherName = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        DailyMenuResponse publishedMenu = dailyMenuService.publishDailyMenu(request, publisherName);
        return new ResponseEntity<>(publishedMenu, HttpStatus.CREATED);
    }

    /**
     * 根据食堂ID和日期获取每日菜谱列表。
     * URL: GET /api/menu/canteen/{canteenId}/date/{menuDate}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param canteenId 食堂ID
     * @param menuDate  菜谱日期 (格式:YYYY-MM-DD)
     * @return 每日菜谱响应 DTO 列表
     */
    @GetMapping("/canteen/{canteenId}/date/{menuDate}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<DailyMenuResponse>> getDailyMenusByCanteenAndDate(
            @PathVariable String canteenId,
            @PathVariable LocalDate menuDate)
    {
        List<DailyMenuResponse> dailyMenus = dailyMenuService.getDailyMenusByCanteenAndDate(canteenId, menuDate);
        return ResponseEntity.ok(dailyMenus);
    }

    /**
     * 根据菜谱ID获取菜谱详情。
     * URL: GET /api/menu/{id}
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param id 菜谱ID
     * @return 菜谱响应 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<DailyMenuResponse> getDailyMenuById(@PathVariable String id)
    {
        DailyMenuResponse dailyMenu = dailyMenuService.getDailyMenuById(id);
        return ResponseEntity.ok(dailyMenu);
    }

    /**
     * 更新每日菜谱。
     * URL: PUT /api/menu/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id      要更新的菜谱ID
     * @param request 包含更新信息的菜谱请求 DTO
     * @return 更新后的菜谱响应 DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新菜谱
    public ResponseEntity<DailyMenuResponse> updateDailyMenu(
            @PathVariable String id,
            @Valid @RequestBody DailyMenuRequest request)
    {
        DailyMenuResponse updatedMenu = dailyMenuService.updateDailyMenu(id, request);
        return ResponseEntity.ok(updatedMenu);
    }

    /**
     * 删除每日菜谱。
     * URL: DELETE /api/menu/{id}
     * (需要管理员或工作人员权限)
     *
     * @param id 要删除的菜谱ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能删除菜谱
    public ResponseEntity<Void> deleteDailyMenu(@PathVariable String id)
    {
        dailyMenuService.deleteDailyMenu(id);
        return ResponseEntity.noContent().build();
    }
}