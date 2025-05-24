package com.harukite.canteen.repository;

import com.harukite.canteen.model.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 过敏原数据访问接口。
 * 继承 JpaRepository，提供 Allergen 实体的 CRUD 操作。
 */
@Repository
public interface AllergenRepository extends JpaRepository<Allergen, String>
{

    /**
     * 根据过敏原名称查找过敏原。
     *
     * @param allergenName 过敏原名称
     * @return 包含过敏原的 Optional 对象，如果未找到则为空
     */
    Optional<Allergen> findByAllergenName(String allergenName);
}

