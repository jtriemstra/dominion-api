package com.jtriemstra.dominion.api.models;

public class BureaucratAction extends CardAction {
	private Bank bank;
	
	public BureaucratAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.getDeck().add(0, bank.getByName("Silver"));
		
		for(Player p : player.getGame().getOtherPlayers(player)) {
			if (!p.hasCard("Moat")) {
				for (Card c : p.getHand()) {
					if (c.getType() == Card.CardType.VICTORY) {
						p.getHand().remove(c);
						p.getDeck().add(0, c);
						break;
					}
				}
			}
		}
	}

}
