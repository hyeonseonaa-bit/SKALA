package com.sk.skala.stockapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Stock;

// JpaRepository<Stock, Long>: Stock 엔터티를 관리하며, PK 타입은 Long
public interface StockRepository extends JpaRepository<Stock, Long> {

	// findByStockName를 통해 주식 이름으로 주식 데이터를 조회
	// Spring Data JPA가 메서드 이름을 분석하여 쿼리를 자동 생성해준다
	// → SELECT * FROM stock WHERE stock_name = ?
	// Optional: 해당 이름의 주식이 없을 수 있으므로 null 대신 Optional로 안전하게 처리함
	Optional<Stock> findByStockName(String stockName);
}
