package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity: 이 클래스가 JPA 엔터티임을 선언. DB 테이블과 매핑됨
@Entity
// @Table: 테이블 이름 지정
@Table(name = "player")

// DB → 객체 값 읽기
@Getter
// 객체 → DB 값 쓰기
@Setter

// @NoArgsConstructor: 빈 생성자 생성 (JPA가 객체 생성 시 빈 생성자가 필요하다)
@NoArgsConstructor
public class Player {

	// @Id: Primary Key 지정 (문자열 형식의 playerId를 기본키로 사용)
	@Id
	// @NotBlank: null 또는 빈 문자열 불허
	@NotBlank
	private String playerId;

	// @NotBlank: null 또는 빈 문자열 불허
	@NotBlank
	private String playerPassword;

	// 플레이어 보유 자산
	private Double playerMoney;

	// playerId와 playerMoney를 인자로 받는 생성자
	public Player(String playerId, Double playerMoney) {
		this.playerId = playerId;
		this.playerMoney = playerMoney;
	}
}
