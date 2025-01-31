package com.jtriemstra.dominion.api.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jtriemstra.dominion.api.dto.AttackState;
import com.jtriemstra.dominion.api.dto.CardData;
import com.jtriemstra.dominion.api.dto.CardDestination;
import com.jtriemstra.dominion.api.dto.CardSource;
import com.jtriemstra.dominion.api.dto.ChoiceOptionCreator;
import com.jtriemstra.dominion.api.dto.ChoiceState;
import com.jtriemstra.dominion.api.dto.DeckState;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;
import com.jtriemstra.dominion.api.dto.TurnState;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;


@Slf4j
@Service
public class ActionService {
	private Map<String, Executable> actions = new HashMap<>();
	private PlayerService playerService;
	private NotificationService notificationService;
	private Map<String, DiscardEvent> discardEvents = new HashMap<>();
	private Map<String, Executable> reactions = new HashMap<>();
	private Map<String, Executable> gainEvents = new HashMap<>();
	private Map<String, CostFunction> costFunctions = new HashMap<>();
	private Map<String, Executable> trashEvents = new HashMap<>();
	private Map<String, Executable> buyEvents = new HashMap<>();
	private Map<String, CardDestinationFunction> gainDestinationFunctions = new HashMap<>();
	private Map<CardSources, CardSourceFunction> cardSourceFunctions = new HashMap<>();
	private Map<String, GainReaction> gainReactions = new HashMap<>();
	
	public enum CardSources {
		HAND,
		ASIDE
	}

		
	public ActionService(PlayerService playerService, NotificationService notificationService) {
		this.playerService = playerService;
		this.notificationService = notificationService;
	}
	
