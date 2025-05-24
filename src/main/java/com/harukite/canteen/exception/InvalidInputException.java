package com.harukite.canteen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 自定义异常：无效输入。
 * 当输入的参数不符合业务规则或格式不正确时抛出（除了 JSR 380 验证）。
 * 映射到 HTTP 400 Bad Request 状态码。
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 映射到 HTTP 400
public class InvalidInputException extends RuntimeException
{

    public InvalidInputException(String message)
    {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
