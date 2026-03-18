package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.PlayerStockDto;
import com.sk.skala.stockapi.data.dto.PlayerStockListDto;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;
import com.sk.skala.stockapi.repository.StockRepository;
import com.sk.skala.stockapi.tools.StringTool;

import lombok.RequiredArgsConstructor;

// @Service: 이 클래스가 Spring의 서비스 계층 빈임을 선언
@Service
// @RequiredArgsConstructor: final 필드를 인자로 받는 생성자를 Lombok이 자동 생성 (의존성 주입)
@RequiredArgsConstructor
public class PlayerService {

	private final StockRepository stockRepository;
	private final PlayerRepository playerRepository;
	private final PlayerStockRepository playerStockRepository;
	private final SessionHandler sessionHandler;

	// 전체 플레이어 목록 조회 (페이징)
	public Response getAllPlayers(int offset, int count) {
		// playerId 기준 오름차순 정렬로 Pageable 생성
		Pageable pageable = PageRequest.of(offset, count, Sort.by("playerId"));

		// 페이지 단위로 플레이어 목록 조회
		Page<Player> page = playerRepository.findAll(pageable);

		// 조회 결과를 PagedList에 가공
		PagedList pagedList = new PagedList();
		pagedList.setTotal(page.getTotalElements());    // 전체 플레이어 수
		pagedList.setCount(page.getNumberOfElements()); // 현재 페이지의 플레이어 수
		pagedList.setOffset(offset);                    // 현재 페이지 번호
		pagedList.setList(page.getContent());           // 현재 페이지의 플레이어 목록

		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	// 단일 플레이어 및 보유 주식 목록 조회
	// @Transactional(readOnly = true): 읽기 전용 트랜잭션 (성능 최적화, 데이터 변경 방지)
	@Transactional(readOnly = true)
	public Response getPlayerById(String playerId) {
		// Player 존재 여부 확인, 없으면 예외 발생
		Player player = playerRepository.findById(playerId)
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));

		// 플레이어가 보유한 PlayerStock 리스트 조회
		List<PlayerStock> playerStocks = playerStockRepository.findByPlayer_PlayerId(playerId);

		// Stream API로 PlayerStock → PlayerStockDto 변환
		List<PlayerStockDto> stockDtos = playerStocks.stream()
				.map(ps -> PlayerStockDto.builder()
						.stockId(ps.getStock().getId())
						.stockName(ps.getStock().getStockName())
						.stockPrice(ps.getStock().getStockPrice())
						.quantity(ps.getQuantity())
						.build())
				.collect(Collectors.toList());

		// PlayerStockListDto 빌더로 응답 DTO 구성
		PlayerStockListDto dto = PlayerStockListDto.builder()
				.playerId(player.getPlayerId())
				.playerMoney(player.getPlayerMoney())
				.stocks(stockDtos)
				.build();

