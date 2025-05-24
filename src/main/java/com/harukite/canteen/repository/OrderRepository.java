package com.harukite.canteen.repository;

import com.harukite.canteen.model.Order;
import com.harukite.canteen.model.OrderStatus;
import com.harukite.canteen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单数据访问接口。
 * 继承 JpaRepository，提供 Order 实体的 CRUD 操作。
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String>
{

    /**
     * 根据用户查找订单列表。
     *
     * @param user 用户实体
     * @return 订单列表
     */
    List<Order> findByUser(User user);

    /**
     * 根据用户和订单状态查找订单列表。
     *
     * @param user   用户实体
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    /**
     * 根据用户和订单日期查找订单列表。
     *
     * @param user      用户实体
     * @param orderDate 订单日期
     * @return 订单列表
     */
    List<Order> findByUserAndOrderDate(User user, LocalDate orderDate);
}

