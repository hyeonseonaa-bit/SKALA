package com.skala.basic.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WorldRequest {
    @NotBlank(message = "이름은 공백이면 안됩니다")
    private String name;
    @NotBlank(message = "설명은 공백이면 안됩니다")
    private String description;
    @NotBlank(message = "타입은 공백이면 안됩니다")
    private String type;
    @Positive(message = "나이는 0보다 커야 합니다")
    private int age;

}
