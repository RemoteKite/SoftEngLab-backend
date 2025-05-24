package com.harukite.canteen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常：资源未找到。
 * 当请求的资源（如用户、菜品、食堂等）不存在时抛出。
 * 映射到 HTTP 404 Not Found 状态码。
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 映射到 HTTP 404
public class ResourceNotFoundException extends RuntimeException
{

    public ResourceNotFoundException(String message)
    {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
