package com.sk.skala.stockapi.data.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockOrder {
	private String playerId;

	// @Positive: 0보다 큰 값만 허용
	@Positive
	private long stockId;

	@Positive
	private int stockQuantity;
}
