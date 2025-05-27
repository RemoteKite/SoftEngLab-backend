package com.harukite.canteen.service;

import com.harukite.canteen.dto.DailyMenuRequest;
import com.harukite.canteen.dto.DailyMenuResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日菜谱服务接口。
 * 定义菜谱的发布、查询和管理业务操作。
 */
public interface DailyMenuService
{

    /**
     * 发布每日菜谱。
     *
     * @param request     菜谱发布请求 DTO
     * @param publisherName 发布者用户名
     * @return 发布成功的菜谱响应 DTO
     */
    DailyMenuResponse publishDailyMenu(DailyMenuRequest request, String publisherName);

    /**
     * 根据食堂ID和日期获取每日菜谱列表。
     *
     * @param canteenId 食堂ID
     * @param menuDate  菜谱日期
     * @return 每日菜谱响应 DTO 列表
     */
    List<DailyMenuResponse> getDailyMenusByCanteenAndDate(String canteenId, LocalDate menuDate);

    /**
     * 根据菜谱ID获取菜谱详情。
     *
     * @param menuId 菜谱ID
     * @return 菜谱响应 DTO
     */
    DailyMenuResponse getDailyMenuById(String menuId);

    /**
     * 更新每日菜谱。
     *
     * @param menuId  要更新的菜谱ID
     * @param request 包含更新信息的菜谱请求 DTO
     * @return 更新后的菜谱响应 DTO
     */
    DailyMenuResponse updateDailyMenu(String menuId, DailyMenuRequest request);

    /**
     * 删除每日菜谱。
     *
     * @param menuId 要删除的菜谱ID
     */
    void deleteDailyMenu(String menuId);
}
