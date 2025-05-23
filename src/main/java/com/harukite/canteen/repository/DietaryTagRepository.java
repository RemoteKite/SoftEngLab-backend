package com.harukite.canteen.repository;

import com.harukite.canteen.model.DietaryTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 饮食标签数据访问接口。
 * 继承 JpaRepository，提供 DietaryTag 实体的 CRUD 操作。
 */
@Repository
public interface DietaryTagRepository extends JpaRepository<DietaryTag, String>
{

    /**
     * 根据饮食标签名称查找饮食标签。
     *
     * @param tagName 饮食标签名称
     * @return 包含饮食标签的 Optional 对象，如果未找到则为空
     */
    Optional<DietaryTag> findByTagName(String tagName);
}

