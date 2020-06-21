package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProvinceGainAction extends CardAction {
	@Override
	public void execute(Player player) {
		for(Player p : player.getGame().getOtherPlayers(player)) {
			(new FoolsGoldAction(player.getGame().getBank())).execute(p);
		}			
	}
}
