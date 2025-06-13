package com.harukite.canteen.repository;

import com.harukite.canteen.model.Order;
import com.harukite.canteen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}

