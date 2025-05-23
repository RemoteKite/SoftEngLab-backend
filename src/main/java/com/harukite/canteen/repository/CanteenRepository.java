package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 食堂数据访问接口。
 * 继承 JpaRepository，提供 Canteen 实体的 CRUD 操作。
 */
@Repository
public interface CanteenRepository extends JpaRepository<Canteen, String>
{

    /**
     * 根据食堂名称查找食堂。
     *
     * @param name 食堂名称
     * @return 包含食堂的 Optional 对象，如果未找到则为空
     */
    Optional<Canteen> findByName(String name);
}
