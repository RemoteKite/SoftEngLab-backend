package com.harukite.canteen.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRole
{
    DINER("DINER"),
    ADMIN("ADMIN"),
    STAFF("STAFF");

    private final String value;

    UserRole(String value)
    {
        this.value = value;
    }


    /**
     * 获取枚举的字符串值。
     *
     * @return 角色的字符串表示
     */
    @JsonValue
    public String getValue()
    {
        return value;
    }
}
