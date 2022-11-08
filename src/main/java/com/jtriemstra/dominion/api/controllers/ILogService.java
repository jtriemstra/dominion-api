package com.jtriemstra.dominion.api.controllers;

import com.jtriemstra.dominion.api.dto.PlayerGameState;

public interface ILogService {
	public void logResponse(String playerName, String action, String card, String[] options, String result, boolean isError);
	public void logResponse(PlayerGameState result, String action, String card, String[] options);
}
