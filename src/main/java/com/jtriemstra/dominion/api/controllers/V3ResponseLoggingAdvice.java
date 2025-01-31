package com.jtriemstra.dominion.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtriemstra.dominion.api.dto.PlayerGameState;
import com.jtriemstra.dominion.api.dto.V3GameState;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class V3ResponseLoggingAdvice implements ResponseBodyAdvice<V3GameState> {
	
	@Autowired
	ILogService logService1;
	
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getContainingClass() == V3Controller.class && returnType.getParameterType() == V3GameState.class;
	}

	@Override
	public V3GameState beforeBodyWrite(V3GameState body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest request1 = (ServletServerHttpRequest) request;
			String uri = request1.getServletRequest().getRequestURI();
			if (!"/v3/refresh".equals(uri)) {
				log.info(uri + "?" + request1.getServletRequest().getQueryString());
				try {
					log.info(objectMapper.writeValueAsString(body));
				} catch (Exception e) {
					log.error("Failed to log body", e);
				}
			}			
		}
		
		return body;
	}

}
