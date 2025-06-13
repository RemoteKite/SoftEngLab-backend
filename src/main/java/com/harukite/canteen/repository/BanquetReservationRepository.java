package com.harukite.canteen.repository;

import com.harukite.canteen.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 宴会预订数据访问接口。
 * 继承 JpaRepository，提供 BanquetReservation 实体的 CRUD 操作。
 */
@Repository
public interface BanquetReservationRepository extends JpaRepository<BanquetReservation, String>
{

    /**
     * 根据用户查找宴会预订列表。
     *
     * @param user 用户实体
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByUser(User user);

    /**
     * 根据包厢和日期查找宴会预订列表。
     * 用于检查包厢可用性。
     *
     * @param room      包厢实体
     * @param eventDate 宴会日期
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByRoomAndEventDate(Room room, LocalDate eventDate);

    /**
     * 根据食堂查找宴会预订列表。
     *
     * @param canteen 包厢实体
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByCanteen(Canteen canteen);
}