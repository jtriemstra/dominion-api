package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuchyGainAction extends CardAction {
	@Override
	public void execute(Player player) {
		if (player.getGame().getBank().hasCard("Duchess")) {
			(new DuchessGainAction(player.getGame().getBank())).execute(player);
		}
	}
}
