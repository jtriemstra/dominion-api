package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchemeAction extends CardAction {
	
	@Override
	public void execute(Player player) {
		player.addCleanupStage(0, new SchemeCleanupAction());
	}

}
