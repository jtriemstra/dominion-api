package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AttacksState {
	private List<AttackState> attacks;
	
	public AttacksState() {
		attacks = new ArrayList<>();
	}
	
	public void enqueue(AttackState in) {
		attacks.add(0, in);
	}
	
	public AttackState dequeue() {
		return attacks.remove(0);
	}
	
	public AttackState peek() {
		return attacks.get(0);
	}
	
	public int size() {
		return attacks.size();
	}
}
