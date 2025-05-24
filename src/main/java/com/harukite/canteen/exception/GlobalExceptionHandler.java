package com.harukite.canteen.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器。
 * 捕获应用程序中抛出的特定异常，并返回统一的错误响应。
 */
@ControllerAdvice // 标记这是一个全局异常处理器
public class GlobalExceptionHandler
{

    /**
     * 处理 ResourceNotFoundException 异常。
     * 映射到 HTTP 404 Not Found。
     *
     * @param ex      ResourceNotFoundException 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * 处理 DuplicateEntryException 异常。
     * 映射到 HTTP 409 Conflict。
     *
     * @param ex      DuplicateEntryException 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorDetails> handleDuplicateEntryException(DuplicateEntryException ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT.value() // 返回 409 Conflict
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * 处理 InvalidInputException 异常。
     * 映射到 HTTP 400 Bad Request。
     *
     * @param ex      InvalidInputException 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorDetails> handleInvalidInputException(InvalidInputException ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理 UnauthorizedException 异常。
     * 映射到 HTTP 401 Unauthorized。
     *
     * @param ex      UnauthorizedException 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedException(UnauthorizedException ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.UNAUTHORIZED.value()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理 JSR 380 (Bean Validation) 相关的 MethodArgumentNotValidException 异常。
     * 映射到 HTTP 400 Bad Request。
     *
     * @param ex      MethodArgumentNotValidException 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request)
    {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.value(),
                errors // 包含字段级别的错误信息
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理所有其他未捕获的异常。
     * 映射到 HTTP 500 Internal Server Error。
     *
     * @param ex      Exception 实例
     * @param request WebRequest 实例
     * @return 包含错误详情的 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request)
    {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "An unexpected error occurred: " + ex.getMessage(), // 生产环境不应暴露过多细节
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        // 生产环境建议记录详细堆栈信息到日志系统
        ex.printStackTrace();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 错误详情的内部类。
     * 用于统一错误响应的结构。
     */
    @Getter
    public static class ErrorDetails
    {
        // Getters (Lombok @Data 会自动生成，这里手动列出以示结构)
        private final LocalDateTime timestamp;
        private final String message;
        private final String details;
        private final int status;
        private Map<String, String> fieldErrors; // 用于验证错误

        public ErrorDetails(LocalDateTime timestamp, String message, String details, int status)
        {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.status = status;
        }

        public ErrorDetails(LocalDateTime timestamp, String message, String details, int status, Map<String, String> fieldErrors)
        {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
            this.status = status;
            this.fieldErrors = fieldErrors;
        }

    }
}
