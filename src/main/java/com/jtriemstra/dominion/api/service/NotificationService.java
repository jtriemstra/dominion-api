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
	private List<String> notifications = new ArrayList<>();
	
	static {
		playNotifications.put(ActionService.VILLAGE, " for 1 card and 2 actions");
		//playNotifications.put(ActionService.ADVENTURER, "");
		playNotifications.put(ActionService.ARTISAN1, " and gains {}");
		playNotifications.put(ActionService.BANDIT, " and gains a gold.");
		playNotifications.put(ActionService.BANDIT2, " trashes {}");
		playNotifications.put(ActionService.BERSERKER, " and gains {}");
		playNotifications.put(ActionService.BORDER_VILLAGE, " for 1 card and 2 actions");
		playNotifications.put(ActionService.BORDER_VILLAGE2, " and gains {}");
		playNotifications.put(ActionService.BUREAUCRAT, ". All players put a victory card on their deck.");
		playNotifications.put(ActionService.CACHE, "");
		playNotifications.put(ActionService.CARTOGRAPHER, " for 1 card and 1 action");
		playNotifications.put(ActionService.CAULDRON, "");
		playNotifications.put(ActionService.CELLAR1, " discarding and drawing {} cards");
		playNotifications.put(ActionService.CHANCELLOR1, " for 2 money and {} put deck into discard");
		playNotifications.put(ActionService.CHAPEL1, " trashing {} cards");
		playNotifications.put(ActionService.COUNCIL_ROOM, " for 4 cards, and other players draw 1");
		playNotifications.put(ActionService.CROSSROADS, " for {} cards and {} actions");
		playNotifications.put(ActionService.DEVELOP, "");
		playNotifications.put(ActionService.DEVELOP1, " trashes {}");
		playNotifications.put(ActionService.DEVELOP2, " gains {}");
		playNotifications.put(ActionService.DEVELOP3, " gains {}");
		playNotifications.put(ActionService.DUCHESS, " for 2 money");
		//playNotifications.put(ActionService.DUCHESS1, "");
		playNotifications.put(ActionService.DUCHESS2, " gains a Duchess");
		playNotifications.put(ActionService.EMBASSY, " for 5 cards and discarding 3");
		playNotifications.put(ActionService.FEAST1, " and gains {}");
		playNotifications.put(ActionService.FESTIVAL, " for 2 actions, 1 buy, 2 money");
		playNotifications.put(ActionService.FOOLS_GOLD, " with value {}");
		playNotifications.put(ActionService.GUARD_DOG, " for {} cards");
		playNotifications.put(ActionService.HAGGLER, " for 2 money");
		playNotifications.put(ActionService.HAGGLER2, " and gains {}");
		playNotifications.put(ActionService.HARBINGER, " for 1 card and 1 action");
		playNotifications.put(ActionService.HIGHWAY, " for 1 card and 1 action");
		playNotifications.put(ActionService.ILL_GOTTEN_GAINS1, " and gains a copper");
		playNotifications.put(ActionService.INN, "");
		
		gainNotifications.put(ActionService.BERSERKER, " and plays it");
		gainNotifications.put(ActionService.CACHE, " and gains 2 coppers");
		gainNotifications.put(ActionService.EMBASSY, " and other players gain a silver");
		gainNotifications.put(ActionService.ILL_GOTTEN_GAINS, " and other players gain a curse");
	}
	
	public void notifyPlay(String playerName, String cardName) {
		String suffix = playNotifications.containsKey(cardName) ? playNotifications.get(cardName) : "";
		notifications.add(0, playerName + " plays " + cardName + suffix);
	}
	
	public List<String> getNotifications() {
		return notifications;
	}
	
	public void clearNotifications() {
		notifications.clear();
	}
}
