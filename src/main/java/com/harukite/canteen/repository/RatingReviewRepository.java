package com.harukite.canteen.repository;

import com.harukite.canteen.model.Dish;
import com.harukite.canteen.model.RatingReview;
import com.harukite.canteen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评价与反馈数据访问接口。
 * 继承 JpaRepository，提供 RatingReview 实体的 CRUD 操作。
 */
@Repository
public interface RatingReviewRepository extends JpaRepository<RatingReview, String>
{

    /**
     * 根据用户和菜品查找评价。
     * 用于检查用户是否已评价过某个菜品。
     *
     * @param user 用户实体
     * @param dish 菜品实体
     * @return 包含评价的 Optional 对象，如果未找到则为空
     */
    Optional<RatingReview> findByUserAndDish(User user, Dish dish);

    /**
     * 根据菜品查找所有评价。
     *
     * @param dish 菜品实体
     * @return 评价列表
     */
    List<RatingReview> findByDish(Dish dish);

    /**
     * 根据用户查找所有评价。
     *
     * @param user 用户实体
     * @return 评价列表
     */
    List<RatingReview> findByUser(User user);
}

