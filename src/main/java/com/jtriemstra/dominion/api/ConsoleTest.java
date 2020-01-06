package com.jtriemstra.dominion.api;

import java.util.Scanner;

import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

public class ConsoleTest {
	
	private static Game game = new Game();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner command = new Scanner(System.in);

		game.getPlayers().add(new Player());
		game.getPlayers().get(0).init(game);
		
	    
	    boolean running = true;

	    while(running){
	    	printState(game.getPlayers().get(0));
	    	
	    	System.out.println("Enter command: ");
	        switch(command.nextLine()){
	        case "play":
	        	System.out.println("Which card?");
	        	handlePlay(command);
	        	break;
	        case "buy":
	        	System.out.println("Which card?");
	        	handleBuy(command);
	        	break;	       
	        case "cleanup":
	        	game.getPlayers().get(0).cleanup();
	        	break;
	        case "exit":
	            System.out.println("Application Closed");
	            running = false;
	            break;

	        default:
	            System.out.println("Command not recognized!");
	            break;
	        }
	    }
	    command.close();
	}
	
	private static void handlePlay(Scanner command) {
		String card = command.nextLine();
		game.getPlayers().get(0).play(card);
	}
	
	private static void handleBuy(Scanner command) {
		String card = command.nextLine();
		game.getPlayers().get(0).buy(card);
	}
	
	private static void printState(Player player) {
		System.out.println("****************");
		System.out.println("");
		System.out.println("Deck has " + player.getDeck().size() + " cards");
		System.out.println("Hand has: ");
		for (Card c : player.getHand()) {
			System.out.println(c.toString());
		}
		System.out.println("Table has: ");
		for (Card c : player.getPlayed()) {
			System.out.println(c.toString());
		}
	}

}
