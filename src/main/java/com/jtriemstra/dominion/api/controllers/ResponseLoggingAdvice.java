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

import com.jtriemstra.dominion.api.dto.PlayerGameState;

@ControllerAdvice
public class ResponseLoggingAdvice implements ResponseBodyAdvice<PlayerGameState> {
	
	@Autowired
	ILogService logService1;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getContainingClass() == MainController.class && returnType.getParameterType() == PlayerGameState.class;
	}

	@Override
	public PlayerGameState beforeBodyWrite(PlayerGameState body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest request1 = (ServletServerHttpRequest) request;
			logService1.logResponse(body, request1.getServletRequest().getRequestURI(), request1.getServletRequest().getParameter("card"), request1.getServletRequest().getParameterValues("options"));	
		}
		
		return body;
	}

}
