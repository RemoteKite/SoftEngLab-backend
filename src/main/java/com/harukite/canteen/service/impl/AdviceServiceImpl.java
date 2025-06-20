package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.AdviceRequest;
import com.harukite.canteen.service.AdviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AdviceService 接口的实现类。
 * 负责构建 DeepSeek AI 的请求，发起 WebClient 调用，并处理响应。
 */
@Service
public class AdviceServiceImpl implements AdviceService {

    private final WebClient webClient;

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    // 构造函数注入 WebClient.Builder，配置 DeepSeek API 的基础 URL
    public AdviceServiceImpl(WebClient.Builder webClientBuilder) {
        String deepSeekApiUrl = "https://api.deepseek.com/v1/chat/completions";
        this.webClient = webClientBuilder
                .baseUrl(deepSeekApiUrl)
                .build();
    }

    /**
     * 根据提供的饮食请求生成并获取健康饮食建议。
     *
     * @param request 包含用户个人信息和饮食目标的请求数据。
     * @return 包含 DeepSeek AI 生成的 JSON 格式建议的 Mono<String>。
     * 如果 API 密钥未配置或调用失败，可能返回带有错误信息的 Mono。
     */
    @Override
    public Mono<String> getDietaryAdvice(AdviceRequest request) {
        // 验证 API 密钥是否已配置
        if (deepSeekApiKey == null || deepSeekApiKey.trim().isEmpty()) {
            System.err.println("错误：DeepSeek API 密钥未配置或为空。");
            return Mono.error(new IllegalStateException("API密钥未配置或为空")); // 返回一个包含异常的 Mono
        }

        String prompt = buildPrompt(request);

        // 构建 DeepSeek AI 请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", Collections.singletonList(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.85);
        requestBody.put("max_tokens", 500);
        requestBody.put("response_format", Map.of("type", "json_object"));

        System.out.println("准备调用 DeepSeek API，请求体: " + requestBody); // 打印 DeepSeek 请求体

        // 发起 WebClient 调用
        return this.webClient.post()
                .uri("") // DeepSeek API 的基础 URL 已经在 baseUrl 中设置，这里留空表示使用 baseUrl
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + deepSeekApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(deepSeekResponse -> System.out.println("成功收到 DeepSeek API 响应: " + deepSeekResponse)) // 添加成功日志
                .doOnError(throwable -> { // 添加通用错误日志
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) throwable;
                        System.err.println("DeepSeek API 调用失败，状态码: " + ex.getStatusCode() + "，响应体: " + ex.getResponseBodyAsString());
                    } else {
                        System.err.println("调用 DeepSeek API 时发生未知错误: " + throwable.getMessage());
                        throwable.printStackTrace(); // 打印完整堆栈信息
                    }
                });
        // 注意：WebClientResponseException 会自动转换为 WebClientResponseException。
        // 我们在 Controller 层统一处理ResponseEntity的包装和异常映射，保持Service层返回业务数据。
    }

    /**
     * 根据 AdviceRequest 构建 DeepSeek AI 的提示文本。
     *
     * @param formData 包含用户饮食目标和个人信息的请求数据。
     * @return 构建好的提示字符串。
     */
    private String buildPrompt(AdviceRequest formData) {
        Map<String, String> dietaryGoalTextMap = Map.of(
                "maintain", "保持健康",
                "lose_weight", "减轻体重",
                "gain_muscle", "增加肌肉"
        );
        Map<String, String> activityLevelTextMap = Map.of(
                "sedentary", "久坐（基本不运动）",
                "light", "轻度（少量运动/每周1-3天）",
                "moderate", "中度（中等强度运动/每周3-5天）",
                "active", "高度（高强度运动/每周6-7天）",
                "very_active", "极高（专业运动员水平）"
        );

        return String.format(
                "请根据以下个人信息和饮食目标，提供一份详细的中文健康饮食建议。\n" +
                        "个人信息：\n" +
                        "- 年龄: %d 岁\n" +
                        "- 性别: %s\n" +
                        "- 身高: %.0f cm\n" +
                        "- 体重: %.0f kg\n" +
                        "- 活动水平: %s\n" +
                        "- 饮食目标: %s\n" +
                        "- 每日目标总热量: %.0f 千卡\n" +
                        "- 每日目标蛋白质: %.0f 克\n" +
                        "- 每日目标碳水化合物: %.0f 克\n" +
                        "- 每日目标脂肪: %.0f 克\n\n" +
                        "请提供以下两部分内容，并确保内容健康、均衡、多样化，并具有可操作性：\n" +
                        "1. 膳食建议 (mealSuggestions): 针对早餐、午餐、晚餐和加餐，分别提供具体的食物建议。请给出一些实际的食物例子，而不仅仅是食物类别。\n" +
                        "2. 健康贴士 (healthyTips): 提供5条实用的健康饮食和生活习惯贴士。\n\n" +
                        "返回的膳食建议和健康贴士必须满足以下要求：\n" +
                        "1.尽量适合中国人的饮食习惯。\n" +
                        "2.返回内容生动有趣，对用户当前状况风趣地吐槽，禁止冒犯性语言。\n" +
                        "3.必须以用户健康为第一标准，当用户的当前状况与健康标准明显偏离时，要警告用户。\n" +
                        "4.应该更有创新性，不要八杯水这类内容。\n" +
                        "请以严格的JSON格式返回响应，格式如下：\n" +
                        "{\n" +
                        "  \"mealSuggestions\": {\n" +
                        "    \"breakfast\": \"早餐建议\",\n" +
                        "    \"lunch\": \"午餐建议\",\n" +
                        "    \"dinner\": \"晚餐建议\",\n" +
                        "    \"snacks\": \"加餐建议\"" +
                        "  },\n" +
                        "  \"healthyTips\": [\n" +
                        "    \"健康贴士1\",\n" +
                        "    \"健康贴士2\",\n" +
                        "    \"健康贴士3\",\n" +
                        "    \"健康贴士4\",\n" +
                        "    \"健康贴士5\"\n" +
                        "  ]\n" +
                        "}\n",
                formData.getAge(),
                "male".equals(formData.getGender()) ? "男" : "女",
                formData.getHeight(),
                formData.getWeight(),
                activityLevelTextMap.get(formData.getActivityLevel()),
                dietaryGoalTextMap.get(formData.getDietaryGoal()),
                formData.getMacros().getCalories(),
                formData.getMacros().getProteinGrams(),
                formData.getMacros().getCarbsGrams(),
                formData.getMacros().getFatGrams()
        );
    }
}
