package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
