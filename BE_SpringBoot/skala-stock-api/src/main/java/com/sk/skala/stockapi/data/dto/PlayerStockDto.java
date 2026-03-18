package com.sk.skala.stockapi.data.dto;

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
public class PlayerStockDto {

	// 주식 ID
	private Long stockId;

	// 주식 이름
	private String stockName;

	// 주식 가격
	private Double stockPrice;

	// 보유 주식 수량
	private Integer quantity;
}
