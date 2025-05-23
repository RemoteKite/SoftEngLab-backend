package com.harukite.canteen.repository;

import com.harukite.canteen.model.Allergen;
import com.harukite.canteen.model.DietaryTag;
import com.harukite.canteen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口。
 * 继承 JpaRepository，提供 User 实体的 CRUD 操作。
 * 第一个泛型参数是实体类型 (User)，第二个是主键类型 (String)。
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>
{

    /**
     * 根据用户名查找用户。
     * Spring Data JPA 会自动根据方法名生成查询。
     *
     * @param username 用户名
     * @return 包含用户的 Optional 对象，如果未找到则为空
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户。
     *
     * @param email 邮箱
     * @return 包含用户的 Optional 对象，如果未找到则为空
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据电话号码查找用户。
     *
     * @param phoneNumber 电话号码
     * @return 包含用户的 Optional 对象，如果未找到则为空
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * 查找包含指定饮食习惯标签的用户列表。
     *
     * @param dietaryTag 饮食习惯标签实体
     * @return 包含该标签的用户列表
     */
    List<User> findByDietaryTagsContaining(DietaryTag dietaryTag);
    
    /**
     * 查找包含指定过敏原的用户列表。
     *
     * @param allergen 过敏原实体
     * @return 包含该过敏原的用户列表
     */
    List<User> findByAllergensContaining(Allergen allergen);
}

