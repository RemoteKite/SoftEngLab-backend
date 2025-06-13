package com.harukite.canteen.model;

import lombok.Getter;

/**
 * 宴会预订状态枚举，对应数据库中的 banquet_status ENUM 类型。
 * 包括待处理 (pending)、已确认 (confirmed)、已取消 (cancelled) 和已完成 (completed)。
 */
@Getter
public enum BanquetStatus
{
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String value;

    BanquetStatus(String value)
    {
        this.value = value;
    }

}
