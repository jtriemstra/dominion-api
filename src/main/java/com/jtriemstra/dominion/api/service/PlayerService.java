package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jtriemstra.dominion.api.dto.CardData;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;
import com.jtriemstra.dominion.api.dto.TurnState;
import com.jtriemstra.dominion.api.models.Card;

import lombok.Getter;

@Service
public class PlayerService {
	private Map<String, Executable> victoryFunctions = new HashMap();
	
	public PlayerService() {
		victoryFunctions.put(ActionService.ESTATE, (game, name) -> {
			getPlayer(game, name).setPoints(1 + getPlayer(game, name).getPoints());
		});
		victoryFunctions.put(ActionService.DUCHY, (game, name) -> {
			getPlayer(game, name).setPoints(3 + getPlayer(game, name).getPoints());
		});
		victoryFunctions.put(ActionService.PROVINCE, (game, name) -> {
			getPlayer(game, name).setPoints(6 + getPlayer(game, name).getPoints());
		});
		victoryFunctions.put(ActionService.CURSE, (game, name) -> {
			getPlayer(game, name).setPoints(-1 + getPlayer(game, name).getPoints());
		});
		victoryFunctions.put(ActionService.SILK_ROAD, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			int cards = (int) player.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isVictory()).count();
			cards += (int) player.getDeck().getCards().stream().filter(c -> CardData.cardInfo.get(c).isVictory()).count();
			cards += (int) player.getDiscard().getCards().stream().filter(c -> CardData.cardInfo.get(c).isVictory()).count();
			
			int points = cards / 4;
			player.setPoints(player.getPoints() + points);
		});
		victoryFunctions.put(ActionService.GARDENS, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			int cards = (int) player.getHand().getCards().stream().count();
			cards += (int) player.getDeck().getCards().stream().count();
			cards += (int) player.getDiscard().getCards().stream().count();
			
			int points = cards / 10;
			player.setPoints(player.getPoints() + points);
		});
		victoryFunctions.put(ActionService.FARMLAND, (game, name) -> {
			getPlayer(game, name).setPoints(2 + getPlayer(game, name).getPoints());
		});
		victoryFunctions.put(ActionService.TUNNEL, (game, name) -> {
			getPlayer(game, name).setPoints(2 + getPlayer(game, name).getPoints());
		});
	}
	
	public void defaultActionChange(TurnState turn, int number) {
		turn.setActionsAvailable(number + turn.getActionsAvailable());
	}
	
	public void startTurn(GameState game, String playerName) {
		PlayerState player = game.getPlayers().get(playerName);
		TurnState turn = player.getTurn();
		turn.setActionsAvailable(1);
		turn.setActive(true);
		turn.setBuys(1);
		turn.setTreasure(0);
		turn.getGainedToDiscard().clear();
		turn.getCostFunctions().clear();
		turn.getGainReactions().clear();
		player.getTurn().getGainedToDiscard().clear();
		turn.getRepeatedAction().clear();
		turn.setSkipActions(false);
	}

	public void calculatePoints(GameState game, String playerName) {
		PlayerState player = game.getPlayers().get(playerName);
		List<String> allCards = new ArrayList<>();
		allCards.addAll(player.getDeck().getCards());
		allCards.addAll(player.getHand().getCards());
		allCards.addAll(player.getPlayed().getCards());
		allCards.addAll(player.getDiscard().getCards());
		allCards.addAll(player.getAside().getCards());
		
		for(String s : allCards) {
			if (CardData.cardInfo.get(s).isVictory()) {
				victoryFunctions.get(s).execute(game, playerName);
			}
		}		
	}
	
	private PlayerState getPlayer(GameState game, String name) {
		return game.getPlayers().get(name);
	}
	
	public void skipActions(GameState game, String playerName) {
		PlayerState player = game.getPlayers().get(playerName);
		player.getTurn().setSkipActions(true);
	}
}