	public void init() {
		gainDestinationFunctions.put("HAND", (g, p) -> getPlayer(g, p).getHand());
		gainDestinationFunctions.put("DECK", (g, p) -> getPlayer(g, p).getDeck());
		
		cardSourceFunctions.put(CardSources.HAND, (g, p) -> getPlayer(g, p).getHand());
		cardSourceFunctions.put(CardSources.ASIDE, (g, p) -> getPlayer(g, p).getAside());		
		
		actions.put(GOLD, (game, name) -> {addTreasure(getPlayer(game, name).getTurn(), 3);});
		actions.put(SILVER, (game, name) -> {addTreasure(getPlayer(game, name).getTurn(), 2);});
		actions.put(COPPER, (game, name) -> {addTreasure(getPlayer(game, name).getTurn(), 1);});
		
		actions.put(SMITHY, (game, name) -> {for (int i=1; i<=3; i++) {defaultDraw(game, name);}});
		actions.put(VILLAGE, (game, name) -> {defaultDraw(game, name); playerService.defaultActionChange(getPlayer(game, name).getTurn(), 2);});
		actions.put(FESTIVAL, (game, name) -> {playerService.defaultActionChange(getPlayer(game, name).getTurn(), 2); addTreasure(getPlayer(game, name).getTurn(), 2); getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());});
		actions.put(WOODCUTTER, (game, name) -> {addTreasure(getPlayer(game, name).getTurn(), 2); getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());});
		actions.put(LABORATORY, (game, name) -> {defaultDraw(game, name); defaultDraw(game, name); playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);});
		actions.put(MARKET, (game, name) -> {defaultDraw(game, name); playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1); addTreasure(getPlayer(game, name).getTurn(), 1); getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());});
		
		actions.put(REMODEL, (game, name) -> {
			PlayerState player = getPlayer(game, name); 
			createChoice(game, player, (g, p) -> chooseFromHand(p), 1, REMODEL1, "Choose a card to trash");			
		});
		actions.put(REMODEL1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String trashedCardName = player.getTurn().getChoicesMade().get(0);
				int trashedCost = getCost(game, name, trashedCardName);
				doTrash(game, name, trashedCardName, player.getHand());
				ChoiceOptionCreator cheaperFromBank = (g, p) -> {
					return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= 2 + trashedCost).collect(Collectors.toList());
				};
				createChoice(game, player, cheaperFromBank, 1, REMODEL2, "Choose a card to gain");	
			}
		});
		actions.put(REMODEL2, (game, name) -> {
			String gainedCardName = getPlayer(game, name).getTurn().getChoicesMade().get(0);
			doGain(game, name, gainedCardName);		
		});
		
		actions.put(MILITIA, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2);
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, MILITIA2, name);
			});
		});
		actions.put(MILITIA1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				List<String> cardNames = player.getTurn().getChoicesMade();
				cardNames.stream().forEach(n -> {
					if (!"".equals(n)) {
						discard(game, name, player.getHand(), n);
					}
				});
			}
			finishAttack(game, name);
		});
		actions.put(MILITIA2, (game, targetName) -> {
			int handSize = getPlayer(game, targetName).getHand().size();

			int discardSize = handSize - 3;
			if (discardSize > 0) {
				createChoice(game, getPlayer(game, targetName), (g, p) -> chooseFromHand(p), discardSize, MILITIA1, "Choose " + discardSize + " cards to discard");
			} else {
				// TODO: maybe need to switch this to the version with choices, but I think I solved that with queuing attacks
				finishAttack(game, targetName);
			}
		});
		
		actions.put(BUREAUCRAT, (game, name) -> {
			// TODO: probably should be wrapped in a "Gain" to trigger Trader, even though it doesn't matter

			if (game.getBank().getSupplies().get("Silver").getCount() > 0) {
				moveCard(game.getBank().getSupplies().get("Silver"), getPlayer(game, name).getDeck());
			}
			List<String> otherNames = getOtherPlayers(game, name);

			otherNames.stream().forEach(other -> {
				startAttack(game, other, BUREAUCRAT2, name);
			});
		});
		actions.put(BUREAUCRAT1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String cardName = player.getTurn().getChoicesMade().get(0);
			moveCard(player.getHand(), player.getDeck(), cardName);
			finishAttack(game, name);
		});
		actions.put(BUREAUCRAT2, (game, targetName) -> {
			PlayerState otherPlayer = getPlayer(game, targetName);
			if (otherPlayer.getHand().hasVictory()) {
				ChoiceOptionCreator victoryCardFromHand = (g, p) -> {
					return p.getHand().getCards().stream().filter(n -> CardData.cardInfo.get(n).isVictory()).toList();
				};
				createChoice(game, otherPlayer, victoryCardFromHand, 1, BUREAUCRAT1, "Choose a victory card to put on your deck");
			} else {
				// TODO: reveal, maybe need to switch this to the version with choices, but I think I solved that with queuing attacks
				finishAttack(game, targetName);
			}
		});
		
		actions.put(VASSAL, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(getPlayer(game, name).getTurn(), 2);
			deckPeek(game, name);
			if (CardData.cardInfo.get(player.getLooking().getCards().get(0)).isAction()) {
				createChoice(game, player, (g,p) -> {return List.of("DISCARD","PLAY");}, 1, VASSAL1, "Do you want to play or discard the top card from your deck?");
			} else {
				createChoice(game, player, (g,p) -> {return List.of("DISCARD");}, 1, VASSAL1, "The top card of your deck is not an action; discard it");
			}
			
		});
		actions.put(VASSAL1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("PLAY")) {
				String cardName = player.getLooking().getCards().get(0);
				moveCard(player.getLooking(), player.getHand());
				defaultPlay(game, name, cardName, CardSources.HAND);
			} else if (choice.equals("DISCARD")) {				
				discard(game, name, player.getLooking(), player.getLooking().getCards().get(0));
			}
		});
		
		actions.put(WEAVER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			ChoiceOptionCreator cheaperFromBank = (g, p) -> {
				List<String> result = getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= 4).collect(Collectors.toList());
				result.add("2 Silver");
				return result;
			};
			createChoice(game, player, cheaperFromBank, 1, WEAVER2, "Either choose a card costing up to 4, or 2 Silver");
		});
		actions.put(WEAVER2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("2 Silver")) {
				doGain(game, name, "Silver");
				doGain(game, name, "Silver");
			} else {
				doGain(game, name, choice);
			}
		});
		discardEvents.put(WEAVER, (game, name, source) -> {
			PlayerState player = getPlayer(game, name);
			if (!player.getTurn().isCleanup()) {
				// TODO: how can this use the source?
				createChoice(game, player, (g,p) -> {return List.of("DISCARD","PLAY");}, 1, WEAVER1, "Do you want to play the weaver instead of discarding?");
			} else {
				// TODO: this could be in a played state, not hand
				defaultDiscard(game, name, player.getHand(), ActionService.WEAVER);
			}
		});
		actions.put(WEAVER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("PLAY")) {
				defaultPlay(game, name, ActionService.WEAVER, CardSources.HAND);
				moveCard(player.getPlayed(), player.getDiscard(), ActionService.WEAVER);
			} else if (choice.equals("DISCARD")) {				
				defaultDiscard(game, name, player.getHand(), ActionService.WEAVER);
			}
		});
		
		actions.put(SCHEME, (game, name) -> {
			defaultDraw(game, name);
			TurnState turn = getPlayer(game, name).getTurn();
			playerService.defaultActionChange(turn, 1);
			turn.getCleanupActions().add(SCHEME1);
		});
		actions.put(SCHEME1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			ChoiceOptionCreator playedActions = (g, p) -> {
				return p.getPlayed().getCards().stream().filter(c -> CardData.cardInfo.get(c).isAction()).toList();
			};
			createChoice(game, player, playedActions, 1, SCHEME2, "Do you want to put an action card on top of your deck?");
		});
		actions.put(SCHEME2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					moveCard(player.getPlayed(), player.getDeck(), choice);
				}
			}
			player.getTurn().getCleanupActions().remove(SCHEME1);
			cleanup(game, name);
		});
		
		actions.put(HAGGLER, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2);
			TurnState turn = getPlayer(game, name).getTurn();
			turn.getBuyActions().add(HAGGLER1);
		});
		actions.put(HAGGLER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String buyingCard = player.getTurn().getBuying();
			int cost = getCost(game, name, buyingCard);
			if (getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) < cost && !CardData.cardInfo.get(c).isVictory()).count() > 0) {
				// TODO: newer version changed the rules, check wiki
				ChoiceOptionCreator cheaperFromBank = (g, p) -> {
					List<String> result = getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) < cost && !CardData.cardInfo.get(c).isVictory()).collect(Collectors.toList());
					return result;
				};
				createChoice(game, player, cheaperFromBank, 1, HAGGLER2, "Choose an extra card to gain");
			}
		});
		actions.put(HAGGLER2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String buyingCard = player.getTurn().getBuying();
			String gainedCardName = getPlayer(game, name).getTurn().getChoicesMade().get(0);
			doGain(game, name, gainedCardName);	
			defaultBuy(game, name, buyingCard);
		});
		
		actions.put(ATTACK_REACTION, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String reaction = player.getTurn().getChoicesMade().get(0);
				if (reaction.equals("No")) {
					actions.get(player.getAttacks().peek().getAttack()).execute(game, name);				
				} else {
					reactions.get(reaction).execute(game, name);
				}
			}
		});
		
		actions.put(MOAT, (game, name) -> {defaultDraw(game, name); defaultDraw(game, name); });
		
		reactions.put(MOAT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			finishAttack(game, name);
			// TODO: should Moat then Guard Dog be a valid combo? https://wiki.dominionstrategy.com/index.php/Attack_immunity says yes
		});
		
		actions.put(GUARD_DOG, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			for (int i=1; i<=2; i++) {defaultDraw(game, name);}
			if (player.getHand().size() <= 5) {
				for (int i=1; i<=2; i++) {defaultDraw(game, name);}
			}
		});
		reactions.put(GUARD_DOG, (game, name) -> {			
			defaultPlay(game, name, GUARD_DOG, CardSources.HAND);
			PlayerState player = getPlayer(game, name);
			processAttack(game, name, player.getAttacks().peek().getAttack(), player.getAttacks().peek().getAttacker(), false);
		});
		
		actions.put(OASIS, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
			addTreasure(player.getTurn(), 1);
			if (player.getHand().size() > 0) {
				createChoice(game, player, (g, p) -> chooseFromHand(p), 1, OASIS1, "Choose a card to discard");
			}
		});
		actions.put(OASIS1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String discardChoice = player.getTurn().getChoicesMade().get(0);
			discard(game, name, player.getHand(), discardChoice);
		});
		
		actions.put(EMBASSY, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			for (int i=0; i<5; i++) {defaultDraw(game, name);}
			createChoice(game, player, (g, p) -> chooseFromHand(p), 3, EMBASSY1, "Choose 3 cards to discard");
		});
		actions.put(EMBASSY1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			for (int i=0; i<3; i++) {
				String discardChoice = player.getTurn().getChoicesMade().get(i);
				discard(game, name, player.getHand(), discardChoice);
			}
		});
		gainEvents.put(EMBASSY, (game, name) -> {
			defaultGain(game, name, ActionService.EMBASSY);
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				doGain(game, other, ActionService.SILVER);
			});			
		});
		
		actions.put(BORDER_VILLAGE, (game, name) -> {
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 2);
		});
		gainEvents.put(BORDER_VILLAGE, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			int thisCost = getCost(game, name, BORDER_VILLAGE);
			ChoiceOptionCreator cheaperFromBank = (g, p) -> {
				List<String> result = getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) < thisCost ).collect(Collectors.toList());
				return result;
			};
			createChoice(game, player, cheaperFromBank, 1, BORDER_VILLAGE2, "Choose an extra card to gain");
			defaultGain(game, name, BORDER_VILLAGE);
		});
		actions.put(BORDER_VILLAGE2, (game, name) -> {
			String gainedCardName = getPlayer(game, name).getTurn().getChoicesMade().get(0);
			doGain(game, name, gainedCardName);	
		});
		
		actions.put(THRONE_ROOM, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isAction()).count() > 0) {
				ChoiceOptionCreator actionsInHand = (g, p) -> {
					return p.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isAction()).collect(Collectors.toList());
				};
				createChoice(game, player, actionsInHand, 1, THRONE_ROOM2, "Choose an action card to play twice");
			}
		});
		actions.put(THRONE_ROOM2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String action = player.getTurn().getChoicesMade().get(0);
			player.getTurn().pushRepeatedAction(action);
			defaultPlay(game, name, action, CardSources.HAND);
		});
		
		actions.put(HIGHWAY, (game, name) -> {
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
			getPlayer(game, name).getTurn().getCostFunctions().add(HIGHWAY);
		});
		costFunctions.put(HIGHWAY, c -> c > 0 ? c-1 : c);
		
		// TODO: technically, something should block you from using the card/action if you're in the buy phase. maybe UI is enough
		actions.put(TRAIL, (game, name) -> {
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
		});
		discardEvents.put(TRAIL, (game, name, source) -> {
			PlayerState player = getPlayer(game, name);
			if (!player.getTurn().isCleanup()) {
				ChoiceState discardChoice = createChoice(game, player, (g,p) -> {return List.of("DISCARD","PLAY");}, 1, TRAIL1, "Do you want to discard the trail normally, or playit ?");
				discardChoice.getAdditionalData().put("discardSource", source);
			}
		});
		actions.put(TRAIL1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			
			if (choice.equals("PLAY")) {
				CardSources discardSourceName = (CardSources) player.getTurn().getChoicesAvailable().get(0).getAdditionalData().get("discardSource");
				
				defaultPlay(game, name, ActionService.TRAIL, discardSourceName);
				defaultDiscard(game, name, player.getPlayed(), ActionService.TRAIL);
			} else if (choice.equals("DISCARD")) {
				CardSources discardSourceName = (CardSources) player.getTurn().getChoicesAvailable().get(0).getAdditionalData().get("discardSource");
				CardSource discardSource = cardSourceFunctions.get(discardSourceName).get(game, name);
				
				defaultDiscard(game, name, discardSource, ActionService.TRAIL);
			} else if (choice.equals("TRASH")) {				
				defaultTrash(game, name, ActionService.TRAIL, player.getHand());
			} else if (choice.equals("GAIN")) {				
				defaultGain(game, name, ActionService.TRAIL);
			} else if (choice.equals("PLAY1")) {
				// TODO: might need to not move this to hand at some point
				// TODO: this is going to need to call doGain or defaultGain?
				moveCard(game.getBank().getSupplies().get(ActionService.TRAIL), getPlayer(game, name).getHand());
				defaultPlay(game, name, ActionService.TRAIL, CardSources.HAND);
			} else if (choice.equals("PLAY2")) {
				defaultPlay(game, name, ActionService.TRAIL, CardSources.HAND);
				defaultDiscard(game, name, player.getPlayed(), ActionService.TRAIL);
			}
		});
		gainEvents.put(TRAIL, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			createChoice(game, player, (g,p) -> {return List.of("GAIN","PLAY1");}, 1, TRAIL1, "Do you want to gain the trail normally, or play it immediately?");			
		});
		// TODO:not sure how this will interact with actions that are "trash to do X" like Spice Merchant (tested remodel ok)
		trashEvents.put(TRAIL, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			createChoice(game, player, (g,p) -> {return List.of("TRASH","PLAY2");}, 1, TRAIL1, "Do you want to trash the trail normally, or play it?");
		});
		
		discardEvents.put(TUNNEL, (game, name, source) -> {
			PlayerState player = getPlayer(game, name);
			if (!player.getTurn().isCleanup()) {
				// TODO: reveal
				createChoice(game, player, (g,p) -> {return List.of("YES","NO");}, 1, TUNNEL1, "Do you want to reveal this to gain a gold?");
			} else {
				defaultDiscard(game, name, player.getHand(), TUNNEL);
			}
		});
		actions.put(TUNNEL1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("YES")) {
				doGain(game, name, ActionService.GOLD);
				// TODO: I forget, what is this action change for? copy/paste?
				playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
			}
			defaultDiscard(game, name, player.getHand(), TUNNEL);
		});
		
		actions.put(FOOLS_GOLD, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getPlayed().getCards().stream().anyMatch(c -> c.equals(FOOLS_GOLD))) {
				addTreasure(getPlayer(game, name).getTurn(), 4);
			} else {
				addTreasure(getPlayer(game, name).getTurn(), 1);
			}
		});
		// TODO: the reaction part
		
		actions.put(SPICE_MERCHANT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).count() > 0) {
				ChoiceOptionCreator treasureInHand = (g, p) -> {
					return p.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList());
				};
				createChoice(game, player, treasureInHand, 1, SPICE_MERCHANT1, "Do you want to trash a treasure card?");
			}
		});
		actions.put(SPICE_MERCHANT1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					doTrash(game, name, choice, player.getHand());
					createChoice(game, player, (g,p) -> {return List.of("CARDS","BUY");}, 1, SPICE_MERCHANT2, "Do you want +2 Cards/+1 Action or +1 Buy/+2 treasure?");
				}
			}
		});
		actions.put(SPICE_MERCHANT2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if ("CARDS".equals(choice)) {
					defaultDraw(game, name);
					defaultDraw(game, name); 
					playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
				} else if ("BUY".equals(choice)) {
					addTreasure(getPlayer(game, name).getTurn(), 2);
					getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());
				}
			}
		});
		
		actions.put(STABLES, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			ChoiceOptionCreator treasureInHand = (g, p) -> {
				List<String> choices = p.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList());
				choices.add("No");
				return choices;
			};
			createChoice(game, player, treasureInHand, 1, STABLES1, "Do you want to discard a treasure?");
		});
		actions.put(STABLES1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice) && !"No".equals(choice)) {
					discard(game, name, player.getHand(), choice);
					defaultDraw(game, name);
					defaultDraw(game, name);
					defaultDraw(game, name); 
					playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
				}
			}
		});
		
		actions.put(WHEELWRIGHT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1);
			ChoiceOptionCreator optionalHandCard = (g, p) -> {
				List<String> choices = p.getHand().getCards().stream().collect(Collectors.toList());
				choices.add("No");
				return choices;
			};
			createChoice(game, player, optionalHandCard, 1, WHEELWRIGHT1, "Do you want to discard?");
		});
		actions.put(WHEELWRIGHT1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				// TODO: if there are 0 cards matching the cost, does the UI correctly send the empty string option, or do i need something else here? could just bypass this
				if (!"".equals(choice) && !"No".equals(choice)) {
					int cost = getCost(game, name, choice);
					discard(game, name, player.getHand(), choice);
					ChoiceOptionCreator cheaperFromBank = (g, p) -> {
						List<String> result = getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= cost && CardData.cardInfo.get(c).isAction() ).collect(Collectors.toList());
						return result;
					};
					
					createChoice(game, player, cheaperFromBank, 1, WHEELWRIGHT2, "Choose a card to gain");
				}
			}
		});
		actions.put(WHEELWRIGHT2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				doGain(game, name, choice);
			}
		});
		
		actions.put(SOUK, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			player.getTurn().setBuys(1 + player.getTurn().getBuys());
			int treasureAdd = player.getHand().getCards().size() > 7 ? 0 : 7 - player.getHand().getCards().size();
			this.addTreasure(player.getTurn(), treasureAdd);
		});
		gainEvents.put(SOUK, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			createChoice(game, player, (g, p) -> chooseFromHand(p), 0, 2, SOUK1, "Do you want to trash cards?");
		});
		actions.put(SOUK1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			for (String choice : player.getTurn().getChoicesMade()) {
				doTrash(game, name, choice, player.getHand());
			}
		});
		
		actions.put(NOMADS, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2);
			getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());
		});
		gainEvents.put(NOMADS, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2);
			defaultGain(game, name, NOMADS);
		});
		trashEvents.put(NOMADS, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2);
			defaultTrash(game, name, ActionService.NOMADS, getPlayer(game, name).getHand());
		});
		
		actions.put(CACHE, (game, name) -> {addTreasure(getPlayer(game, name).getTurn(), 3);});
		gainEvents.put(CACHE, (game, name) -> {
			doGain(game, name, COPPER);
			doGain(game, name, COPPER);
		});
		
		// printed card says "Buy" but digital says "Gain" is the new rule
		gainEvents.put(FARMLAND, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getHand().size() > 0) {
				createChoice(game, player, (g, p) -> chooseFromHand(p), 1, FARMLAND1, "Choose a card to trash");
			}
			defaultGain(game, name, FARMLAND);
			
		});
		actions.put(FARMLAND1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					int cost = getCost(game, name, choice);
					
					doTrash(game, name, choice, player.getHand());
					ChoiceOptionCreator twoMoreFromBank = (g, p) -> {
						return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) == cost + 2).collect(Collectors.toList());
					};
					createChoice(game, player, twoMoreFromBank, 1, FARMLAND2, "Choose a card to gain");	
				}
			}
		});
		actions.put(FARMLAND2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					doGain(game, name, choice);
				}
			}
		});
		
		actions.put(ARTISAN, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			ChoiceOptionCreator fromBank = (g, p) -> {
				return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= 5).collect(Collectors.toList());
			};
			createChoice(game, player, fromBank, 1, ARTISAN1, "Choose a card to gain");
		});
		actions.put(ARTISAN1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					player.getTurn().setGainDestination("HAND");
					doGain(game, name, choice);
				}
			}
			createChoice(game, player, (g, p) -> chooseFromHand(p), 1, ARTISAN2, "Choose a card to put on your deck");
		});
		actions.put(ARTISAN2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					moveCard(player.getHand(), player.getDeck(), choice);
				}
			}
		});
		
		actions.put(POACHER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1); 
			addTreasure(getPlayer(game, name).getTurn(), 1);

			long emptyPiles = game.getBank().getSupplies().values().stream().filter(s -> s.getCount() == 0).count();
			int discardCount = Math.min((int) emptyPiles, player.getHand().size());
			if (emptyPiles > 0) {
				createChoice(game, player, (g, p) -> chooseFromHand(p), discardCount, POACHER1, "Choose " + emptyPiles + " to discard");	
			}
		});
		actions.put(POACHER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				for(String card : player.getTurn().getChoicesMade()) {
					discard(game, name, player.getHand(), card);
				}
			}
		});
		
		actions.put(MERCHANT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1); 
			
			player.getTurn().getPlayActions().computeIfAbsent(SILVER, k -> new ArrayList<String>());
			player.getTurn().getPlayActions().get(SILVER).add(MERCHANT1);
		});
		actions.put(MERCHANT1, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 1);
			PlayerState player = getPlayer(game, name);
			boolean success = player.getTurn().getPlayActions().get(SILVER).remove(MERCHANT1);
			log.info("remove? " + success);
		});
		
		actions.put(HARBINGER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), 1); 
			
			if (player.getDiscard().getCards().size() > 0) {
				createChoice(game, player, (g, p) -> chooseFromDiscard(p), 1, HARBINGER1, "Do you want to put a card from discard onto deck?");	
			}
		});
		actions.put(HARBINGER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					moveCard(player.getDiscard(), player.getDeck(), choice);
				}
			}
		});
		
		actions.put(CHAPEL, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			createChoice(game, player, (g, p) -> chooseFromHand(p), 0, 4, CHAPEL1, "Choose up to 4 cards to trash");				
		});
		actions.put(CHAPEL1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				for(String card : player.getTurn().getChoicesMade()) {
					doTrash(game, name, card, player.getHand());
				}
			}
		});
		
		actions.put(CHANCELLOR, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 2);
			createChoice(game, player, (g, p) -> List.of("YES","NO"), 1, CHANCELLOR1, "Do you want to put your deck into your discard?");				
		});
		actions.put(CHANCELLOR1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if ("YES".equals(choice)) {
					while (player.getDeck().getCards().size() > 0) {
						moveCard(player.getDeck(), player.getDiscard());
					}
				}
			}
		});
		
		actions.put(MINE, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			ChoiceOptionCreator treasureInHand = (g, p) -> {
				return player.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList());
			};
			// This is optional in newer versions, my print version is "do it"
			createChoice(game, player, treasureInHand, 0, 1, MINE1, "Choose a treasure to trash");			
		});
		actions.put(MINE1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					int cost = getCost(game, name, choice);
					
					doTrash(game, name, choice, player.getHand());
					ChoiceOptionCreator threeMoreFromBank = (g, p) -> {
						return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= cost + 3 && CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList());
					};
					createChoice(game, player, threeMoreFromBank, 1, MINE2, "Choose a treasure to gain");	
				}
			}
		});
		actions.put(MINE2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					player.getTurn().setGainDestination("HAND");
					doGain(game, name, choice);
				}
			}
		});
		
		gainReactions.put(TRADER, (game, name, cardName) -> {
			PlayerState player = getPlayer(game, name);
			
			createChoice(game, player, (g, p) -> List.of("Silver",cardName), 1, TRADER1, "Do you want to gain the original card or a silver?");		
			
		});
		actions.put(TRADER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				defaultGain(game, name, choice);
			}
		});
		actions.put(TRADER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getHand().size() > 0) {
				createChoice(game, player, (g, p) -> chooseFromHand(p), 1, TRADER2, "Choose a card to trash");
			}
		});
		actions.put(TRADER2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					int cost = getCost(game, name, choice);
					doTrash(game, name, choice, player.getHand());
					for (int i=0; i<cost; i++) {
						doGain(game, name, "Silver");
					}
				}
			}
		});

		actions.put(CELLAR, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			playerService.defaultActionChange(player.getTurn(), 1);
			
			createChoice(game, player, (g, p) -> chooseFromHand(p), 0, player.getHand().size(), CELLAR1, "Choose cards to discard");				
		});
		actions.put(CELLAR1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				int discardCount = player.getTurn().getChoicesMade().size();
				// TODO: does this order matter for Cellar-Trail or other discard events? Maybe Cellar-Weaver the knowledge of what you drew would inform the choice on Weaver?
				for (String discard : player.getTurn().getChoicesMade()) {
					discard(game, name, player.getHand(), discard);
				}
				for (int i=0; i<discardCount; i++) {
					defaultDraw(game, name);
				}
			}
		});
		
		actions.put(WORKSHOP, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			ChoiceOptionCreator fromBankLessThan5 = (g, p) -> {
				return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= 4).collect(Collectors.toList());
			};
			
			createChoice(game, player, fromBankLessThan5, 1, WORKSHOP1, "Choose a card to gain");			
		});
		actions.put(WORKSHOP1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					doGain(game, name, choice);	
				}
			}
		});
		
		actions.put(FEAST, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			// TODO: this may interfere with Berserker's "if you have an action in play". what's correct?
			if (player.getTurn().getRepeatedAction() == null) {
				doTrash(game, name, FEAST, player.getPlayed());
			}
			
			ChoiceOptionCreator fromBankLessThan6 = (g, p) -> {
				return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) <= 5).collect(Collectors.toList());
			};
			
			createChoice(game, player, fromBankLessThan6, 1, FEAST1, "Choose a card to gain");			
		});
		actions.put(FEAST1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if (!"".equals(choice)) {
					doGain(game, name, choice);	
				}
			}
		});

		actions.put(MONEYLENDER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			if (player.getHand().getCards().contains("Copper")) {				
				createChoice(game, player, (g, p) -> List.of("YES","NO"), 1, MONEYLENDER1, "Do you want to trash a copper?");	
			}
		});
		actions.put(MONEYLENDER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if ("YES".equals(choice)) {
					doTrash(game, name, COPPER, player.getHand());
					addTreasure(player.getTurn(), 3);
				}
			}
		});
		
		actions.put(COUNCIL_ROOM, (game, name) -> {
			defaultDraw(game, name); 
			defaultDraw(game, name);
			defaultDraw(game, name);
			defaultDraw(game, name);
			PlayerState player = getPlayer(game, name);
			player.getTurn().setBuys(1 + player.getTurn().getBuys());
			
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, COUNCIL_ROOM1, name);
			});
		});
		actions.put(COUNCIL_ROOM1, (game, targetName) -> {
			defaultDraw(game, targetName);
			finishAttack(game, targetName);
		});
		
		actions.put(LIBRARY, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getHand().size() < 7) {
				if (player.getDeck().getCards().size() == 0) {
					moveDiscardToDeck(player);
				}
				if (CardData.cardInfo.get(player.getDeck().getCards().get(0)).isAction()) {
					moveCard(player.getDeck(), player.getLooking());
					createChoice(game, player, (g, p) -> List.of(player.getLooking().getCards().get(0),"NO"), 1, LIBRARY1, "Do you want to set this action aside, to be discarded? (Otherwise it goes to your hand)");	
				} else {
					defaultDraw(game, name);
					actions.get(LIBRARY).execute(game, name);
				}
			} else {
				List<String> aside = new ArrayList<>();
				aside.addAll(player.getAside().getCards());
				for (int i=0; i<aside.size(); i++) {
					discard(game, name, player.getAside(), aside.get(i));
				}
			}
		});
		actions.put(LIBRARY1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if ("NO".equals(choice)) {
					moveCard(player.getLooking(), player.getDeck());
					defaultDraw(game, name);
				} else {
					moveCard(player.getLooking(), player.getAside(), choice);
				}
			}
			actions.get(LIBRARY).execute(game, name);
		});
		
		actions.put(ILL_GOTTEN_GAINS, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 1);
			createChoice(game, player, (g, p) -> List.of("YES","NO"), 1, ILL_GOTTEN_GAINS1, "Do you want to gain a copper to your hand?");
		});
		actions.put(ILL_GOTTEN_GAINS1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				String choice = player.getTurn().getChoicesMade().get(0);
				if ("YES".equals(choice)) {
					player.getTurn().setGainDestination("HAND");
					doGain(game, name, ActionService.COPPER);
				}
			}
		});
		gainEvents.put(ILL_GOTTEN_GAINS, (game, name) -> {
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				if (game.getBank().getSupplies().get(CURSE).getCount() > 0) {
					doGain(game, other, CURSE);
				}
			});
			
			defaultGain(game, name, ILL_GOTTEN_GAINS);
		});
		
		actions.put(NOMAD_CAMP, (game, name) -> {
			addTreasure(getPlayer(game, name).getTurn(), 2); 
			getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());
		});
		gainEvents.put(NOMAD_CAMP, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			player.getTurn().setGainDestination("DECK");
			defaultGain(game, name, NOMAD_CAMP);
		});
		
		actions.put(MARGRAVE, (game, name) -> {
			defaultDraw(game, name); 
			defaultDraw(game, name);
			defaultDraw(game, name);
			getPlayer(game, name).getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());
			
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, MARGRAVE1, name);
			});
		});
		actions.put(MARGRAVE1, (game, targetName) -> {
			PlayerState thisPlayer = getPlayer(game, targetName);
			defaultDraw(game, targetName);
			int handSize = getPlayer(game, targetName).getHand().size();
			if (handSize > 3) {
				int discardSize = handSize - 3;
				createChoice(game, getPlayer(game, targetName), (g, p) -> chooseFromHand(p), discardSize, MARGRAVE2, "Choose " + discardSize + " cards to discard");
			}
		});
		actions.put(MARGRAVE2, (game, targetName) -> {
			PlayerState player = getPlayer(game, targetName);
			List<String> cardNames = player.getTurn().getChoicesMade();
			cardNames.stream().forEach(n -> discard(game, targetName, player.getHand(), n));
			finishAttack(game, targetName);
		});
		
		actions.put(INN, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			defaultDraw(game, name);
			playerService.defaultActionChange(player.getTurn(), 2);
			
			createChoice(game, getPlayer(game, name), (g, p) -> chooseFromHand(p), 2, INN1, "Choose 2 cards to discard");
		});
		actions.put(INN1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				List<String> cardNames = player.getTurn().getChoicesMade();
				cardNames.stream().forEach(n -> discard(game, name, player.getHand(), n));
			}
		});
		gainEvents.put(INN, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultGain(game, name, INN);
			if (player.getDiscard().getCards().size() > 0) {
				int numActionsDiscarded = (int) player.getDiscard().getCards().stream().filter(c -> CardData.cardInfo.get(c).isAction()).count();
				ChoiceOptionCreator discardActions = (g, p) -> {
					return p.getDiscard().getCards().stream().filter(c -> CardData.cardInfo.get(c).isAction()).collect(Collectors.toList());
				};
				createChoice(game, player, discardActions, 0, numActionsDiscarded, INN2, "Choose action cards to shuffle into your deck");	
			}
		});
		actions.put(INN2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				List<String> cardNames = player.getTurn().getChoicesMade();
				for(String card : cardNames) {
					moveCard(player.getDiscard(), player.getDeck(), card);
					shuffle(player);
				}
			}
		});
		
		actions.put(MANDARIN, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 3);
			if (player.getHand().size() > 0) {
				createChoice(game, getPlayer(game, name), (g, p) -> chooseFromHand(p), 1, MANDARIN1, "Choose a card to put on your deck");
			}
		});
		actions.put(MANDARIN1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				moveCard(player.getHand(), player.getDeck(), player.getTurn().getChoicesMade().get(0));
			}
		});
		gainEvents.put(MANDARIN, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultGain(game, name, MANDARIN);
			// TODO: ordering
			for (String card : player.getPlayed().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList())) {
				moveCard(player.getPlayed(), player.getDeck(), card);
			}
		});
		
		actions.put(JACK_OF_ALL_TRADES, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			doGain(game, name, SILVER);
			deckPeek(game, name);
			createChoice(game, player, (g,p) -> {return List.of("DISCARD","RETURN");}, 1, JACK_OF_ALL_TRADES1, "Do you want to discard the top card from your deck, or return it?");
		});
		actions.put(JACK_OF_ALL_TRADES1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				if (player.getTurn().getChoicesMade().get(0).equals("DISCARD")) {
					discard(game, name, player.getLooking(), player.getLooking().getCards().get(0));
				} else {
					moveCard(player.getLooking(), player.getDeck(), player.getLooking().getCards().get(0));
				}
			}
			while (player.getHand().size() < 5) {
				defaultDraw(game, name);
			}
			if (player.getHand().getCards().stream().anyMatch(c -> !CardData.cardInfo.get(c).isTreasure())) {
				ChoiceOptionCreator nonTreasure = (g, p) -> {
					List<String> result = p.getHand().getCards().stream().filter(c -> !CardData.cardInfo.get(c).isTreasure()).collect(Collectors.toList());
					result.add("None");
					return result;
				};
				createChoice(game, player, nonTreasure, 1, JACK_OF_ALL_TRADES2, "Choose a non-treasure card to trash");
			}
		});
		actions.put(JACK_OF_ALL_TRADES2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				if (!"".equals(player.getTurn().getChoicesMade().get(0))) {
					if (!"None".equals(player.getTurn().getChoicesMade().get(0))) {
						doTrash(game, name, player.getTurn().getChoicesMade().get(0), player.getHand());
					}
				}
			}
		});
		
		actions.put(NOBLE_BRIGAND, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 1);
			
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, NOBLE_BRIGAND1, name);
			});
		});
		actions.put(NOBLE_BRIGAND1, (game, targetName) -> {
			PlayerState player = getPlayer(game, targetName);
			for (int i=0; i<2; i++) {
				if (player.getDeck().getCards().size() == 0) {
					moveDiscardToDeck(player);
				}
				moveCard(player.getDeck(), player.getLooking());	
			}
			PlayerState attacker = getPlayer(game, player.getAttacks().peek().getAttacker());
			
			finishAttack(game, targetName);
			if (player.getLooking().getCards().stream().noneMatch(c -> CardData.cardInfo.get(c).isTreasure())) {
				doGain(game, targetName, COPPER);
			} else if (player.getLooking().getCards().stream().noneMatch(c -> c.equals(ActionService.GOLD) || c.equals(ActionService.SILVER))) {
				
			} else {
				ChoiceOptionCreator nobleBrigand = (g, p) -> {
					ArrayList<String> result = new ArrayList<>(player.getLooking().getCards().stream().map(c -> {return targetName + " : " + c;}).toList());
					result.add(targetName + " : " + "Discard both");
					return result;
				};
				createChoice(game, attacker, nobleBrigand, 1, NOBLE_BRIGAND2, "Choose a silver or gold to steal");
			}
		});
		actions.put(NOBLE_BRIGAND2, (game, attackerName) -> {
			PlayerState player = getPlayer(game, attackerName);
			if (player.getTurn().getChoicesMade().size() > 0) {
				if (!"".equals(player.getTurn().getChoicesMade().get(0))) {
					String[] choiceData = player.getTurn().getChoicesMade().get(0).split(" : ");
					PlayerState target = getPlayer(game, choiceData[0]);
					if (choiceData[1].trim().equals("Discard both")) {
						discard(game, choiceData[0], target.getLooking(), target.getLooking().getCards().get(0));
						discard(game, choiceData[0], target.getLooking(), target.getLooking().getCards().get(0));						
					} else {
						// TODO: technically this should be doGain, but I think a pointless Trader is the only current reason
						moveCard(target.getLooking(), player.getDiscard(), choiceData[1].trim());
						moveCard(target.getLooking(), target.getDiscard());
					}
				}
			}
		});
		
		actions.put(ADVENTURER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			// TODO: actual reveal
			while (player.getRevealing().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure()).count() < 2) {
				if (player.getDeck().getCards().size() == 0) {
					moveDiscardToDeck(player);
				} 
				moveCard(player.getDeck(), player.getRevealing());					
			}
			createChoice(game, player, (g,p) -> p.getLooking().getCards(), 0, ADVENTURER1, "Treasure cards will be added to your hand, others will be discarded");
		});
		actions.put(ADVENTURER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			while (player.getRevealing().getCards().size() > 0) {
				if (CardData.cardInfo.get(player.getRevealing().getCards().get(0)).isTreasure()) {
					moveCard(player.getRevealing(), player.getHand(), player.getRevealing().getCards().get(0));
				} else {
					moveCard(player.getRevealing(), player.getDiscard(), player.getRevealing().getCards().get(0));
				}
			}
		});
		
		actions.put(WITCH, (game, attackerName) -> {
			PlayerState player = getPlayer(game, attackerName);
			defaultDraw(game, attackerName);
			defaultDraw(game, attackerName);
			List<String> otherNames = getOtherPlayers(game, attackerName);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, WITCH1, attackerName);
			});
		});
		actions.put(WITCH1, (game, targetName) -> {
			if (game.getBank().getSupplies().get(CURSE).getCount() > 0) {
				doGain(game, targetName, CURSE);
			}
			finishAttack(game, targetName);
		});
		
		actions.put(BERSERKER, (game, attackerName) -> {
			PlayerState player = getPlayer(game, attackerName);

			int cost = getCost(game, attackerName, BERSERKER);
			
			ChoiceOptionCreator cheaperFromBank = (g, p) -> {
				return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, attackerName, c) < cost).collect(Collectors.toList());
			};
			createChoice(game, player, cheaperFromBank, 1, BERSERKER1, "Choose a card costing less than Berserker to gain");
			
			List<String> otherNames = getOtherPlayers(game, attackerName);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, BERSERKER2, attackerName);
			});
		});
		actions.put(BERSERKER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0) {
				doGain(game, name, player.getTurn().getChoicesMade().get(0));
			}
		});
		actions.put(BERSERKER2, (game, targetName) -> {
			int handSize = getPlayer(game, targetName).getHand().size();
			if (handSize > 3) {
				int discardSize = handSize - 3;
				createChoice(game, getPlayer(game, targetName), (g, p) -> chooseFromHand(p), discardSize, BERSERKER3, "Choose " + discardSize + " cards to discard");
			} else {
				// TODO: maybe need to switch this to the version with choices, but I think I solved that with queuing attacks
				finishAttack(game, targetName);
			}
		});
		actions.put(BERSERKER3, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			List<String> cardNames = player.getTurn().getChoicesMade();
			cardNames.stream().forEach(n -> discard(game, name, player.getHand(), n));
			finishAttack(game, name);
		});
		gainEvents.put(BERSERKER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getPlayed().getCards().stream().anyMatch(c -> CardData.cardInfo.get(c).isAction())) {
				player.getTurn().setGainDestination("HAND");
				defaultGain(game, name, BERSERKER);
				defaultPlay(game, name, BERSERKER, CardSources.HAND);
			} else {
				defaultGain(game, name, BERSERKER);
			}
		});

		actions.put(CROSSROADS, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			// TODO: reveal hand
			long victoryCards = player.getHand().getCards().stream().filter(c -> CardData.cardInfo.get(c).isVictory()).count();
			boolean playedCrossroads = player.getPlayed().getCards().stream().anyMatch(c -> c.equals(CROSSROADS));
			for (int i=0; i<victoryCards; i++) {
				defaultDraw(game, name);
			}
			if (!playedCrossroads) {
				playerService.defaultActionChange(player.getTurn(), 3);
			}
		});
		
		actions.put(DEVELOP, (game, name) -> {
			PlayerState player = getPlayer(game, name); 
			if (player.getHand().size() > 0) {
				createChoice(game, player, (g, p) -> chooseFromHand(p), 1, DEVELOP1, "Choose a card from your hand to trash");
			}
		});
		actions.put(DEVELOP1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String trashedCardName = player.getTurn().getChoicesMade().get(0);
			int trashedCost = getCost(game, name, trashedCardName);
			doTrash(game, name, trashedCardName, player.getHand());
			ChoiceOptionCreator bothCostsFromBank = (g, p) -> {
				return getNonEmptyBankSuppliesStream(game).filter(c -> getCost(game, name, c) == trashedCost + 1 || getCost(game, name, c) == trashedCost - 1).collect(Collectors.toList());
			};
			createChoice(game, player, bothCostsFromBank, 1, DEVELOP2, "Choose a card to gain");			
		});
		actions.put(DEVELOP2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0 && !player.getTurn().getChoicesMade().get(0).equals("")) {
				String gainedCardName = player.getTurn().getChoicesMade().get(0);
				player.getTurn().setGainDestination("DECK");
				doGain(game, name, gainedCardName);		
				
				int gainedCost = getCost(game, name, gainedCardName);
				List<String> otherCostCards = player.getTurn().getChoicesAvailable().get(0).getOptions().stream().filter(c -> getCost(game, name, c) != gainedCost).toList();
				ChoiceOptionCreator otherCostFromBank = (g, p) -> {
					return otherCostCards;
				};
				createChoice(game, player, otherCostFromBank, 1, DEVELOP3, "Choose a card to gain");
			}
		});
		actions.put(DEVELOP3, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			if (player.getTurn().getChoicesMade().size() > 0 && !player.getTurn().getChoicesMade().get(0).equals("")) {
				String gainedCardName = player.getTurn().getChoicesMade().get(0);
				player.getTurn().setGainDestination("DECK");
				doGain(game, name, gainedCardName);		
			}
		});
		
		actions.put(SENTRY, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(player.getTurn(), 1);
			
			deckPeek(game, name);
			deckPeek(game, name);
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK : " + player.getLooking().getCards().get(0),
						"DISCARD : " + player.getLooking().getCards().get(0),
						"TRASH : " + player.getLooking().getCards().get(0),
						"DECK : " + player.getLooking().getCards().get(1),
						"DISCARD : " + player.getLooking().getCards().get(1),
						"TRASH : " + player.getLooking().getCards().get(1)
						);
			};
			createChoice(game, player, actions, 1, SENTRY1, "These are the top 2 cards of your deck. Choose a card and whether to discard, trash, or return to the deck. Then you'll have the same choice for the next card.");
		});
		actions.put(SENTRY1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String[] actionData = player.getTurn().getChoicesMade().get(0).split(" : ");
			String card = actionData[1].trim();
			String action = actionData[0].trim();
			switch(action) {
			case "DECK":
				moveCard(player.getLooking(), player.getDeck(), card);
				break;
			case "DISCARD":
				discard(game, name, player.getLooking(), card);
				break;
			case "TRASH":
				doTrash(game, name, card, player.getLooking());
				break;
			}
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK : " + player.getLooking().getCards().get(0),
						"DISCARD : " + player.getLooking().getCards().get(0),
						"TRASH : " + player.getLooking().getCards().get(0)
						);
			};
			createChoice(game, player, actions, 1, SENTRY2, "Choose whether to discard, trash, or return to the deck");
		});
		actions.put(SENTRY2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String[] actionData = player.getTurn().getChoicesMade().get(0).split(" : ");
			String card = actionData[1].trim();
			String action = actionData[0].trim();
			switch(action) {
			case "DECK":
				moveCard(player.getLooking(), player.getDeck(), card);
				break;
			case "DISCARD":
				discard(game, name, player.getLooking(), card);
				break;
			case "TRASH":
				doTrash(game, name, card, player.getLooking());
				break;
			}
		});
		
		actions.put(WITCHS_HUT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); defaultDraw(game, name); defaultDraw(game, name); defaultDraw(game, name);
			
			createChoice(game, player, (g, p) -> chooseFromHand(p), 2, WITCHS_HUT1, "Choose two cards from your hand to discard");			
		});
		actions.put(WITCHS_HUT1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			// TODO: reveal
			if (player.getTurn().getChoicesMade().size() > 0) {
				String card1 = player.getTurn().getChoicesMade().get(1);
				discard(game, name, player.getHand(), card1);
				String card2 = player.getTurn().getChoicesMade().get(0);
				discard(game, name, player.getHand(), card2);
				
				if (CardData.cardInfo.get(card1).isAction() && CardData.cardInfo.get(card2).isAction()) {
					List<String> otherNames = getOtherPlayers(game, name);
					otherNames.stream().forEach(other -> {
						startAttack(game, other, WITCHS_HUT2, name);
					});
				}
			}
		});
		actions.put(WITCHS_HUT2, (game, targetName) -> {
			if (game.getBank().getSupplies().get(CURSE).getCount() > 0) {
				doGain(game, targetName, CURSE);
			}
			finishAttack(game, targetName);
		});
		
		actions.put(CARTOGRAPHER, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(player.getTurn(), 1);
			
			deckPeek(game, name);
			deckPeek(game, name);
			deckPeek(game, name);
			deckPeek(game, name);
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK : " + player.getLooking().getCards().get(0),
						"DISCARD : " + player.getLooking().getCards().get(0),
						"DECK : " + player.getLooking().getCards().get(1),
						"DISCARD : " + player.getLooking().getCards().get(1),
						"DECK : " + player.getLooking().getCards().get(2),
						"DISCARD : " + player.getLooking().getCards().get(2),
						"DECK : " + player.getLooking().getCards().get(3),
						"DISCARD : " + player.getLooking().getCards().get(3)
						);
			};
			createChoice(game, player, actions, 1, CARTOGRAPHER1, "Choose a card and discard or return to the deck");
		});
		actions.put(CARTOGRAPHER1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String[] actionData = player.getTurn().getChoicesMade().get(0).split(" : ");
			String card = actionData[1].trim();
			String action = actionData[0].trim();
			switch(action) {
			case "DECK":
				moveCard(player.getLooking(), player.getDeck(), card);
				break;
			case "DISCARD":
				discard(game, name, player.getLooking(), card);
				break;
			}
			
			if (player.getLooking().getCards().size() > 0) {
				ChoiceOptionCreator actions = (g, p) -> {
					List<String> options = new ArrayList<>();
					for (int i=0; i<player.getLooking().getCards().size(); i++) {
						options.add("DECK : " + player.getLooking().getCards().get(i));
						options.add("DISCARD : " + player.getLooking().getCards().get(i));
					}
					return options;
				};
				createChoice(game, player, actions, 1, CARTOGRAPHER1, "Choose a card and discard, trash, or return to the deck");	
			}
		});
		
		actions.put(ORACLE, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			// TODO: should all player reveals happen before choosing?
			if (player.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(player);
				shuffle(player);
			}
			moveCard(player.getDeck(), player.getRevealing());
			if (player.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(player);
				shuffle(player);
			}
			moveCard(player.getDeck(), player.getRevealing());
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of(player.getRevealing().getCards().get(0),
						player.getRevealing().getCards().get(1),
						"DISCARD"
						);
			};
			createChoice(game, player, actions, 1, ORACLE1, "Choose to discard both, or choose the first card returned to the deck (the second will be returned after it)");
			
			// TODO: do i get the order of options right if other players submit responses while i'm thinking about my choice?
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, ORACLE2, name);
			});
		});
		actions.put(ORACLE1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("DISCARD")) {
				discard(game, name, player.getRevealing(), player.getRevealing().getCards().get(0));
				discard(game, name, player.getRevealing(), player.getRevealing().getCards().get(0));
			} else {
				moveCard(player.getRevealing(), player.getDeck(), choice);
				moveCard(player.getRevealing(), player.getDeck(), player.getRevealing().getCards().get(0));
			}
		});
		actions.put(ORACLE2, (game, targetName) -> {
			PlayerState targetPlayer = getPlayer(game, targetName);
			
			if (targetPlayer.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(targetPlayer);
				shuffle(targetPlayer);
			}
			moveCard(targetPlayer.getDeck(), targetPlayer.getRevealing());
			
			if (targetPlayer.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(targetPlayer);
				shuffle(targetPlayer);
			}
			moveCard(targetPlayer.getDeck(), targetPlayer.getRevealing());
			PlayerState attackerPlayer = getPlayer(game, targetPlayer.getAttacks().peek().getAttacker());			
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK : " + targetName,
						"DISCARD : " + targetName
						);
			};
			createChoice(game, attackerPlayer, actions, 1, ORACLE3, targetName + " has revealed " + targetPlayer.getRevealing().getCards().get(0) + " and " + targetPlayer.getRevealing().getCards().get(1) + ". Do they discard or return to the deck?");
			
		});
		actions.put(ORACLE3, (game, attackerName) -> {
			PlayerState player = getPlayer(game, attackerName);
			String[] choice = player.getTurn().getChoicesMade().get(0).split(" : ");
			String targetName = choice[1].trim();
			PlayerState targetPlayer = getPlayer(game, targetName);
			if (choice[0].trim().equals("DECK")) {
				ChoiceOptionCreator actions = (g, p) -> {
					return List.of(targetPlayer.getRevealing().getCards().get(0),
							targetPlayer.getRevealing().getCards().get(1)
							);
				};
				createChoice(game, targetPlayer, actions, 1, ORACLE4, "Choose the first card to go back to your deck. The second will go back automatically");
			} else {
				discard(game, targetName, targetPlayer.getRevealing(), targetPlayer.getRevealing().getCards().get(0));
				discard(game, targetName, targetPlayer.getRevealing(), targetPlayer.getRevealing().getCards().get(0)); 
			}
		});
		actions.put(ORACLE4, (game, targetName) -> {
			PlayerState player = getPlayer(game, targetName);
			String choice = player.getTurn().getChoicesMade().get(0);
			moveCard(player.getRevealing(), player.getDeck(), choice);
			moveCard(player.getRevealing(), player.getDeck(), player.getRevealing().getCards().get(0));
			finishAttack(game, targetName);
		});
		

		actions.put(BANDIT, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			
			if (game.getBank().getSupplies().get(GOLD).getCount() > 0) {
				doGain(game, name, GOLD);
			}
			
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, BANDIT1, name);
			});
		});
		actions.put(BANDIT1, (game, targetName) -> {
			PlayerState targetPlayer = getPlayer(game, targetName);
			
			if (targetPlayer.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(targetPlayer);
				shuffle(targetPlayer);
			}
			moveCard(targetPlayer.getDeck(), targetPlayer.getRevealing());

			if (targetPlayer.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(targetPlayer);
				shuffle(targetPlayer);
			}
			moveCard(targetPlayer.getDeck(), targetPlayer.getRevealing());
			
			// TODO: reveal
			ChoiceOptionCreator actions = (g, p) -> {
				return p.getRevealing().getCards().stream().filter(c -> CardData.cardInfo.get(c).isTreasure() && !c.equals(COPPER)).toList();
			};
			createChoice(game, targetPlayer, actions, 1, BANDIT2, "Someone has played a Bandit - choose a non-Copper Treasure to trash");			
		});
		actions.put(BANDIT2, (game, targetName) -> {
			PlayerState targetPlayer = getPlayer(game, targetName);
			if (targetPlayer.getTurn().getChoicesMade().size() > 0) {
				if (!targetPlayer.getTurn().getChoicesMade().get(0).equals("")) {
					String choice = targetPlayer.getTurn().getChoicesMade().get(0);	
					doTrash(game, targetName, choice, targetPlayer.getRevealing());
				}
			}
			
			int numberRevealed = targetPlayer.getRevealing().getCards().size();
			for (int i=0; i<numberRevealed; i++) {
				discard(game, targetName, targetPlayer.getRevealing(), targetPlayer.getRevealing().getCards().get(0));
			}
			finishAttack(game, targetName);
		});
		
		actions.put(SPY, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			defaultDraw(game, name); 
			playerService.defaultActionChange(player.getTurn(), 1);
			
			// TODO: should all player reveals happen before choosing?
			if (player.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(player);
				shuffle(player);
			}
			moveCard(player.getDeck(), player.getRevealing());
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK",
						"DISCARD"
						);
			};
			createChoice(game, player, actions, 1, SPY1, "Choose to discard or return to deck");
			
			// TODO: do i get the order of options right if other players submit responses while i'm thinking about my choice?
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				startAttack(game, other, SPY2, name);
			});
		});
		actions.put(SPY1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if (choice.equals("DISCARD")) {
				discard(game, name, player.getRevealing(), player.getRevealing().getCards().get(0));
			} else {
				moveCard(player.getRevealing(), player.getDeck(), player.getRevealing().getCards().get(0));
			}
		});
		actions.put(SPY2, (game, targetName) -> {
			PlayerState targetPlayer = getPlayer(game, targetName);
			
			if (targetPlayer.getDeck().getCards().size() == 0) {
				moveDiscardToDeck(targetPlayer);
				shuffle(targetPlayer);
			}
			moveCard(targetPlayer.getDeck(), targetPlayer.getRevealing());
			PlayerState attackerPlayer = getPlayer(game, targetPlayer.getAttacks().peek().getAttacker());			
			
			ChoiceOptionCreator actions = (g, p) -> {
				return List.of("DECK : " + targetName,
						"DISCARD : " + targetName
						);
			};
			createChoice(game, attackerPlayer, actions, 1, SPY3, targetName + " has revealed " + targetPlayer.getRevealing().getCards().get(0) + ". Do they discard or return to the deck?");

			finishAttack(game, targetName);
		});
		actions.put(SPY3, (game, attackerName) -> {
			PlayerState player = getPlayer(game, attackerName);
			String[] choice = player.getTurn().getChoicesMade().get(0).split(" : ");
			String targetName = choice[1].trim();
			PlayerState targetPlayer = getPlayer(game, targetName);
			if (choice[0].trim().equals("DECK")) {
				moveCard(targetPlayer.getRevealing(), targetPlayer.getDeck(), targetPlayer.getRevealing().getCards().get(0));
			} else {
				discard(game, targetName, targetPlayer.getRevealing(), targetPlayer.getRevealing().getCards().get(0)); 
			}
		});
		
		actions.put(CAULDRON, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 2); 
			player.getTurn().setBuys(1 + getPlayer(game, name).getTurn().getBuys());
			// TODO: technically there are cases where gaining your 3rd action causes you to play Cauldron, and wiki says that should trigger
			player.getTurn().getGainReactions().add(CAULDRON1);			
		});
		gainReactions.put(CAULDRON1, (game, name, gainedCardName) -> {
			if (CardData.cardInfo.get(gainedCardName).isAction()) {
				if (getPlayer(game,  name).getTurn().getGainedToDiscard().stream().filter(c -> CardData.cardInfo.get(c).isAction()).count() == 2) {
					List<String> otherNames = getOtherPlayers(game, name);
					otherNames.stream().forEach(other -> {
						startAttack(game, other, CAULDRON2, name);
					});
				}
			}
		});
		actions.put(CAULDRON2, (game, targetName) -> {
			if (game.getBank().getSupplies().get(CURSE).getCount() > 0) {
				doGain(game, targetName, CURSE);
			}
			finishAttack(game, targetName);
		});
		
		actions.put(DUCHESS, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			addTreasure(player.getTurn(), 2);
			deckPeek(game, name);
			createChoice(game, player, (g,p) -> List.of("DISCARD","RETURN"), 1, DUCHESS1, "Do you want to discard this or return it to your deck?");
			
			List<String> otherNames = getOtherPlayers(game, name);
			otherNames.stream().forEach(other -> {
				PlayerState otherPlayer = getPlayer(game, other);
				deckPeek(game, other);
				createChoice(game, otherPlayer, (g,p) -> List.of("DISCARD","RETURN"), 1, DUCHESS1, "Do you want to discard this or return it to your deck?");
			});
		});
		actions.put(DUCHESS1, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if ("DISCARD".equals(choice)) {
				discard(game, name, player.getLooking(), player.getLooking().getCards().get(0));
			} else {
				moveCard(player.getLooking(), player.getDeck(), player.getLooking().getCards().get(0));
			}
		});
		gainEvents.put(DUCHY, (game, name) -> {
			defaultGain(game, name, DUCHY);
			if (game.getBank().getSupplies().keySet().contains(DUCHESS) && game.getBank().getSupplies().get(DUCHESS).getCount() > 0) {
				PlayerState player = getPlayer(game, name);
				createChoice(game, player, (g,p) -> List.of("YES","NO"), 1, DUCHESS2, "Do you want to gain a Duchess?");
			}
		});
		actions.put(DUCHESS2, (game, name) -> {
			PlayerState player = getPlayer(game, name);
			String choice = player.getTurn().getChoicesMade().get(0);
			if ("YES".equals(choice)) {
				doGain(game, name, DUCHESS);
			} 
		});
	}
	
	private void tryThroneRoom(GameState game, String name) {
		PlayerState thisPlayer = getPlayer(game, name);
		if (thisPlayer.getTurn().getChoicesAvailable().size() == 0) {
			//if (getOtherPlayers(game, name).stream().noneMatch(p -> getPlayer(game, p).getAttacks().size() > 0)) {
				String action = thisPlayer.getTurn().popRepeatedAction();
				if (action != null) {
					if (!action.equals("")) {
						//thisPlayer.getTurn().pushRepeatedAction("");
						defaultExecute(game, name, action);
					}
				}
			//}
		}
	}
	
	private void defaultExecute(GameState game, String playerName, String action) {
		actions.get(action).execute(game, playerName);
		tryThroneRoom(game, playerName);
	}
	
	private ChoiceState createChoice(GameState game,
							PlayerState player,
							ChoiceOptionCreator optionSource,
							int optionCount,
							String followupAction,
							String... text) {
		List<String> choices = optionSource.createChoices(game, player);
		ChoiceState thisChoice = ChoiceState.builder().minChoices(CollectionUtils.isEmpty(choices) ? 0 : optionCount).maxChoices(optionCount).followUpAction(followupAction).text(text.length > 0 ? text[0] : "").build();
		thisChoice.addAll(choices);
		player.getTurn().getChoicesAvailable().add(thisChoice);
		return thisChoice;
	}
	
	private void createChoice(GameState game,
				PlayerState player,
				ChoiceOptionCreator optionSource,
				int minOptionCount,
				int maxOptionCount,
				String followupAction,
				String... text) {
		List<String> choices = optionSource.createChoices(game, player);
		ChoiceState thisChoice = ChoiceState.builder().minChoices(CollectionUtils.isEmpty(choices) ? 0 : minOptionCount).maxChoices(maxOptionCount).followUpAction(followupAction).text(text.length > 0 ? text[0] : "").build();
		thisChoice.addAll(choices);
		player.getTurn().getChoicesAvailable().add(thisChoice);
	}
	
	public void doChoice(GameState game, String playerName) {
		PlayerState thisPlayer = getPlayer(game, playerName);
		if (thisPlayer.getTurn().getChoicesAvailable().size() == 0) {
			throw new RuntimeException("No choices available");
		}
		ChoiceState choice = thisPlayer.getTurn().getChoicesAvailable().get(0);
		String actionName = choice.getFollowUpAction();
		for (String s : thisPlayer.getTurn().getChoicesMade()) {
			//TODO: this should compare to choicesAvailable?
			if (!StringUtils.isEmpty(s) && !thisPlayer.getTurn().getChoicesMade().contains(s)) {
				throw new RuntimeException("Invalid choice '" + s + "'");
			}
		}
		actions.get(actionName).execute(game, playerName);
		thisPlayer.getTurn().getChoicesAvailable().remove(0);
		thisPlayer.getTurn().getChoicesMade().clear();
		
		tryThroneRoom(game, playerName);

		refreshBuyableBankCards(game, playerName);
	}

	private List<String> chooseFromHand(PlayerState player) {
		return player.getHand().getCards();
	}
	
	private List<String> chooseFromDiscard(PlayerState player) {
		return player.getDiscard().getCards();
	}
	
	/*
	 * private void resetChoice(GameState game, String playerName) { getPlayer(game,
	 * playerName).getTurn().getChoicesMade().clear(); getPlayer(game,
	 * playerName).getTurn().getChoicesAvailable().remove(0); }
	 */
	
	public void defaultTrash(GameState game, String playerName, String card, CardSource source) {
		moveCard(source, game.getTrash(), card);
	}
	
	public void doTrash(GameState game, String playerName, String card, CardSource source) {
		PlayerState player = getPlayer(game, playerName);
		if (trashEvents.containsKey(card)) {
			trashEvents.get(card).execute(game, playerName);
		} else {
			defaultTrash(game, playerName, card, source);
		}
	}
	
	public void deckPeek(GameState game, String thisPlayer) {
		PlayerState player = getPlayer(game, thisPlayer);
		if (player.getDeck().getCards().size() == 0) {
			moveDiscardToDeck(player);
			shuffle(player);
		}
		moveCard(player.getDeck(), player.getLooking());
	}
	
	public String defaultDraw(GameState game, String thisPlayer) {
		PlayerState player = getPlayer(game, thisPlayer);
		if (player.getDeck().getCards().size() == 0) {
			moveDiscardToDeck(player);
			shuffle(player);
		}
		return moveCard(player.getDeck(), player.getHand());
	}
	
	private void moveDiscardToDeck(PlayerState player) {
		int discardSize = player.getDiscard().getCards().size();
		for (int i=0; i<discardSize; i++) {
			moveCard(player.getDiscard(), player.getDeck());
		}
	}
	
	public void shuffle(PlayerState player) {
		List<String> temp = new ArrayList<>();
		temp.addAll(player.getDeck().getCards());
		player.getDeck().getCards().clear();
		
		Random r = new Random();
		while(temp.size() > 0) {
			int i = r.nextInt(temp.size());
			player.getDeck().add(temp.remove(i));
		}
	}
	
	public void discard(GameState game, String thisPlayer, CardSource source, String cardName) {
		if (!discardEvents.containsKey(cardName)) {
			defaultDiscard(game, thisPlayer, source, cardName);
		} else {
			// TODO: pass in the enum rather than figuring it out? and then get object later?
			if (source == getPlayer(game, thisPlayer).getAside()) {
				discardEvents.get(cardName).execute(game, thisPlayer, CardSources.ASIDE);
			} else {
				discardEvents.get(cardName).execute(game, thisPlayer, CardSources.HAND);
			}			
		}
	}
		
	private void defaultDiscard(GameState game, String thisPlayer, CardSource source, String cardName) {
		PlayerState player = game.getPlayers().get(thisPlayer);
		moveCard(source, player.getDiscard(), cardName);
	}

	private void defaultDiscard(GameState game, String thisPlayer, CardSource source) {
		PlayerState player = game.getPlayers().get(thisPlayer);
		moveCard(source, player.getDiscard());
	}
	
	private void defaultPlay(GameState game, String thisPlayer, String cardName, CardSources playFromName) {
		PlayerState player = getPlayer(game, thisPlayer);
		CardSource playFrom = cardSourceFunctions.get(playFromName).get(game, thisPlayer);
		
		synchronized(player) {
			playFrom.remove(cardName);
			if (player.getTurn().getPlayActions().containsKey(cardName)) {
				if (player.getTurn().getPlayActions().get(cardName).size() > 0) {
					List<String> playActionsClone = new ArrayList<>();
					player.getTurn().getPlayActions().get(cardName).forEach(s -> playActionsClone.add(s));
					playActionsClone.forEach(c -> actions.get(c).execute(game, thisPlayer));				
				}
			}
			notificationService.notifyPlay(thisPlayer, cardName);
			actions.get(cardName).execute(game, thisPlayer);
			// TODO: this doesn't make much sense when playing a card as an off-turn reaction, eg Guard Dog
			player.getPlayed().add(cardName);
		}
	}

	public void turnPlay(GameState game, String name, String actionName) {
		if (CardData.cardInfo.get(actionName).isAction() && !getPlayer(game, name).getPhase().equals("action")) {
			throw new RuntimeException("Cannot play this after the action phase is complete");
		}
		if (CardData.cardInfo.get(actionName).isAction()) {
			playerService.defaultActionChange(getPlayer(game, name).getTurn(), -1);
		}
		defaultPlay(game, name, actionName, CardSources.HAND);
		refreshBuyableBankCards(game, name);
	}
	
	public void refreshBuyableBankCards(GameState game, String playerName) {
		PlayerState thisPlayer = getPlayer(game, playerName);
		thisPlayer.getBuyableBankCards().clear();
		game.getBank().getSupplies().keySet().forEach(c -> {
			if (thisPlayer.hasBuys() && thisPlayer.treasureAvailable() >= getCost(game, playerName, c) ) {
				thisPlayer.getBuyableBankCards().add(c);
			}
		});
	}

	public void doBuy(GameState game, String playerName, String buyName) {
		PlayerState player = getPlayer(game, playerName);
		TurnState turn = player.getTurn();
		turn.setBuying(buyName);
		turn.setBuys(turn.getBuys() - 1);
		if (turn.getBuyActions().size() > 0) {
			actions.get(turn.getBuyActions().get(0)).execute(game, playerName);
		} else {
			defaultBuy(game, playerName, buyName);
		}
		refreshBuyableBankCards(game, playerName);
	}
	
	public void defaultBuy(GameState game, String playerName, String boughtCardName) {
		int cost = getCost(game, playerName, boughtCardName);
		addTreasure(getPlayer(game, playerName).getTurn(), -1 * cost);
		doGain(game, playerName, boughtCardName);
	}
		
	public void doGain(GameState game, String playerName, String gainedCardName) {
		PlayerState player = getPlayer(game, playerName);
		// TODO: if there are gain reactions from played or supplies, maybe add a dimension to the map
		for (String c : player.getHand().getCards()) {
			if (gainReactions.containsKey(c)) {
				gainReactions.get(c).execute(game, playerName, gainedCardName);
				// TODO: this "return" works if Trader is the only gainReaction, may need to revisit
				return;
			}
		}
		for (String r : player.getTurn().getGainReactions()) {
			gainReactions.get(r).execute(game, playerName, gainedCardName);
		}
		if (gainEvents.containsKey(gainedCardName)) {
			gainEvents.get(gainedCardName).execute(game, playerName);
		} else {
			defaultGain(game, playerName, gainedCardName);
		}
	}
	
	public void defaultGain(GameState game, String playerName, String gainedCardName) {
		CardDestination dest = getPlayer(game, playerName).getDiscard();
		if (!StringUtils.isEmpty(getPlayer(game, playerName).getTurn().getGainDestination())) {
			dest = gainDestinationFunctions.get(getPlayer(game, playerName).getTurn().getGainDestination()).get(game, playerName);
			getPlayer(game, playerName).getTurn().setGainDestination("");
		}
		if (dest.getKey().equals("Discard")) {
			getPlayer(game, playerName).getTurn().getGainedToDiscard().add(gainedCardName);
		}
		moveCard(game.getBank().getSupplies().get(gainedCardName), dest);
	}
	
	public int getCost(GameState game, String playerName, String cardName) {
		int cost = defaultCost(cardName);
		List<String> currFunctions = getPlayer(game, playerName).getTurn().getCostFunctions();
		for (String f : currFunctions) {
			cost = costFunctions.get(f).cost(cost);
		}
		return cost;
	}
	
	private int defaultCost(String cardName) {
		return CardData.cardInfo.get(cardName).getCost();
	}
	
	public void cleanup(GameState game, String playerName) {
		PlayerState player = getPlayer(game, playerName);
		TurnState turn = player.getTurn();
		if (turn.getCleanupActions().size() > 0) {
			actions.get(turn.getCleanupActions().get(0)).execute(game, playerName);
		} else {
			defaultCleanup(game, playerName);
		}
	}
	
	private void defaultCleanup(GameState game, String playerName) {
		PlayerState player = getPlayer(game, playerName);
		while (player.getHand().getCards().size() > 0) {
			defaultDiscard(game, playerName, player.getHand(), player.getHand().getCards().get(0));
		}
		while (player.getPlayed().getCards().size() > 0) {
			defaultDiscard(game, playerName, player.getPlayed());
		}
		player.bought().clear();
		for (int i=1; i<=5; i++) {
			defaultDraw(game, playerName);
		}
	}
		
	public void startAttack(GameState game, String targetName, String attack, String attacker) {
		PlayerState target = getPlayer(game, targetName);
		target.getAttacks().enqueue(new AttackState(attack, attacker));
		processAttack(game, targetName, attack, attacker, true);
	}
	
	public void processAttack(GameState game, String targetName, String attack, String attacker, boolean isAddingNewAttack) {
		PlayerState target = getPlayer(game, targetName);
 
		if ((target.getAttacks().size() == 1 && isAddingNewAttack) || (target.getAttacks().size() >= 1 && !isAddingNewAttack)) {
			List<String> reactions = target.getHand().attackReactions();		
			boolean isAttack = CardData.cardInfo.entrySet().stream().anyMatch((e) -> attack.equals(e.getValue().getAttackAction()));
			if (reactions.size() > 0 && isAttack) {
				reactions.add("No");
				ChoiceOptionCreator reactionChoices = (g, p) -> {
					return reactions;
				};
				createChoice(game, target, reactionChoices, 1, ATTACK_REACTION, "Do you want to play a reaction card?");
			} else {
				actions.get(attack).execute(game, targetName);
			}
		}
	}
	
	public void finishAttack(GameState game, String targetName) {
		PlayerState target = getPlayer(game, targetName);		
		target.getAttacks().dequeue();		
		if (target.getAttacks().size() > 0) {
			processAttack(game, targetName, target.getAttacks().peek().getAttack(), target.getAttacks().peek().getAttacker(), false);
		}
	}

	private List<String> getOtherPlayers(GameState game, String thisPlayer) {
		List<String> result = new ArrayList<>();
		game.getPlayers().keySet().stream().filter(n -> !n.equals(thisPlayer)).forEach(n -> result.add(n));
		return result;
	}
	
	private void addTreasure(TurnState turn, int amount) {
		turn.setTreasure(amount + turn.getTreasure());
	}
		
	public String moveCard(CardSource source, CardDestination destination) {
		String cardMoved = source.remove();
		destination.add(cardMoved);
		return cardMoved;
	}

	public void moveCard(CardSource source, CardDestination destination, String cardName) {
		String card = source.remove(cardName);
		destination.add(card);
	}
	
	public void startingHand(GameState game, String playerName) {
		PlayerState player = getPlayer(game, playerName);
		for (int i=1; i<=3; i++) {
			moveCard(game.getBank().getSupplies().get(ESTATE), player.getDeck());			
		}
		for (int i=1; i<=7; i++) {
			moveCard(game.getBank().getSupplies().get(COPPER), player.getDeck());
		}
		shuffle(player);
		for (int i=1; i<=5; i++) {
			defaultDraw(game, playerName);
		}
	}
	
	public Stream<String> getNonEmptyBankSuppliesStream(GameState game) {
		return game.getBank().getSupplies().keySet().stream().filter(c -> game.getBank().getSupplies().get(c).getCount() > 0).sorted(new BankComparator(CardData.cardInfo));
	}
	
	private PlayerState getPlayer(GameState game, String name) {
		return game.getPlayers().get(name);
	}
	
	
	
	@AllArgsConstructor
	public class BankComparator implements Comparator<String> {
		private Map<String, CardData> cardData;

		@Override
		public int compare(String o1, String o2) {
			return ((Integer) getOrderingInt(o1)).compareTo(getOrderingInt(o2));
		}
		
		private int getOrderingInt(String c) {
			switch(c) {
			case ActionService.GOLD:
			case ActionService.SILVER:
			case ActionService.COPPER:
				return cardData.get(c).getCost() * -1 - 10;
			case ActionService.ESTATE:
			case ActionService.DUCHY:
			case ActionService.PROVINCE:
				return cardData.get(c).getCost() + 20;
			case ActionService.CURSE:
				return 50;
			default:
				return cardData.get(c).getCost();
			}
		}
	}
	
	public static final String SMITHY = "Smithy";
	public static final String VILLAGE = "Village";
	public static final String LABORATORY = "Laboratory";
	public static final String MARKET = "Market";
	public static final String WOODCUTTER = "Woodcutter";
	public static final String REMODEL = "Remodel";
	public static final String REMODEL1 = "Remodel1";
	public static final String REMODEL2 = "Remodel2";
	public static final String MILITIA = "Militia";
	public static final String MILITIA1 = "Militia1";
	public static final String MILITIA2 = "Militia2";
	public static final String BUREAUCRAT = "Bureaucrat";
	public static final String BUREAUCRAT1 = "Bureaucrat1";
	public static final String BUREAUCRAT2 = "Bureaucrat2";
	public static final String VASSAL = "Vassal";
	public static final String VASSAL1 = "Vassal1";
	public static final String WEAVER = "Weaver";
	public static final String WEAVER1 = "Weaver1";
	public static final String WEAVER2 = "Weaver2";
	public static final String SCHEME = "Scheme";
	public static final String SCHEME1 = "Scheme1";
	public static final String SCHEME2 = "Scheme2";
	public static final String HAGGLER = "Haggler";
	public static final String HAGGLER1 = "Haggler1";
	public static final String HAGGLER2 = "Haggler2";
	public static final String GOLD = "Gold";
	public static final String SILVER = "Silver";
	public static final String COPPER = "Copper";
	public static final String CURSE = "Curse";
	public static final String ESTATE = "Estate";
	public static final String DUCHY = "Duchy";
	public static final String PROVINCE = "Province";
	public static final String ATTACK_REACTION = "defend";
	public static final String GUARD_DOG = "Guard Dog";
	public static final String OASIS = "Oasis";
	public static final String OASIS1 = "Oasis1";
	public static final String EMBASSY = "Embassy";
	public static final String EMBASSY1 = "Embassy1";
	public static final String BORDER_VILLAGE = "Border Village";
	public static final String BORDER_VILLAGE2 = "Border Village2";
	public static final String THRONE_ROOM = "Throne Room";
	public static final String THRONE_ROOM2 = "Throne Room2";
	public static final String TRAIL = "Trail";
	public static final String TRAIL1 = "Trail1";
	public static final String HIGHWAY = "Highway";
	public static final String TUNNEL = "Tunnel";
	public static final String TUNNEL1 = "Tunnel1";
	public static final String FOOLS_GOLD = "Fools Gold";
	public static final String FARMLAND = "Farmland";
	public static final String FARMLAND1 = "Farmland1";
	public static final String FARMLAND2 = "Farmland2";
	public static final String SPICE_MERCHANT = "Spice Merchant";
	public static final String SPICE_MERCHANT1 = "Spice Merchant1";
	public static final String SPICE_MERCHANT2 = "Spice Merchant2";
	public static final String STABLES = "Stables";
	public static final String STABLES1 = "Stables1";
	public static final String WHEELWRIGHT = "Wheelwright";
	public static final String WHEELWRIGHT1 = "Wheelwright1";
	public static final String WHEELWRIGHT2 = "Wheelwright2";
	public static final String SOUK = "Souk";
	public static final String SOUK1 = "Souk1";
	public static final String NOMADS = "Nomads";
	public static final String CACHE = "Cache";
	public static final String FESTIVAL = "Festival";
	public static final String ARTISAN = "Artisan";
	public static final String ARTISAN1 = "Artisan1";
	public static final String ARTISAN2 = "Artisan2";
	public static final String POACHER = "Poacher";
	public static final String POACHER1 = "Poacher1";
	public static final String MERCHANT = "Merchant";
	public static final String MERCHANT1 = "Merchant1";
	public static final String HARBINGER = "Harbinger";
	public static final String HARBINGER1 = "Harbinger1";
	public static final String CHAPEL = "Chapel";
	public static final String CHAPEL1 = "Chapel1";
	public static final String CHANCELLOR = "Chancellor";
	public static final String CHANCELLOR1 = "Chancellor1";
	public static final String MOAT = "Moat";
	public static final String MINE = "Mine";
	public static final String MINE1 = "Mine1";
	public static final String MINE2 = "Mine2";
	public static final String TRADER = "Trader";
	public static final String TRADER1 = "Trader1";
	public static final String TRADER2 = "Trader2";
	public static final String CELLAR = "Cellar";
	public static final String CELLAR1 = "Cellar1";
	public static final String WORKSHOP = "Workshop";
	public static final String WORKSHOP1 = "Workshop1";
	public static final String FEAST = "Feast";
	public static final String FEAST1 = "Feast1";
	public static final String MONEYLENDER = "Moneylender";
	public static final String MONEYLENDER1 = "Moneylender1";
	public static final String COUNCIL_ROOM = "Council Room";
	public static final String COUNCIL_ROOM1 = "Council Room1";
	public static final String LIBRARY = "Library";
	public static final String LIBRARY1 = "Library1";
	public static final String LIBRARY2 = "Library2";
	public static final String ILL_GOTTEN_GAINS = "Ill-Gotten Gains";
	public static final String ILL_GOTTEN_GAINS1 = "Ill-Gotten Gains1";
	public static final String INN = "Inn";
	public static final String INN1 = "Inn1";
	public static final String INN2 = "Inn2";
	public static final String JACK_OF_ALL_TRADES = "Jack of All Trades";
	public static final String JACK_OF_ALL_TRADES1 = "Jack of All Trades1";
	public static final String JACK_OF_ALL_TRADES2 = "Jack of All Trades2";
	public static final String MANDARIN = "Mandarin";
	public static final String MANDARIN1 = "Mandarin1";
	public static final String MARGRAVE = "Margrave";
	public static final String MARGRAVE1 = "Margrave1";
	public static final String MARGRAVE2 = "Margrave2";
	public static final String NOMAD_CAMP = "Nomad Camp";
	public static final String NOBLE_BRIGAND = "Noble Brigand";
	public static final String NOBLE_BRIGAND1 = "Noble Brigand1";
	public static final String NOBLE_BRIGAND2 = "Noble Brigand2";
	public static final String ADVENTURER = "Adventurer";
	public static final String ADVENTURER1 = "Adventurer1";
	public static final String WITCH = "Witch";
	public static final String WITCH1 = "Witch1";
	public static final String BERSERKER = "Berserker";
	public static final String BERSERKER1 = "Berserker1";
	public static final String BERSERKER2 = "Berserker2";
	public static final String BERSERKER3 = "Berserker3";
	public static final String CROSSROADS = "Crossroads";
	public static final String DEVELOP = "Develop";
	public static final String DEVELOP1 = "Develop1";
	public static final String DEVELOP2 = "Develop2";
	public static final String DEVELOP3 = "Develop3";
	public static final String SENTRY = "Sentry";
	public static final String SENTRY1 = "Sentry1";
	public static final String SENTRY2 = "Sentry2";
	public static final String WITCHS_HUT = "Witch's Hut";
	public static final String WITCHS_HUT1 = "Witch's Hut1";
	public static final String WITCHS_HUT2 = "Witch's Hut2";
	public static final String CARTOGRAPHER = "Cartographer";
	public static final String CARTOGRAPHER1 = "Cartographer1";
	public static final String ORACLE = "Oracle";
	public static final String ORACLE1 = "Oracle1";
	public static final String ORACLE2 = "Oracle2";
	public static final String ORACLE3 = "Oracle3";
	public static final String ORACLE4 = "Oracle4";
	public static final String BANDIT = "Bandit";
	public static final String BANDIT1 = "Bandit1";
	public static final String BANDIT2 = "Bandit2";
	public static final String BANDIT3 = "Bandit3";
	public static final String SPY = "Spy";
	public static final String SPY1 = "Spy1";
	public static final String SPY2 = "Spy2";
	public static final String SPY3 = "Spy3";
	public static final String CAULDRON = "Cauldron";
	public static final String CAULDRON1 = "Cauldron1";
	public static final String CAULDRON2 = "Cauldron2";
	public static final String DUCHESS = "Duchess";
	public static final String DUCHESS1 = "Duchess1";
	public static final String DUCHESS2 = "Duchess2";
	public static final String SILK_ROAD = "Silk Road";
	public static final String GARDENS = "Gardens";
}



