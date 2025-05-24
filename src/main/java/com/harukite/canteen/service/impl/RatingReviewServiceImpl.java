package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.RatingReviewDto;
import com.harukite.canteen.dto.RatingReviewRequest;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.InvalidInputException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Dish;
import com.harukite.canteen.model.RatingReview;
import com.harukite.canteen.model.User;
import com.harukite.canteen.repository.DishRepository;
import com.harukite.canteen.repository.RatingReviewRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.RatingReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评分与评论服务接口的实现类。
 * 包含用户对菜品进行评分和评论的业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class RatingReviewServiceImpl implements RatingReviewService
{

    private final RatingReviewRepository ratingReviewRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    /**
     * 创建新的评分和评论。
     *
     * @param request 包含评分和评论信息的请求 DTO
     * @param userId  评论用户ID
     * @return 创建成功的评分与评论响应 DTO
     * @throws ResourceNotFoundException 如果用户或菜品不存在
     * @throws DuplicateEntryException   如果用户已对该菜品进行过评论
     * @throws InvalidInputException     如果评分不在有效范围 (1-5)
     */
    @Override
    @Transactional
    public RatingReviewDto createRatingReview(RatingReviewRequest request, String userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Dish dish = dishRepository.findById(request.getDishId()) // 使用 request.getDishId()
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + request.getDishId()));

        // 检查用户是否已对该菜品评论过
        Optional<RatingReview> existingReview = ratingReviewRepository.findByUserAndDish(user, dish);
        if (existingReview.isPresent())
        {
            throw new DuplicateEntryException("User has already reviewed this dish.");
        }

        // 验证评分范围
        if (request.getRating() < 1 || request.getRating() > 5)
        { // 使用 request.getRating()
            throw new InvalidInputException("Rating must be between 1 and 5.");
        }

        RatingReview review = new RatingReview();
        review.setUser(user);
        review.setDish(dish);
        review.setRating(request.getRating()); // 使用 request.getRating()
        review.setComment(request.getComment()); // 使用 request.getComment()

        RatingReview savedReview = ratingReviewRepository.save(review);
        return convertToDto(savedReview);
    }

    /**
     * 根据评论ID获取评分与评论详情。
     *
     * @param reviewId 评论ID
     * @return 评分与评论响应 DTO
     * @throws ResourceNotFoundException 如果评论不存在
     */
    @Override
    @Transactional(readOnly = true)
    public RatingReviewDto getRatingReviewById(String reviewId)
    {
        RatingReview review = ratingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating review not found with ID: " + reviewId));
        return convertToDto(review);
    }

    /**
     * 获取所有评分与评论列表。
     *
     * @return 评分与评论响应 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<RatingReviewDto> getAllRatingReviews()
    {
        return ratingReviewRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据菜品ID获取其所有评分与评论。
     *
     * @param dishId 菜品ID
     * @return 评分与评论响应 DTO 列表
     * @throws ResourceNotFoundException 如果菜品不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<RatingReviewDto> getRatingReviewsByDishId(String dishId)
    {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + dishId));
        List<RatingReview> reviews = ratingReviewRepository.findByDish(dish);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取其所有评分与评论。
     *
     * @param userId 用户ID
     * @return 评分与评论响应 DTO 列表
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<RatingReviewDto> getRatingReviewsByUserId(String userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<RatingReview> reviews = ratingReviewRepository.findByUser(user);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新评分与评论。
     *
     * @param reviewId       要更新的评论ID
     * @param updatedRequest 包含更新信息的请求 DTO
     * @param userId         操作用户ID (用于权限检查，确保只有评论的创建者才能修改)
     * @return 更新后的评分与评论响应 DTO
     * @throws ResourceNotFoundException 如果评论不存在
     * @throws InvalidInputException     如果用户没有权限更新此评论或评分无效
     */
    @Override
    @Transactional
    public RatingReviewDto updateRatingReview(String reviewId, RatingReviewRequest updatedRequest, String userId)
    {
        RatingReview existingReview = ratingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating review not found with ID: " + reviewId));

        // 权限检查：只有评论的创建者才能修改
        if (!existingReview.getUser().getUserId().equals(userId))
        {
            throw new InvalidInputException("You are not authorized to update this review.");
        }

        // 验证评分范围
        if (updatedRequest.getRating() != null && (updatedRequest.getRating() < 1 || updatedRequest.getRating() > 5))
        {
            throw new InvalidInputException("Rating must be between 1 and 5.");
        }

        if (updatedRequest.getRating() != null)
        {
            existingReview.setRating(updatedRequest.getRating());
        }
        if (updatedRequest.getComment() != null)
        {
            existingReview.setComment(updatedRequest.getComment());
        }

        RatingReview savedReview = ratingReviewRepository.save(existingReview);
        return convertToDto(savedReview);
    }

    /**
     * 删除评分与评论。
     *
     * @param reviewId 要删除的评论ID
     * @param userId   操作用户ID (用于权限检查，确保只有评论的创建者或管理员才能删除)
     * @throws ResourceNotFoundException 如果评论不存在
     * @throws InvalidInputException     如果用户没有权限删除此评论
     */
    @Override
    @Transactional
    public void deleteRatingReview(String reviewId, String userId)
    {
        RatingReview review = ratingReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating review not found with ID: " + reviewId));

        // 权限检查：只有评论的创建者或管理员才能删除
        // 假设这里有获取用户角色并进行判断的逻辑，例如：
        // User currentUser = userRepository.findById(userId).orElseThrow(...);
        // if (!review.getUser().getUserId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
        if (!review.getUser().getUserId().equals(userId))
        { // 暂时简化为只有创建者可删
            throw new InvalidInputException("You are not authorized to delete this review.");
        }
        ratingReviewRepository.deleteById(reviewId);
    }

    /**
     * 辅助方法：将 RatingReview 实体转换为 RatingReviewDto。
     *
     * @param review RatingReview 实体
     * @return RatingReviewDto
     */
    private RatingReviewDto convertToDto(RatingReview review)
    {
        return new RatingReviewDto(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getUser().getUsername(),
                review.getDish().getDishId(),
                review.getDish().getName(),
                review.getRating(),
                review.getComment(),
                review.getReviewDate()
        );
    }
}