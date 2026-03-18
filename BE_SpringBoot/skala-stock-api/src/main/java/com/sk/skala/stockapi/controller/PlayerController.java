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

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.service.PlayerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// @RestController: JSON 응답을 반환하는 REST API 컨트롤러
@RestController
// @RequestMapping: 이 컨트롤러의 기본 URL 경로를 /api/players로 지정
@RequestMapping("/api/players")
// @RequiredArgsConstructor: final 필드를 인자로 받는 생성자를 Lombok이 자동 생성 (의존성 주입)
@RequiredArgsConstructor
public class PlayerController {

	// PlayerService를 주입받아 비즈니스 로직 처리에 사용
	private final PlayerService playerService;

	// 전체 플레이어 목록 조회 API
	// GET /api/players/list?offset=0&count=10
	@GetMapping("/list")
	public Response getAllPlayers(
			@RequestParam(value = "offset", defaultValue = "0") int offset,   // 페이지 번호 (기본값 0)
			@RequestParam(value = "count", defaultValue = "10") int count) {  // 페이지당 개수 (기본값 10)
		return playerService.getAllPlayers(offset, count);
	}

	// 단일 플레이어 상세 조회 API
	// GET /api/players/{playerId}
	@GetMapping("/{playerId}")
	public Response getPlayerById(@PathVariable String playerId) { // URL 경로의 {playerId}를 파라미터로 받음
		return playerService.getPlayerById(playerId);
	}

	// 플레이어 등록 API
	// POST /api/players
	@PostMapping
	public Response createPlayer(@Valid @RequestBody Player player) { // @Valid: 요청 바디 검증
		return playerService.createPlayer(player);
	}

	// 플레이어 로그인 API
	// POST /api/players/login
	@PostMapping("/login")
	public Response loginPlayer(@Valid @RequestBody PlayerSession playerSession) { 
		return playerService.loginPlayer(playerSession);
	}

	// 플레이어 정보 수정 API
	// PUT /api/players
	@PutMapping
	public Response updatePlayer(@Valid @RequestBody Player player) { 
		return playerService.updatePlayer(player);
	}

	// 플레이어 삭제 API
	// DELETE /api/players
	@DeleteMapping
	public Response deletePlayer(@RequestBody Player player) { // 삭제할 플레이어 정보 (id) 전달
		return playerService.deletePlayer(player);
	}

	// 플레이어 주식 매수 API
	// POST /api/players/buy
	@PostMapping("/buy")
	public Response buyPlayerStock(@RequestBody StockOrder order) { // JSON 바디를 StockOrder(stockId, quantity 등)로 변환
		return playerService.buyPlayerStock(order);
	}

	// 플레이어 주식 매도 API
	// POST /api/players/sell
	@PostMapping("/sell")
	public Response sellPlayerStock(@RequestBody StockOrder order) { // JSON 바디를 StockOrder로 변환
		return playerService.sellPlayerStock(order);
	}
}
