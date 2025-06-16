package com.harukite.canteen.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

// 使用Lombok的@Data注解可以自动生成getter, setter, toString等方法
@Data
@NoArgsConstructor
public class AdviceRequest {
    private int age;
    private String gender;
    private double height;
    private double weight;
    private String activityLevel;
    private String dietaryGoal;
    private Macros macros;

    @Data
    @NoArgsConstructor
    public static class Macros {
        private double calories;
        private double proteinGrams;
        private double carbsGrams;
        private double fatGrams;
    }
}