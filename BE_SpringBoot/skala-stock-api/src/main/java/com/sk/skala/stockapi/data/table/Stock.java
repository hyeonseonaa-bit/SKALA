package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity: 이 클래스가 JPA 엔터티임을 선언. DB 테이블과 매핑됨
@Entity
// @Table: 테이블 이름 지정
@Table(name = "stock")

// DB → 객체 값 읽기
@Getter
// 객체 → DB 값 쓰기
@Setter

// @NoArgsConstructor: 빈 생성자 생성 (JPA가 객체 생성 시 빈 생성자가 필요하다)
@NoArgsConstructor
public class Stock {

	// @Id: Primary Key 지정
	@Id
	// @GeneratedValue: 기본키 자동 생성 전략 지정
	// GenerationType.IDENTITY를 사용하여 DB의 AUTO_INCREMENT를 사용해 순차적으로 자동 증가
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @NotBlank: null 또는 빈 문자열 불허
	@NotBlank
	private String stockName;

	// @NotNull: null 불허 / @Positive: 0보다 큰 값만 허용
	@NotNull
	@Positive
	private Double stockPrice;

	// stockName과 stockPrice를 인자로 받는 생성자
	public Stock(String stockName, Double stockPrice) {
		this.stockName = stockName;
		this.stockPrice = stockPrice;
	}
}

