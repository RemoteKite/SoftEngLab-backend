package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
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
     * 根据所属食堂查找菜品列表。
     *
     * @param canteen 食堂实体
     * @return 菜品列表
     */
    List<Dish> findByCanteen(Canteen canteen);

    /**
     * 根据菜品名称和所属食堂查找菜品。
     *
     * @param name    菜品名称
     * @param canteen 食堂实体
     * @return 包含菜品的 Optional 对象，如果未找到则为空
     */
    List<Dish> findByNameAndCanteen(String name, Canteen canteen);

    /**
     * 根据菜品是否可用查找菜品列表。
     *
     * @param isAvailable 菜品是否可用
     * @return 菜品列表
     */
    List<Dish> findByIsAvailable(Boolean isAvailable);

    /**
     * 获取所有菜品。
     *
     * @return 菜品列表
     */
    @Query("SELECT d FROM Dish d JOIN FETCH d.dietaryTags dt JOIN FETCH d.allergens a")
    List<Dish> findAllWithDetails();
}

