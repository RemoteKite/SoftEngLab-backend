package com.harukite.canteen.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRole
{
    DINER("diner"),
    ADMIN("admin"),
    STAFF("staff");

    private final String value;

    UserRole(String value)
    {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的 UserRole 枚举。
     *
     * @param text 角色字符串值
     * @return 对应的 UserRole 枚举，如果未找到则抛出 IllegalArgumentException
     */
    public static UserRole fromString(String text)
    {
        for (UserRole b : UserRole.values())
        {
            if (b.value.equalsIgnoreCase(text))
            {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    /**
     * 获取枚举的字符串值。
     *
     * @return 角色的小写字符串表示
     * @JsonValue 注解告诉 Jackson 在将枚举序列化为 JSON 时使用此方法返回的值。
     * 例如：UserRole.STUDENT 序列化为 "student"。
     */
    @JsonValue
    public String getValue()
    {
        return value;
    }
}
