package com.jtriemstra.dominion.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.BankSupply;

@Service
public class BankService {
	public BankState createRandom() {
		throw new RuntimeException("must specify cards");
	}
	
	public BankState create(List<String> cardNames) {
		BankState bank = createEmpty();
		addDefault(bank);
		for (String s : cardNames) {
			bank.getSupplies().put(s, new BankSupply(10, s));
		}
		
		return bank;
	}
	
	private void addDefault(BankState bank) {
		bank.getSupplies().put(ActionService.GOLD, new BankSupply(30, ActionService.GOLD));
		bank.getSupplies().put(ActionService.SILVER, new BankSupply(40, ActionService.SILVER));
		bank.getSupplies().put(ActionService.COPPER, new BankSupply(60, ActionService.COPPER));
		bank.getSupplies().put(ActionService.CURSE, new BankSupply(30, ActionService.CURSE));
		bank.getSupplies().put(ActionService.ESTATE, new BankSupply(20, ActionService.ESTATE));
		bank.getSupplies().put(ActionService.DUCHY, new BankSupply(12, ActionService.DUCHY));
		bank.getSupplies().put(ActionService.PROVINCE, new BankSupply(12, ActionService.PROVINCE));
	}
	
	private BankState createEmpty() {
		return new BankState();
	}
	
}
