package com.sk.skala.stockapi.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlayerSession {
	// @NotBlank: null 또는 빈 문자열 불허
	@NotBlank
	private String playerId;

	@NotBlank
	private String playerPassword;
}
