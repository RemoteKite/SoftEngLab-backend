package com.harukite.canteen.repository;

import com.harukite.canteen.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜品数据访问接口。
 * 继承 JpaRepository，提供 Dish 实体的 CRUD 操作。
 */
@Repository
public interface DishRepository extends JpaRepository<Dish, String>, JpaSpecificationExecutor<Dish>
{
    /**
     * 获取所有菜品。
     *
     * @return 菜品列表
     */
    @Query("SELECT d FROM Dish d LEFT JOIN FETCH d.dietaryTags dt LEFT JOIN FETCH d.allergens a")
    List<Dish> findAllWithDetails();
}

