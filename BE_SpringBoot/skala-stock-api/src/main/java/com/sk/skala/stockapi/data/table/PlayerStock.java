package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity: 이 클래스가 JPA 엔터티임을 선언. DB 테이블과 매핑됨
@Entity
// @Table: 테이블 이름 지정
@Table(name = "player_stock")

// DB → 객체 값 읽기
@Getter
// 객체 → DB 값 쓰기
@Setter

// @NoArgsConstructor: 빈 생성자 생성 (JPA가 객체 생성 시 빈 생성자가 필요하다)
@NoArgsConstructor
public class PlayerStock {

	// @Id: Primary Key 지정
	@Id
	// @GeneratedValue: 기본키 자동 생성 전략 지정
	// GenerationType.IDENTITY를 사용하여 DB의 AUTO_INCREMENT를 사용해 순차적으로 자동 증가
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @ManyToOne: 여러 PlayerStock이 하나의 Player에 속하는 다대일 관계
	// @JoinColumn: player 테이블의 PK(playerId)를 외래키로 참조
	@ManyToOne
	@JoinColumn(name = "player_id")
	private Player player;

	// @ManyToOne: 여러 PlayerStock이 하나의 Stock에 속하는 다대일 관계
	// @JoinColumn: stock 테이블의 PK(id)를 외래키로 참조
	@ManyToOne
	@JoinColumn(name = "stock_id")
	private Stock stock;

	// 보유 주식 수량
	private Integer quantity;

	// Player, Stock, 보유 주식 수를 인자로 받는 생성자
	public PlayerStock(Player player, Stock stock, Integer quantity) {
		this.player = player;
		this.stock = stock;
		this.quantity = quantity;
	}
}
