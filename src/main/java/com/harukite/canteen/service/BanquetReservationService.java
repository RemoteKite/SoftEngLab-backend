package com.harukite.canteen.service;

import com.harukite.canteen.dto.BanquetReservationRequest;
import com.harukite.canteen.dto.BanquetReservationResponse;
import com.harukite.canteen.model.BanquetStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 宴会预订服务接口。
 * 定义宴会预订的创建、查询、更新和取消操作。
 */
public interface BanquetReservationService
{

    /**
     * 创建新的宴会预订。
     *
     * @param request 包含预订信息的 DTO
     * @param userName  预订用户名
     * @return 创建成功的宴会预订响应 DTO
     */
    BanquetReservationResponse createBanquetReservation(BanquetReservationRequest request, String userName);

    /**
     * 根据预订ID获取宴会预订详情。
     *
     * @param banquetId 宴会预订ID
     * @return 宴会预订响应 DTO
     */
    BanquetReservationResponse getBanquetReservationById(String banquetId);

    /**
     * 获取所有宴会预订列表。
     *
     * @return 宴会预订响应 DTO 列表
     */
    List<BanquetReservationResponse> getAllBanquetReservations();

    /**
     * 根据用户ID获取其所有宴会预订。
     *
     * @param userId 用户ID
     * @return 宴会预订响应 DTO 列表
     */
    List<BanquetReservationResponse> getBanquetReservationsByUserId(String userId);

    /**
     * 根据食堂ID获取宴会预订列表。
     *
     * @param canteenId 食堂ID
     * @return 宴会预订响应 DTO 列表
     */
    List<BanquetReservationResponse> getBanquetReservationsByCanteenId(String canteenId);

    /**
     * 更新宴会预订信息。
     *
     * @param banquetId      要更新的宴会预订ID
     * @param updatedRequest 包含更新信息的宴会预订请求 DTO
     * @return 更新后的宴会预订响应 DTO
     */
    BanquetReservationResponse updateBanquetReservation(String banquetId, BanquetReservationRequest updatedRequest);

    /**
     * 更新宴会预订状态。
     *
     * @param banquetId 宴会预订ID
     * @param newStatus 新的预订状态
     * @return 更新后的宴会预订响应 DTO
     */
    BanquetReservationResponse updateBanquetStatus(String banquetId, BanquetStatus newStatus);

    /**
     * 取消宴会预订。
     *
     * @param banquetId 宴会预订ID
     * @param userName    操作用户名 (用于权限检查)
     */
    void cancelBanquetReservation(String banquetId, String userName);

    /**
     * 检查某个包厢在指定日期和时间段是否可用。
     *
     * @param roomId             包厢ID
     * @param date               预订日期
     * @param time               预订开始时间
     * @param banquetIdToExclude 可选参数，在更新预订时排除当前预订ID
     * @return 如果包厢可用则为 true，否则为 false
     */
    boolean isRoomAvailable(String roomId, LocalDate date, LocalTime time, String banquetIdToExclude); // 修正：添加 banquetIdToExclude 参数
}
