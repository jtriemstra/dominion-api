package com.jtriemstra.dominion.api.models;

public class CouncilRoomAction extends CardAction {
	@Override
	public void execute(Player player) {
		for(Player p : player.getGame().getOtherPlayers(player)) {
			p.draw();
		}
	}
}
