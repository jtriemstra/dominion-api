package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	private static Map<String, String> playNotifications = new HashMap<>();
	private static Map<String, String> gainNotifications = new HashMap<>();
	private static Map<String, String> defaultChoiceNotifications = new HashMap<>();
	private static Map<String, String> customChoiceNotifications = new HashMap<>();
	private List<String> notifications = new ArrayList<>();
	
	static {
		defaultChoiceNotifications.put(ActionService.ARTISAN1, " and gains %s");
		defaultChoiceNotifications.put(ActionService.BANDIT2, " trashes %s");
		defaultChoiceNotifications.put(ActionService.BERSERKER1, " and gains %s");
		defaultChoiceNotifications.put(ActionService.BORDER_VILLAGE2, " and gains %s");
		defaultChoiceNotifications.put(ActionService.CELLAR1, " discarding and drawing %s cards");
		defaultChoiceNotifications.put(ActionService.CHANCELLOR1, " for 2 money and %s put deck into discard");
		defaultChoiceNotifications.put(ActionService.CHAPEL1, " trashing %s cards");
		defaultChoiceNotifications.put(ActionService.DEVELOP1, " trashes %s");
		defaultChoiceNotifications.put(ActionService.DEVELOP2, " gains %s");
		defaultChoiceNotifications.put(ActionService.DEVELOP3, " gains %s");
		defaultChoiceNotifications.put(ActionService.DUCHESS2, " gains a Duchess");
		defaultChoiceNotifications.put(ActionService.FEAST1, " and gains %s");
		defaultChoiceNotifications.put(ActionService.HAGGLER2, " and gains %s");
		defaultChoiceNotifications.put(ActionService.MINE2, " and gains %s");
				
		customChoiceNotifications.put(ActionService.CROSSROADS, " for %s cards and %s actions");
		customChoiceNotifications.put(ActionService.FOOLS_GOLD, " with value %s");
		customChoiceNotifications.put(ActionService.GUARD_DOG, " for %s cards");
		customChoiceNotifications.put(ActionService.HARBINGER1, " puts a card on deck");	
		customChoiceNotifications.put(ActionService.ILL_GOTTEN_GAINS1, " gains a copper");
		customChoiceNotifications.put(ActionService.MONEYLENDER1, " trashes a copper");
		customChoiceNotifications.put(ActionService.REMODEL1, " trashes %s");
		customChoiceNotifications.put(ActionService.SPICE_MERCHANT2, " for %s");
		customChoiceNotifications.put(ActionService.STABLES1, " gets 3 cards and 1 action");
		customChoiceNotifications.put(ActionService.WEAVER1, " gains 2 silver");
		
		//playNotifications.put(ActionService.ADVENTURER, "");
		
		playNotifications.put(ActionService.BANDIT, " and gains a gold.");
		playNotifications.put(ActionService.BORDER_VILLAGE, " for 1 card and 2 actions");
		playNotifications.put(ActionService.BUREAUCRAT, ". All players put a victory card on their deck.");
		playNotifications.put(ActionService.CACHE, "");
		playNotifications.put(ActionService.CARTOGRAPHER, " for 1 card and 1 action");
		playNotifications.put(ActionService.CAULDRON, "");
		playNotifications.put(ActionService.COUNCIL_ROOM, " for 4 cards, and other players draw 1");
		
		playNotifications.put(ActionService.DEVELOP, "");
		playNotifications.put(ActionService.DUCHESS, " for 2 money");
		//playNotifications.put(ActionService.DUCHESS1, "");
		playNotifications.put(ActionService.EMBASSY, " for 5 cards and discarding 3");
		playNotifications.put(ActionService.FESTIVAL, " for 2 actions, 1 buy, 2 money");

		playNotifications.put(ActionService.HAGGLER, " for 2 money");
		playNotifications.put(ActionService.HARBINGER, " for 1 card and 1 action");
		playNotifications.put(ActionService.HIGHWAY, " for 1 card and 1 action, and a discount");
		playNotifications.put(ActionService.INN, " for 2 cards and 2 actions");
		playNotifications.put(ActionService.JACK_OF_ALL_TRADES, " for a Silver and stuff");
		playNotifications.put(ActionService.LABORATORY, " for 2 cards and 1 actions");
		playNotifications.put(ActionService.LIBRARY, " to draw");
		playNotifications.put(ActionService.MANDARIN, " for 3 money and puts a card on the deck");
		playNotifications.put(ActionService.MARGRAVE, " for 3 cards, 1 buy, and other players draw and discard down to 3");
		playNotifications.put(ActionService.MARKET, " for 1 card, 1 action, 1 buy, 1 money");
		playNotifications.put(ActionService.MERCHANT, " for 1 card, 1 action and maybe 1 money");
		playNotifications.put(ActionService.MILITIA, " 2 money, and other players discard down to 3");
		playNotifications.put(ActionService.MOAT, " for 2 cards");
		playNotifications.put(ActionService.NOMAD_CAMP, " for 1 buy and 2 money");
		playNotifications.put(ActionService.NOMADS, " for 1 buy and 2 money");
		playNotifications.put(ActionService.OASIS, " for 1 card, 1 action, 1 money");
		playNotifications.put(ActionService.POACHER, " for 1 card, 1 action, 1 money");
		playNotifications.put(ActionService.SCHEME, " for 1 card, 1 action");
		playNotifications.put(ActionService.SENTRY, " for 1 card, 1 action");
		playNotifications.put(ActionService.SMITHY, " for 3 cards");
		playNotifications.put(ActionService.SOUK, " for 1 buy and money");
		playNotifications.put(ActionService.SPY, " for 1 card, 1 action");
		playNotifications.put(ActionService.TRAIL, " for 1 card, 1 action");
		playNotifications.put(ActionService.VASSAL, " for 2 money");
		playNotifications.put(ActionService.VILLAGE, " for 1 card and 2 actions");
		playNotifications.put(ActionService.WHEELWRIGHT, " for 1 card, 1 action");
		playNotifications.put(ActionService.WITCH, " for 2 cards, and other players gain a curse");
		playNotifications.put(ActionService.WITCHS_HUT, " for 4 cards");
		playNotifications.put(ActionService.WOODCUTTER, " for 1 buy and 2 money");
		
		gainNotifications.put(ActionService.CACHE, " and gains 2 coppers");
		gainNotifications.put(ActionService.EMBASSY, " and other players gain a silver");
		gainNotifications.put(ActionService.ILL_GOTTEN_GAINS, " and other players gain a curse");
	}
	
	public void notifyPlay(String playerName, String cardName) {
		if (ActionService.GOLD.equals(cardName) || ActionService.SILVER.equals(cardName) || ActionService.COPPER.equals(cardName)) {
			return;
		}
		String suffix = playNotifications.containsKey(cardName) ? playNotifications.get(cardName) : "";
		notifications.add(0, playerName + " plays " + cardName + suffix);
	}
	
	public void notifyChoice(String playerName, String actionKey, Object... params) {
		if (defaultChoiceNotifications.containsKey(actionKey)) {
			String suffix = String.format(defaultChoiceNotifications.get(actionKey), params);
			notifications.add(0, playerName + suffix);	
		} else if (customChoiceNotifications.containsKey(actionKey)) {
			String suffix = String.format(customChoiceNotifications.get(actionKey), params);
			notifications.add(0, playerName + suffix);
		}
	}
	
	public void notifyGain(String playerName, String cardName) {
		String suffix = gainNotifications.containsKey(cardName) ? gainNotifications.get(cardName) : "";
		notifications.add(0, playerName + " gains " + cardName + suffix);
	}
	
	public void reveal(String playerName, List<String> cards) {
		notifications.add(0, playerName + " reveals " + String.join(", ", cards));
	}
	
	public List<String> getNotifications() {
		return notifications;
	}
	
	public void clearNotifications() {
		notifications.clear();
	}
}
