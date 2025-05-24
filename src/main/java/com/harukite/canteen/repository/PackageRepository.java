package com.harukite.canteen.repository;

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
public interface PackageRepository extends JpaRepository<Package, String>
{

    /**
     * 根据套餐名称查找套餐。
     *
     * @param name 套餐名称
     * @return 包含套餐的 Optional 对象，如果未找到则为空
     */
    Optional<Package> findByName(String name);

    /**
     * 根据价格范围查找套餐。
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 套餐列表
     */
    List<Package> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}