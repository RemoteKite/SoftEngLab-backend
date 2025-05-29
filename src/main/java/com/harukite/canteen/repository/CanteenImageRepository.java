package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.CanteenImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 食堂图片数据访问接口。
 * 继承 JpaRepository，提供 CanteenImage 实体的 CRUD 操作。
 */
@Repository
public interface CanteenImageRepository extends JpaRepository<CanteenImage, String> {

    /**
     * 根据所属食堂查找图片列表。
     *
     * @param canteen 食堂实体
     * @return 食堂图片列表
     */
    List<CanteenImage> findByCanteen(Canteen canteen);
}
