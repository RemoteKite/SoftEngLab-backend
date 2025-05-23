package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 包厢数据访问接口。
 * 继承 JpaRepository，提供 Room 实体的 CRUD 操作。
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, String>
{

    /**
     * 根据所属食堂查找包厢列表。
     *
     * @param canteen 食堂实体
     * @return 包厢列表
     */
    List<Room> findByCanteen(Canteen canteen);

    /**
     * 根据包厢名称和所属食堂查找包厢。
     *
     * @param name    包厢名称
     * @param canteen 食堂实体
     * @return 包含包厢的 Optional 对象，如果未找到则为空
     */
    Optional<Room> findByNameAndCanteen(String name, Canteen canteen);

    /**
     * 根据食堂和容纳人数范围查找包厢。
     *
     * @param canteen     食堂实体
     * @param minCapacity 最小容纳人数
     * @param maxCapacity 最大容纳人数
     * @return 包厢列表
     */
    List<Room> findByCanteenAndCapacityBetween(Canteen canteen, Integer minCapacity, Integer maxCapacity);
}
