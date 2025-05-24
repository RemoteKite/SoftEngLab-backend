package com.harukite.canteen.repository;

import com.harukite.canteen.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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
     * 根据食堂和日期查找宴会预订列表。
     *
     * @param canteen   食堂实体
     * @param eventDate 宴会日期
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByCanteenAndEventDate(Canteen canteen, LocalDate eventDate);

    /**
     * 根据用户和状态查找宴会预订列表。
     *
     * @param user   用户实体
     * @param status 宴会状态
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByUserAndStatus(User user, BanquetStatus status);

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
     * 根据包厢、日期、时间以及状态不等于指定状态的预订查找。
     * 用于更精确的包厢可用性检查，排除某些状态的预订。
     *
     * @param room      包厢实体
     * @param eventDate 宴会日期
     * @param eventTime 宴会时间
     * @param status    排除的状态
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByRoomAndEventDateAndEventTimeAndStatusNot(Room room, LocalDate eventDate, LocalTime eventTime, BanquetStatus status);

    /**
     * 根据食堂查找宴会预订列表。
     *
     * @param canteen 包厢实体
     * @return 宴会预订列表
     */
    List<BanquetReservation> findByCanteen(Canteen canteen);
}