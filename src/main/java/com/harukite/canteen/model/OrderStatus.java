package com.harukite.canteen.model;

import lombok.Getter;

/**
 * 订单状态枚举，对应数据库中的 order_status ENUM 类型。
 * 包括待处理 (pending)、已确认 (confirmed)、已完成 (completed) 和已取消 (cancelled)。
 */
@Getter
public enum OrderStatus
{
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(String value)
    {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的 OrderStatus 枚举。
     *
     * @param text 状态字符串值
     * @return 对应的 OrderStatus 枚举，如果未找到则抛出 IllegalArgumentException
     */
    public static OrderStatus fromString(String text)
    {
        for (OrderStatus b : OrderStatus.values())
        {
            if (b.value.equalsIgnoreCase(text))
            {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
