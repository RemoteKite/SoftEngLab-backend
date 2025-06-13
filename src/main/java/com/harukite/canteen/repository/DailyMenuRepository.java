package com.harukite.canteen.repository;

import com.harukite.canteen.model.Canteen;
import com.harukite.canteen.model.DailyMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 每日菜谱数据访问接口。
 * 继承 JpaRepository，提供 DailyMenu 实体的 CRUD 操作。
 */
@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, String>
{

    /**
     * 根据食堂和日期查找每日菜谱。
     *
     * @param canteen  食堂实体
     * @param menuDate 菜谱日期
     * @return 每日菜谱列表
     */
    List<DailyMenu> findByCanteenAndMenuDate(Canteen canteen, LocalDate menuDate);

    /**
     * 根据食堂、日期、开始时间和结束时间查找每日菜谱。
     * 用于确保特定时间段内菜谱的唯一性。
     *
     * @param canteen   食堂实体
     * @param menuDate  菜谱日期
     * @param startTime 菜谱开始时间
     * @param endTime   菜谱结束时间
     * @return 包含每日菜谱的 Optional 对象，如果未找到则为空
     */
    Optional<DailyMenu> findByCanteenAndMenuDateAndStartTimeAndEndTime(Canteen canteen, LocalDate menuDate, LocalTime startTime, LocalTime endTime);

}
