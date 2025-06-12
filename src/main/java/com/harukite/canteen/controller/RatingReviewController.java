package com.harukite.canteen.controller;

import com.harukite.canteen.dto.RatingReviewDto;
import com.harukite.canteen.dto.RatingReviewRequest;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.User;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.RatingReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理菜品评分与评论。
 * 提供评分与评论的创建、查询、更新和删除的 API 接口。
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class RatingReviewController {

    private final RatingReviewService ratingReviewService;
    private final UserRepository userRepository;

    /**
     * 创建新的评分和评论。
     * URL: POST /api/reviews
     *
     * @param request 包含评分和评论信息的请求 DTO
     * @return 创建成功的评分与评论响应 DTO
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingReviewDto> createRatingReview(@Valid @RequestBody RatingReviewRequest request) {
        // 从 Spring Security 认证上下文中获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID
        RatingReviewDto createdReview = ratingReviewService.createRatingReview(request, userId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    /**
     * 根据评论ID获取评分与评论详情。
     * URL: GET /api/reviews/{id}
     *
     * @param id 评论ID
     * @return 评分与评论响应 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<RatingReviewDto> getRatingReviewById(@PathVariable String id) {
        RatingReviewDto review = ratingReviewService.getRatingReviewById(id);
        return ResponseEntity.ok(review);
    }

    /**
     * 获取所有评分与评论列表。
     * URL: GET /api/reviews
     *
     *
     * @return 评分与评论响应 DTO 列表
     */
    @GetMapping
    public ResponseEntity<List<RatingReviewDto>> getAllRatingReviews() {
        List<RatingReviewDto> reviews = ratingReviewService.getAllRatingReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * 根据菜品ID获取其所有评分与评论。
     * URL: GET /api/reviews/dish/{dishId}
     *
     * @param dishId 菜品ID
     * @return 评分与评论响应 DTO 列表
     */
    @GetMapping("/dish/{dishId}")
    public ResponseEntity<List<RatingReviewDto>> getRatingReviewsByDishId(@PathVariable String dishId) {
        List<RatingReviewDto> reviews = ratingReviewService.getRatingReviewsByDishId(dishId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 根据用户ID获取其所有评分与评论。
     * URL: GET /api/reviews/user/{userId}
     *
     * @param userId 用户ID
     * @return 评分与评论响应 DTO 列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingReviewDto>> getRatingReviewsByUserId(@PathVariable String userId) {
        List<RatingReviewDto> reviews = ratingReviewService.getRatingReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 获取当前用户的所有评分与评论。
     * URL: GET /api/reviews/current-user
     *
     * @return 评分与评论响应 DTO 列表
     */
    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()") // 任何已认证用户都可以查看自己的评分与评论
    public ResponseEntity<List<RatingReviewDto>> getRatingReviewsByCurrentUser() {
        // 从 Spring Security 认证上下文中获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID
        List<RatingReviewDto> reviews = ratingReviewService.getRatingReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 更新评分与评论。
     * URL: PUT /api/reviews/{id}
     *
     * @param id 要更新的评论ID
     * @param request 包含更新信息的请求 DTO
     * @return 更新后的评分与评论响应 DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<RatingReviewDto> updateRatingReview(
            @PathVariable String id,
            @Valid @RequestBody RatingReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID
        RatingReviewDto updatedReview = ratingReviewService.updateRatingReview(id, request, userId);
        return ResponseEntity.ok(updatedReview);
    }

    /**
     * 删除评分与评论。
     * URL: DELETE /api/reviews/{id}
     *
     * @param id 要删除的评论ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatingReview(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID
        ratingReviewService.deleteRatingReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}
