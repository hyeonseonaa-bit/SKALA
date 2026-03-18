package com.sk.skala.stockapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;

// JpaRepository<PlayerStock, Long>: PlayerStock 엔터티를 관리하며, PK 타입은 Long
public interface PlayerStockRepository extends JpaRepository<PlayerStock, Long> {

	// findByPlayer_PlayerId는 Spring Data JPA의 중첩 속성 탐색 기능을 사용
	// player 필드를 통해 Player 엔터티의 playerId까지 접근하는 방식 (언더스코어로 연관 엔터티의 필드를 구분)
	// List<PlayerStock>: 한 플레이어가 여러 주식 보유 가능하므로 List로 반환
	// → SELECT * FROM player_stock WHERE player_id = ? 와 같다
	List<PlayerStock> findByPlayer_PlayerId(String playerId);

	// findByPlayerAndStock를 통해 player 객체와 Stock 객체를 인자로 받아 두 조건을 동시에 만족하는 레코드를 검색
	// 해당 주식을 보유하지 않을 수 있으므로 Optional로 안전하게 처리 
	// → SELECT * FROM player_stock WHERE player_id = ? AND stock_id = ? 와 같다 
	Optional<PlayerStock> findByPlayerAndStock(Player player, Stock stock);
}
