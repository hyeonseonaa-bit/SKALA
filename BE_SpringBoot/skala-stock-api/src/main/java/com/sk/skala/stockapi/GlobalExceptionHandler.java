package com.sk.skala.stockapi;

import java.util.stream.Collectors;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public @ResponseBody Response takeException(Exception e) {
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
		log.error("GlobalExceptionHandler.Exception: {}", e.getMessage());
		return response;
	}

	@ExceptionHandler(value = NullPointerException.class)
	public @ResponseBody Response takeNullPointerException(Exception e) {
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(), e.getMessage());
		log.error("GlobalExceptionHandler.NullPointerException: {}", e.getMessage());
		e.printStackTrace();
		return response;
	}

	@ExceptionHandler(value = SecurityException.class)
	public @ResponseBody Response takeSecurityException(SecurityException e) {
		Response response = new Response();
		response.setError(Error.NOT_AUTHENTICATED.getCode(), e.getMessage());
		log.error("GlobalExceptionHandler.SecurityException: {}", e.getMessage());
		return response;
	}

	// @Valid 검증 실패 시 발생하는 예외 처리
	// 검증 실패한 필드명을 추출해서 PARAMETER_MISSED 코드로 응답
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public @ResponseBody Response takeMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		String fields = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getField)
				.collect(Collectors.joining(", "));
		Response response = new Response();
		response.setError(Error.PARAMETER_MISSED.getCode(), Error.PARAMETER_MISSED.getMessage() + ": " + fields);
		return response;
	}

	@ExceptionHandler(value = ParameterException.class)
	public @ResponseBody Response takeParameterException(ParameterException e) {
		Response response = new Response();
		response.setError(e.getCode(), e.getMessage());
		return response;
	}

	@ExceptionHandler(value = ResponseException.class)
	public @ResponseBody Response takeResponseException(ResponseException e) {
		Response response = new Response();
		response.setError(e.getCode(), e.getMessage());
		return response;
	}
}