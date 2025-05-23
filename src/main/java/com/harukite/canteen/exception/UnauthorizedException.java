package com.harukite.canteen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常：未授权。
 * 当用户没有权限执行某个操作时抛出。
 * 映射到 HTTP 401 Unauthorized 状态码。
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 映射到 HTTP 401
public class UnauthorizedException extends RuntimeException
{

    public UnauthorizedException(String message)
    {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
