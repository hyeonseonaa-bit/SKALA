package com.skala.basic.data;

import java.util.List;

import lombok.Data;

@Data
public class WorldResponse {
    private int result;
    private String message;
    private List<WorldEntity> worlds;
}
