package com.harukite.canteen.model;

import lombok.Getter;

/**
 * 宴会预订状态枚举，对应数据库中的 banquet_status ENUM 类型。
 * 包括待处理 (pending)、已确认 (confirmed)、已取消 (cancelled) 和已完成 (completed)。
 */
@Getter
public enum BanquetStatus
{
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    BanquetStatus(String value)
    {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的 BanquetStatus 枚举。
     *
     * @param text 状态字符串值
     * @return 对应的 BanquetStatus 枚举，如果未找到则抛出 IllegalArgumentException
     */
    public static BanquetStatus fromString(String text)
    {
        for (BanquetStatus b : BanquetStatus.values())
        {
            if (b.value.equalsIgnoreCase(text))
            {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
