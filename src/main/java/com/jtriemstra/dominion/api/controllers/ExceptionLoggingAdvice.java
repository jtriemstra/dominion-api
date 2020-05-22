package com.jtriemstra.dominion.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionLoggingAdvice {
	@Autowired
	ILogService logService1;
	
	@ExceptionHandler({ RuntimeException.class})
	private void handleError(RuntimeException e, HttpServletRequest request) {
		
		String card = request.getParameter("card");
		String[] options = request.getParameterValues("options");
		logService1.logResponse(request.getParameter("playerName"), request.getServletPath(), card, options, "{\"error\":\"" + e.getMessage() + "\"}");
	}
}
