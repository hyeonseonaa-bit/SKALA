package com.sk.skala.stockapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.StockRepository;

import lombok.RequiredArgsConstructor;

// @Service: 이 클래스가 Spring의 서비스 계층 빈임을 선언
@Service
// @RequiredArgsConstructor: final 필드를 인자로 받는 생성자 생성 (의존성 주입)
@RequiredArgsConstructor
public class StockService {

	// StockRepository를 주입받아 DB 접근에 사용
	private final StockRepository stockRepository;

	// 전체 주식 목록 조회 (페이징)
	public Response getAllStocks(int offset, int count) {
		// offset(페이지 번호)과 count(페이지당 개수), id 기준 오름차순 정렬로 Pageable 생성
		Pageable pageable = PageRequest.of(offset, count, Sort.by("id"));

		// pageable 조건에 맞게 주식 목록을 페이지 단위로 조회
		Page<Stock> page = stockRepository.findAll(pageable);

		// 조회 결과를 PagedList에 가공
		PagedList pagedList = new PagedList();
		pagedList.setTotal(page.getTotalElements());    // 전체 주식 수
		pagedList.setCount(page.getNumberOfElements()); // 현재 페이지의 주식 수
		pagedList.setOffset(offset);                    // 현재 페이지 번호
		pagedList.setList(page.getContent());           // 현재 페이지의 주식 목록

		// Response에 담아 반환
		Response response = new Response();
		response.setBody(pagedList);
		return response;
	}

	// 개별 주식 상세 조회
	public Response getStockById(Long id) {
		// ID로 주식 조회, 존재하지 않으면 DATA_NOT_FOUND 예외 발생
		Stock stock = stockRepository.findById(id)
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// Response에 담아 반환
		Response response = new Response();
		response.setBody(stock);
		return response;
	}

	// 주식 등록 (생성)
	public Response createStock(Stock stock) {
		// 입력값 검증: stockName이 비어있거나 stockPrice가 0 이하이면 예외 발생
		if (stock.getStockName() == null || stock.getStockName().isEmpty() || stock.getStockPrice() <= 0) {
			throw new ParameterException("stockName", "stockPrice");
		}

		// 이름 중복 체크: 동일한 이름의 주식이 이미 존재하면 예외 발생
		stockRepository.findByStockName(stock.getStockName()).ifPresent(s -> {
			throw new ResponseException(Error.DATA_DUPLICATED);
		});

		// ID를 null로 세팅 → JPA가 INSERT로 인식하여 AUTO_INCREMENT로 자동 생성
		stock.setId(null);

		// 저장 후 Response 반환
		Response response = new Response();
		response.setBody(stockRepository.save(stock));
		return response;
	}

	// 주식 정보 수정
	public Response updateStock(Stock stock) {
		// 입력값 검증: stockName이 비어있거나 stockPrice가 0 이하이면 예외 발생
		if (stock.getStockName() == null || stock.getStockName().isEmpty() || stock.getStockPrice() <= 0) {
			throw new ParameterException("stockName", "stockPrice");
		}

		// 해당 ID의 주식이 존재하는지 확인, 없으면 예외 발생
		stockRepository.findById(stock.getId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 수정(저장) 후 Response 반환
		Response response = new Response();
		response.setBody(stockRepository.save(stock));
		return response;
	}

	// 주식 삭제
	public Response deleteStock(Stock stock) {
		// ID로 주식 조회, 존재하지 않으면 예외 발생
		stockRepository.findById(stock.getId())
				.orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

		// 주식 삭제
		stockRepository.delete(stock);

		// Response 반환
		return new Response();
	}
}
