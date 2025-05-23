package com.harukite.canteen.repository;

import com.harukite.canteen.model.Order;
import com.harukite.canteen.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单详情数据访问接口。
 * 继承 JpaRepository，提供 OrderItem 实体的 CRUD 操作。
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String>
{

    /**
     * 根据所属订单查找订单项列表。
     *
     * @param order 订单实体
     * @return 订单项列表
     */
    List<OrderItem> findByOrder(Order order);
}
