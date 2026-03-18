package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.service.StockService;

import lombok.RequiredArgsConstructor;

// @RestController: JSON 응답을 반환하는 REST API 컨트롤러
@RestController
// @RequestMapping: 이 컨트롤러의 기본 URL 경로를 /api/stocks로 지정
@RequestMapping("/api/stocks")
// @RequiredArgsConstructor: final 필드를 인자로 받는 생성자를 Lombok이 자동 생성 (의존성 주입)
@RequiredArgsConstructor
public class StockController {

	// StockService를 주입받아 비즈니스 로직 처리에 사용
	private final StockService stockService;

	// 전체 주식 목록 조회 API
	// GET /api/stocks/list?offset=0&count=10
	@GetMapping("/list")
	public Response getAllStocks(
			@RequestParam(defaultValue = "0") Integer offset,  // 페이지 번호 (기본값 0)
			@RequestParam(defaultValue = "10") Integer count) { // 페이지당 개수 (기본값 10)
		return stockService.getAllStocks(offset, count);
	}

	// 개별 주식 상세 조회 API
	// GET /api/stocks/{id}
	@GetMapping("/{id}")
	public Response getStockById(@PathVariable Long id) { // URL 경로의 {id}를 파라미터로 받음
		return stockService.getStockById(id);
	}

	// 주식 등록 API
	// POST /api/stocks
	@PostMapping
	public Response createStock(@Valid @RequestBody Stock stock) { // @Valid: 요청 바디 검증
		return stockService.createStock(stock);
	}

	// 주식 정보 수정 API
	// PUT /api/stocks
	@PutMapping
	public Response updateStock(@Valid @RequestBody Stock stock) { // @Valid: 요청 바디 검증
		return stockService.updateStock(stock);
	}

	// 주식 삭제 API
	// DELETE /api/stocks
	@DeleteMapping
	public Response deleteStock(@RequestBody Stock stock) { // 삭제할 주식 정보 (id) 전달
		return stockService.deleteStock(stock);
	}
}
