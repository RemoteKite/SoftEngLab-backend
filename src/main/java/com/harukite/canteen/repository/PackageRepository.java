package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 宴会套餐数据访问接口。
 * 继承 JpaRepository，提供 Package 实体的 CRUD 操作。
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, String> {

    /**
     * 根据套餐名称和所属食堂查找套餐。
     * 用于检查套餐名称在特定食堂内是否唯一。
     *
     * @param name 套餐名称
     * @param canteen 所属食堂实体
     * @return 包含套餐的 Optional 对象，如果未找到则为空
     */
    Optional<Package> findByNameAndCanteen(String name, Canteen canteen);

    /**
     * 根据所属食堂查找套餐列表。
     *
     * @param canteen 食堂实体
     * @return 套餐列表
     */
    List<Package> findByCanteen(Canteen canteen);
}