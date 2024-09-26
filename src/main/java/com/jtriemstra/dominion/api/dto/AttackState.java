package com.jtriemstra.dominion.api.dto;

import lombok.Data;

@Data
public class AttackState {
	private String attack;
	private String attacker;
	
	public AttackState(String attack, String attacker) {
		this.attack = attack;
		this.attacker = attacker;
	}
}
