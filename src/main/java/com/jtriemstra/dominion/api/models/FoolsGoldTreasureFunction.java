package com.jtriemstra.dominion.api.models;

public class FoolsGoldTreasureFunction implements TreasureFunction{

	@Override
	public int getTreasure(Player p) {
		int numberFoolsGold=0;
		for(Card c : p.getPlayed()) {
			if (c.getName().equals("Fools Gold")) {
				numberFoolsGold++;
			}
		}
		return numberFoolsGold == 1 ? 1 : 4;
	}

}
