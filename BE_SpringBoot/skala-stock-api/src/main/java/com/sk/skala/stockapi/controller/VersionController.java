package com.sk.skala.stockapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.data.common.Response;

@RestController
@RequestMapping("/api/version")
public class VersionController {

	@Value("${APP_VERSION:v1}")
	private String appVersion;

	@GetMapping
	public Response getVersion() {
		Response response = new Response();
		response.setResult(Response.SUCCESS);
		response.setBody(java.util.Map.of("version", appVersion));
		return response;
	}
}
