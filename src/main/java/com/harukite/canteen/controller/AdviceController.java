package com.harukite.canteen.controller;

import com.harukite.canteen.dto.AdviceRequest;
import com.harukite.canteen.service.AdviceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AdviceController {

    private final AdviceService adviceService;

    // 构造函数注入 AdviceService
    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }

    @PostMapping("/get-advice")
    public Mono<ResponseEntity<String>> getAdvice(@RequestBody AdviceRequest request, // 返回类型改回 ResponseEntity<String>
                                                  @RequestHeader(value = HttpHeaders.ORIGIN, required = false) String originHeader) {

        // 添加更多日志来调试请求来源和内容
        System.out.println("收到前端请求: " + request);
        if (originHeader != null) {
            System.out.println("请求来源 (Origin): " + originHeader);
        } else {
            System.out.println("请求没有 Origin 头。");
        }

        // 将业务逻辑委托给 AdviceService
        return adviceService.getDietaryAdvice(request)
                .map(ResponseEntity::ok) // 成功时将 String 包装成 200 OK 的 ResponseEntity
                .doOnNext(finalResponseEntity -> {
                    System.out.println("准备返回给前端的 ResponseEntity - 状态码: " + finalResponseEntity.getStatusCode());
                    System.out.println("准备返回给前端的 ResponseEntity - 响应体: " + finalResponseEntity.getBody());
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // DeepSeek API 返回 4xx/5xx 时，在此处捕获并返回对应的 ResponseEntity
                    System.err.println("WebClient 捕获到 DeepSeek API 响应错误，状态码: " + ex.getStatusCode() + ", 响应体: " + ex.getResponseBodyAsString());
                    return Mono.just(ResponseEntity.status(ex.getStatusCode())
                            .body("{\"error\": \"调用AI服务失败: " + ex.getResponseBodyAsString() + "\"}"));
                })
                .onErrorResume(IllegalStateException.class, ex -> {
                    // 捕获 API 密钥未配置的异常，返回 500
                    System.err.println("服务器内部错误（API密钥未配置）: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"API密钥未配置或为空\"}"));
                })
                .onErrorResume(Exception.class, ex -> {
                    // 捕获其他通用异常，返回 500 INTERNAL_SERVER_ERROR
                    System.err.println("服务器内部错误（捕获到通用异常）: " + ex.getMessage());
                    ex.printStackTrace(); // 打印完整堆栈信息
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"服务器内部错误: " + ex.getMessage() + "\"}"));
                });
    }
}