		Response response = new Response();
		response.setBody(dto);
		return response;
	}

	// 플레이어 생성
	public Response createPlayer(Player player) {
		// 입력값 검증: playerId 또는 playerPassword가 비어있으면 예외 발생
		if (StringTool.isAnyEmpty(player.getPlayerId(), player.getPlayerPassword())) {
			throw new ParameterException("playerId", "playerPassword");
		}

		// 중복 아이디 체크: 이미 존재하는 ID이면 예외 발생
		playerRepository.findById(player.getPlayerId()).ifPresent(p -> {
			throw new ResponseException(Error.DATA_DUPLICATED);
		});

		// Player 객체 생성 및 초기 자산(10000.0) 세팅
		Player newPlayer = new Player(player.getPlayerId(), 10000.0);
		newPlayer.setPlayerPassword(player.getPlayerPassword());

		// 저장 후 Response 반환
		Response response = new Response();
		response.setBody(playerRepository.save(newPlayer));
		return response;
	}

	// 플레이어 로그인
	public Response loginPlayer(PlayerSession playerSession) {
		// 입력값 검증: playerId 또는 playerPassword가 비어있으면 예외 발생
		if (StringTool.isAnyEmpty(playerSession.getPlayerId(), playerSession.getPlayerPassword())) {
			throw new ParameterException("playerId", "playerPassword");
		}

		// ID로 플레이어 조회, 없으면 예외 발생
		Player player = playerRepository.findById(playerSession.getPlayerId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 비밀번호 검증: 불일치하면 예외 발생
		if (!player.getPlayerPassword().equals(playerSession.getPlayerPassword())) {
			throw new ResponseException(Error.NOT_AUTHENTICATED);
		}

		// 인증 성공 시 JWT 토큰 생성 및 쿠키 저장
		sessionHandler.storeAccessToken(playerSession);

		// 패스워드 null 처리 후 Response 반환
		player.setPlayerPassword(null);
		Response response = new Response();
		response.setBody(player);
		return response;
	}

	// 플레이어 정보 업데이트
	public Response updatePlayer(Player player) {
		// playerId와 playerMoney 유효성 체크
		if (StringTool.isEmpty(player.getPlayerId()) || player.getPlayerMoney() == null) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		// 존재 확인, 없으면 예외 발생
		Player existing = playerRepository.findById(player.getPlayerId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 자산 업데이트 후 저장
		existing.setPlayerMoney(player.getPlayerMoney());

		Response response = new Response();
		response.setBody(playerRepository.save(existing));
		return response;
	}

	// 플레이어 삭제
	public Response deletePlayer(Player player) {
		// playerId로 존재 확인, 없으면 예외 발생
		playerRepository.findById(player.getPlayerId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 삭제 후 Response 반환
		playerRepository.delete(player);
		return new Response();
	}

	// 주식 매수
	// @Transactional: 매수 처리 중 오류 발생 시 전체 롤백 보장
	@Transactional
	public Response buyPlayerStock(StockOrder order) {
		// 현재 로그인된 playerId 가져오기
		String playerId = sessionHandler.getPlayerId();

		// Player 조회, 없으면 예외 발생
		Player player = playerRepository.findById(playerId)
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// Stock 조회, 없으면 예외 발생
		Stock stock = stockRepository.findById(order.getStockId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 총 매수 금액 계산 (주식 가격 × 수량)
		double totalPrice = stock.getStockPrice() * order.getStockQuantity();

		// 잔액 충분성 체크, 부족하면 예외 발생
		if (player.getPlayerMoney() < totalPrice) {
			throw new ResponseException(Error.INSUFFICIENT_FUNDS);
		}

		// 잔액 차감
		player.setPlayerMoney(player.getPlayerMoney() - totalPrice);
		playerRepository.save(player);

		// 이미 보유한 주식이면 수량 추가, 없으면 신규 생성
		PlayerStock playerStock = playerStockRepository.findByPlayerAndStock(player, stock)
				.orElse(new PlayerStock(player, stock, 0));
		playerStock.setQuantity(playerStock.getQuantity() + order.getStockQuantity());
		playerStockRepository.save(playerStock);

		return new Response();
	}

	// 주식 매도
	// @Transactional: 매도 처리 중 오류 발생 시 전체 롤백 보장
	@Transactional
	public Response sellPlayerStock(StockOrder order) {
		// 현재 로그인된 playerId 가져오기
		String playerId = sessionHandler.getPlayerId();

		// Player 조회, 없으면 예외 발생
		Player player = playerRepository.findById(playerId)
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// Stock 조회, 없으면 예외 발생
		Stock stock = stockRepository.findById(order.getStockId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 보유한 PlayerStock 조회, 없으면 예외 발생
		PlayerStock playerStock = playerStockRepository.findByPlayerAndStock(player, stock)
				.orElseThrow(() -> new ResponseException(Error.INSUFFICIENT_QUANTITY));

		// 보유 수량 검증: 매도 수량이 보유 수량보다 많으면 예외 발생
		if (playerStock.getQuantity() < order.getStockQuantity()) {
			throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
		}

		// 수량 감소 또는 전량 매도 시 레코드 삭제
		if (playerStock.getQuantity() == order.getStockQuantity()) {
			playerStockRepository.delete(playerStock);
		} else {
			playerStock.setQuantity(playerStock.getQuantity() - order.getStockQuantity());
			playerStockRepository.save(playerStock);
		}

		// 매도 금액만큼 플레이어 자산 증가
		double totalPrice = stock.getStockPrice() * order.getStockQuantity();
		player.setPlayerMoney(player.getPlayerMoney() + totalPrice);
		playerRepository.save(player);

		return new Response();
	}
}
