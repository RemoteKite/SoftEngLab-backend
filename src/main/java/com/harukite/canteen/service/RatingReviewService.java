package com.harukite.canteen.service;

import com.harukite.canteen.dto.RatingReviewDto;
import com.harukite.canteen.dto.RatingReviewRequest;

import java.util.List;

/**
 * 评分与评论服务接口。
 * 定义用户对菜品进行评分和评论的业务操作。
 */
public interface RatingReviewService
{

    /**
     * 创建新的评分和评论。
     *
     * @param request 包含评分和评论信息的请求 DTO
     * @param userId  评论用户ID
     * @return 创建成功的评分与评论响应 DTO
     */
    RatingReviewDto createRatingReview(RatingReviewRequest request, String userId);

    /**
     * 根据评论ID获取评分与评论详情。
     *
     * @param reviewId 评论ID
     * @return 评分与评论响应 DTO
     */
    RatingReviewDto getRatingReviewById(String reviewId);

    /**
     * 获取所有评分与评论列表。
     *
     * @return 评分与评论响应 DTO 列表
     */
    List<RatingReviewDto> getAllRatingReviews();

    /**
     * 根据菜品ID获取其所有评分与评论。
     *
     * @param dishId 菜品ID
     * @return 评分与评论响应 DTO 列表
     */
    List<RatingReviewDto> getRatingReviewsByDishId(String dishId);

    /**
     * 根据用户ID获取其所有评分与评论。
     *
     * @param userId 用户ID
     * @return 评分与评论响应 DTO 列表
     */
    List<RatingReviewDto> getRatingReviewsByUserId(String userId);

    /**
     * 更新评分与评论。
     *
     * @param reviewId       要更新的评论ID
     * @param updatedRequest 包含更新信息的请求 DTO
     * @param userId         操作用户ID (用于权限检查)
     * @return 更新后的评分与评论响应 DTO
     */
    RatingReviewDto updateRatingReview(String reviewId, RatingReviewRequest updatedRequest, String userId);

    /**
     * 删除评分与评论。
     *
     * @param reviewId 要删除的评论ID
     * @param userId   操作用户ID (用于权限检查)
     */
    void deleteRatingReview(String reviewId, String userId);
}

