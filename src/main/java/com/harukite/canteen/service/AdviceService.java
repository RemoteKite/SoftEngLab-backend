package com.harukite.canteen.service;

import com.harukite.canteen.dto.AdviceRequest;
import reactor.core.publisher.Mono;

/**
 * AdviceService 接口定义了获取饮食建议的业务操作。
 * 它封装了与外部 AI 服务 (DeepSeek AI) 交互的逻辑。
 */
public interface AdviceService {

    /**
     * 根据提供的饮食请求生成并获取健康饮食建议。
     *
     * @param request 包含用户个人信息和饮食目标的请求数据。
     * @return 包含 DeepSeek AI 生成的 JSON 格式建议的 Mono<String>。
     * 如果 API 密钥未配置或调用失败，可能返回带有错误信息的 Mono。
     */
    Mono<String> getDietaryAdvice(AdviceRequest request);
}