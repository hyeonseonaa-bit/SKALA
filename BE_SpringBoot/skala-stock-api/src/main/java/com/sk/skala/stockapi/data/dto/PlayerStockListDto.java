package com.sk.skala.stockapi.data.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DB → 객체 값 읽기
@Getter
// 객체 → DB 값 쓰기
@Setter

// @NoArgsConstructor: 빈 생성자 생성
@NoArgsConstructor

// @AllArgsConstructor: 모든 필드를 인자로 받는 생성자 생성 (@Builder 사용 시 필요)
@AllArgsConstructor

// @Builder: Builder 패턴을 자동 생성 (메서드 체이닝으로 객체 생성 가능)
@Builder
public class PlayerStockListDto {

	// 플레이어 ID
	private String playerId;

	// 플레이어 보유 자산
	private Double playerMoney;

	// 플레이어가 보유한 주식 목록
	private List<PlayerStockDto> stocks;
}
