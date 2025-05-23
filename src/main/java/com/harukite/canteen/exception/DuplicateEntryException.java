package com.harukite.canteen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常：重复条目。
 * 当尝试创建已存在的唯一资源（如用户名、邮箱、食堂名称等）时抛出。
 * 映射到 HTTP 409 Conflict 状态码。
 */
@ResponseStatus(HttpStatus.CONFLICT) // 映射到 HTTP 409
public class DuplicateEntryException extends RuntimeException
{

    public DuplicateEntryException(String message)
    {
        super(message);
    }

    public DuplicateEntryException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
