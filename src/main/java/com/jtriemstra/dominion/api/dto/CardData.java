package com.jtriemstra.dominion.api.dto;

import java.util.HashMap;
import java.util.Map;

import com.jtriemstra.dominion.api.service.ActionService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardData {
	private String name;
	private int cost;
	private boolean victory;
	private boolean action;
	private boolean attackReaction;
	private boolean treasure;
	private String attackAction;
	// types
	
	public static Map<String, CardData> cardInfo = new HashMap<>();
	
	static {
		cardInfo.put(ActionService.COPPER, new CardDataBuilder().name("Copper").cost(0).treasure(true).build());
		cardInfo.put(ActionService.SILVER, new CardDataBuilder().name("Silver").cost(3).treasure(true).build());
		cardInfo.put(ActionService.GOLD, new CardDataBuilder().name("Gold").cost(6).treasure(true).build());
		cardInfo.put(ActionService.CURSE, new CardDataBuilder().name("Curse").cost(0).victory(true).build());
		cardInfo.put(ActionService.PROVINCE, new CardDataBuilder().name("Province").cost(8).victory(true).build());
		cardInfo.put(ActionService.DUCHY, new CardDataBuilder().name("Duchy").cost(5).victory(true).build());
		cardInfo.put(ActionService.ESTATE, new CardDataBuilder().name("Estate").cost(2).victory(true).build());
		cardInfo.put(ActionService.VILLAGE, new CardDataBuilder().name("Village").cost(3).action(true).build());
		cardInfo.put(ActionService.SMITHY, new CardDataBuilder().name("Smithy").cost(4).action(true).build());
		cardInfo.put(ActionService.REMODEL, new CardDataBuilder().name("Remodel").cost(4).action(true).build());
		cardInfo.put(ActionService.MILITIA, new CardDataBuilder().name("Militia").cost(4).action(true).attackAction(ActionService.MILITIA2).build());
		cardInfo.put(ActionService.BUREAUCRAT, new CardDataBuilder().name("Bureaucrat").cost(4).action(true).attackAction(ActionService.BUREAUCRAT2).build());
		cardInfo.put(ActionService.SCHEME, new CardDataBuilder().name("Scheme").cost(3).action(true).build());
		cardInfo.put(ActionService.VASSAL, new CardDataBuilder().name("Vassal").cost(3).action(true).build());
		cardInfo.put(ActionService.HAGGLER, new CardDataBuilder().name("Hagggler").cost(4).action(true).build());
		cardInfo.put(ActionService.WEAVER, new CardDataBuilder().name("Weaver").cost(5).action(true).build());
		cardInfo.put(ActionService.MOAT, new CardDataBuilder().name("Moat").cost(2).action(true).attackReaction(true).build());
		cardInfo.put(ActionService.GUARD_DOG, new CardDataBuilder().name("Guard Dog").cost(3).action(true).attackReaction(true).build());
		cardInfo.put(ActionService.OASIS, new CardDataBuilder().name("Oasis").cost(3).action(true).build());
		cardInfo.put(ActionService.EMBASSY, new CardDataBuilder().name("Embassy").cost(5).action(true).build());
		cardInfo.put(ActionService.BORDER_VILLAGE, new CardDataBuilder().name("Border Village").cost(6).action(true).build());
		cardInfo.put(ActionService.TRAIL, new CardDataBuilder().name("Trail").cost(4).action(true).build());
		cardInfo.put(ActionService.HIGHWAY, new CardDataBuilder().name("Highway").cost(5).action(true).build());
		cardInfo.put(ActionService.TUNNEL, new CardDataBuilder().name("Tunnel").cost(3).victory(true).build());
		cardInfo.put(ActionService.FOOLS_GOLD, new CardDataBuilder().name("Fools Gold").treasure(true).cost(2).build());
		cardInfo.put(ActionService.FARMLAND, new CardDataBuilder().name("Farmland").cost(6).victory(true).build());
		cardInfo.put(ActionService.SPICE_MERCHANT, new CardDataBuilder().name("Spice Merchant").cost(4).action(true).build());
		cardInfo.put(ActionService.NOMADS, new CardDataBuilder().name("Nomads").cost(4).action(true).build());
		cardInfo.put(ActionService.SOUK, new CardDataBuilder().name("Souk").cost(5).action(true).build());
		cardInfo.put(ActionService.WHEELWRIGHT, new CardDataBuilder().name("Wheelwright").cost(5).action(true).build());
		cardInfo.put(ActionService.STABLES, new CardDataBuilder().name("Stables").cost(5).action(true).build());
		cardInfo.put(ActionService.CACHE, new CardDataBuilder().name("Cache").treasure(true).cost(5).build());
		cardInfo.put(ActionService.FESTIVAL, new CardDataBuilder().name("Festival").action(true).cost(5).build());
		cardInfo.put(ActionService.WOODCUTTER, new CardDataBuilder().name("Woodcutter").action(true).cost(3).build());
		cardInfo.put(ActionService.MARKET, new CardDataBuilder().name("Market").action(true).cost(5).build());
		cardInfo.put(ActionService.LABORATORY, new CardDataBuilder().name("Laboratory").action(true).cost(5).build());
		cardInfo.put(ActionService.THRONE_ROOM, new CardDataBuilder().name("Throne Room").action(true).cost(4).build());
		cardInfo.put(ActionService.ARTISAN, new CardDataBuilder().name("Artisan").action(true).cost(6).build());
		cardInfo.put(ActionService.POACHER, new CardDataBuilder().name("Poacher").action(true).cost(4).build());
		cardInfo.put(ActionService.MERCHANT, new CardDataBuilder().name("Merchant").action(true).cost(3).build());
		cardInfo.put(ActionService.HARBINGER, new CardDataBuilder().name("Harbinger").action(true).cost(3).build());
		cardInfo.put(ActionService.CHAPEL, new CardDataBuilder().name("Chapel").action(true).cost(2).build());
		cardInfo.put(ActionService.CHANCELLOR, new CardDataBuilder().name("Chancellor").action(true).cost(3).build());
		cardInfo.put(ActionService.MINE, new CardDataBuilder().name("Mine").action(true).cost(5).build());
		cardInfo.put(ActionService.TRADER, new CardDataBuilder().name("Trader").action(true).cost(4).build());
		cardInfo.put(ActionService.CELLAR, new CardDataBuilder().name("Cellar").action(true).cost(2).build());
		cardInfo.put(ActionService.WORKSHOP, new CardDataBuilder().name("Workshop").action(true).cost(3).build());
		cardInfo.put(ActionService.FEAST, new CardDataBuilder().name("Feast").action(true).cost(4).build());
		cardInfo.put(ActionService.LIBRARY, new CardDataBuilder().name("Library").action(true).cost(5).build());
		cardInfo.put(ActionService.COUNCIL_ROOM, new CardDataBuilder().name("Council Room").action(true).cost(5).build());
		cardInfo.put(ActionService.MONEYLENDER, new CardDataBuilder().name("Moneylender").action(true).cost(4).build());
		cardInfo.put(ActionService.ILL_GOTTEN_GAINS, new CardDataBuilder().name("Ill-Gotten Gains").treasure(true).cost(5).build());
		cardInfo.put(ActionService.INN, new CardDataBuilder().name("Inn").action(true).cost(5).build());
		cardInfo.put(ActionService.JACK_OF_ALL_TRADES, new CardDataBuilder().name("Jack of All Trades").action(true).cost(4).build());
		cardInfo.put(ActionService.MANDARIN, new CardDataBuilder().name("Mandarin").action(true).cost(5).build());
		cardInfo.put(ActionService.MARGRAVE, new CardDataBuilder().name("Margrave").action(true).attackAction(ActionService.MARGRAVE1).cost(5).build());
		cardInfo.put(ActionService.NOMAD_CAMP, new CardDataBuilder().name("Nomad Camp").action(true).cost(4).build());
		cardInfo.put(ActionService.NOBLE_BRIGAND, new CardDataBuilder().name("Noble Brigand").action(true).cost(4).build());
		cardInfo.put(ActionService.ADVENTURER, new CardDataBuilder().name("Adventurer").action(true).cost(6).build());
		cardInfo.put(ActionService.WITCH, new CardDataBuilder().name("Witch").action(true).attackAction(ActionService.WITCH1).cost(5).build());
		cardInfo.put(ActionService.BERSERKER, new CardDataBuilder().name("Berserker").action(true).attackAction(ActionService.BERSERKER2).cost(5).build());
		cardInfo.put(ActionService.CROSSROADS, new CardDataBuilder().name("Crossroads").action(true).cost(2).build());
		cardInfo.put(ActionService.DEVELOP, new CardDataBuilder().name("Develop").action(true).cost(3).build());
		cardInfo.put(ActionService.SENTRY, new CardDataBuilder().name("Sentry").action(true).cost(5).build());
		cardInfo.put(ActionService.WITCHS_HUT, new CardDataBuilder().name("Witch's Hut").action(true).cost(5).build());
		cardInfo.put(ActionService.CARTOGRAPHER, new CardDataBuilder().name("Cartographer").action(true).cost(5).build());
		cardInfo.put(ActionService.ORACLE, new CardDataBuilder().name("Oracle").action(true).cost(3).build());
		cardInfo.put(ActionService.BANDIT, new CardDataBuilder().name("Bandit").action(true).attackAction(ActionService.BANDIT1).cost(5).build());
		cardInfo.put(ActionService.SPY, new CardDataBuilder().name("Spy").action(true).attackAction(ActionService.SPY2).cost(4).build());
		cardInfo.put(ActionService.CAULDRON, new CardDataBuilder().name("Cauldron").treasure(true).cost(5).build());
		cardInfo.put(ActionService.GARDENS, new CardDataBuilder().name("Gardens").cost(4).victory(true).build());
		cardInfo.put(ActionService.SILK_ROAD, new CardDataBuilder().name("Silk Road").cost(4).victory(true).build());
		cardInfo.put(ActionService.DUCHESS, new CardDataBuilder().name("Duchess").cost(2).treasure(true).build());
	}
}
