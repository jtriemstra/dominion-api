package com.jtriemstra.dominion.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtriemstra.dominion.api.dto.GameState;

@Service
public class TurnService {
	@Autowired
	private ActionService actionService;
	
	
}
