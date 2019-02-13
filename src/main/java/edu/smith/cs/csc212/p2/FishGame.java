package edu.smith.cs.csc212.p2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class manages our model of gameplay: missing and found fish, etc.
 * @author jfoley
 *
 */
public class FishGame {
	/**
	 * This is the world in which the fish are missing. (It's mostly a List!).
	 */
	World world;
	/**
	 * The player (a Fish.COLORS[0]-colored fish) goes seeking their friends.
	 */
	Fish player;
	/**
	 * The home location.
	 */
	FishHome home;
	/**
	 * These are the missing fish!
	 */
	List<Fish> missing;
	
	/**
	 * These are fish we've found!
	 */
	List<Fish> found;
	
	// fish that have been taken home
	List<Fish> homeList;
	/**
	 * Number of steps!
	 */
	int stepsTaken;
	
	int stepsLeft = 20;
	/**
	 * Score!
	 */
	int score;
	
	List<Fish> winGame;
	
	//public int rockNum;
	
	//Snail snail;
	/**
	 * Create a FishGame of a particular size.
	 * @param w how wide is the grid?
	 * @param h how tall is the grid?
	 */
	public FishGame(int w, int h) {
		world = new World(w, h);
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		homeList = new ArrayList<Fish>();
		winGame = new ArrayList<Fish>();
		// Add a home!
		home = world.insertFishHome();
		
		//Random rockRand = new Random();
		//rockNum = world.rand.nextInt(15) + 5;
		for (int i=0; i<(world.rand.nextInt(15) + 5-6); i++) {
			//List<Rock> rocks = new ArrayList<>();
			world.insertRockRandomly();
			//rocks.add(world.insertRockRandomly());
		}
		for (int i=0; i < 6; i++) {
			world.fallingRock();
			world.insertBubbleRandomly();
		}
		
		//snail = new Snail(world);
		world.insertSnailRandomly();
		world.insertSnailRandomly();
		
		
		/*
		 * for (int i =0; stepsTaken < 200; i+=stepsTaken) { world.insertFoodRandomly();
		 * }
		 */
		
		// Make the player out of the 0th fish color.
		player = new Fish(0, world);
		// Start the player at "home".
		player.setPosition(home.getX(), home.getY());
		player.markAsPlayer();
		world.register(player);
		
		// Generate fish of all the colors but the first into the "missing" List.
		for (int ft = 1; ft < Fish.COLORS.length; ft++) {
			Fish friend = world.insertFishRandomly(ft);
			missing.add(friend);
			winGame.add(friend);
		}
	}
	
	
	/**
	 * How we tell if the game is over: if missingFishLeft() == 0.
	 * @return the size of the missing list.
	 */
	public int missingFishLeft() {
		return missing.size();
	}
	
	/**
	 * This method is how the PlayFish app tells whether we're done.
	 * @return true if the player has won (or maybe lost?).
	 */
	public boolean gameOver() {

		// TODO(P2) We want to bring the fish home before we win!
		return winGame.isEmpty();
		//return missing.isEmpty();
	}

	/**
	 * Update positions of everything (the user has just pressed a button).
	 */
	public void step() {
		// Keep track of how long the game has run.
		this.stepsTaken += 1;
		if (this.stepsLeft == 10) {
			world.insertFoodRandomly();
		}
				
		// These are all the objects in the world in the same cell as the player.
		List<WorldObject> overlap = this.player.findSameCell();
		List<WorldObject> stayOver = this.home.findSameCell();
		// The player is there, too, let's skip them.
		overlap.remove(this.player);
		// If we find a fish, remove it from missing.
		
		 for (WorldObject wo : stayOver) { 
			 if (missing.contains(wo)) {
				 Fish f = (Fish) wo;
				 homeList.add(f);
				 winGame.remove(f);
				 world.remove(f); }
			 }
		for (WorldObject wo : overlap) {
			// It is missing if it's in our missing list.
			if (missing.contains(wo)) {
				// Remove this fish from the missing list.
				Fish f = (Fish) wo;
				missing.remove(f);
				
				// Remove from world.
					//world.remove(f);
				found.add(f);
				
				
				World.objectsFollow(player, found);
	
				// Increase score when you find a fish!
				
				  Map<Color,Integer> scoring = new HashMap<>(); 
				  scoring.put(Color.red, 0);
				  scoring.put(Color.green, 10); 
				  scoring.put(Color.yellow, 10);
				  scoring.put(Color.orange, 20); 
				  scoring.put(Color.gray, 10);
				  scoring.put(Color.magenta, 15); 
				  scoring.put(Color.cyan, 20);
				  scoring.put(Color.black, 15);
				  scoring.put(Color.pink, 15);
				 
				Integer fishScore = scoring.get(f.getColor());
				score += fishScore;
			}
			
		else if (wo == home) {
			//Fish fishy = (Fish) wo;
					reachHome();
					
		}
		else if (wo instanceof FishFood) {
			//FishFood tasty = (FishFood) wo;
			//treats.add(tasty);
			world.remove(wo);
			score += 5;
		}
		else if (wo instanceof Bubble) {
			
			world.canSwim(player, player.getX(), player.getY());
		}
			}
		// Make sure missing fish *do* something.
		if (homeList.size() > missingFishLeft()) {
			fastScared();}
		else if (homeList.size() <= missingFishLeft()) {
			wanderMissingFish();
		}
		// When fish get added to "found" they will follow the player around.
		World.objectsFollow(player, found);
		
		// Step any world-objects that run themselves.
		//world.fallingRock();
		losingFish();
		world.stepAll();
	}
	/**
	 * Call moveRandomly() on all of the missing fish to make them seem alive.
	 */
	private void losingFish() {
		if (found.size() != 0) {
			stepsLeft -= 1;
			if (stepsLeft == 0) {
				Fish sadFishie = found.get(found.size()-1);
				missing.add(sadFishie);
				found.remove(sadFishie);
				stepsLeft = 20; }
			System.out.println(stepsLeft);
			}
		else if (found.size() == 0) {
			stepsLeft = 20;
		}
	}
	
	// sets up index for missing
	int i = 0;
	private void wanderMissingFish() {
		Random rand = ThreadLocalRandom.current();
		for (Fish lost : missing) {
			feeding();
			}
			// 30% of the time, lost fish move randomly.
			if (rand.nextDouble() < 0.3) {
				// TODO(lab): What goes here? finished but not sure yet
				for (i=0; i < missingFishLeft(); i++) {
					missing.get(i).moveRandomly();
					
				}
			}
		}
	
	public void reachHome() {
		Integer i = 0;
		//for (i = 0; i<this.found.size(); i++) {
		for (Fish fish : this.found) {
			//Fish fish = found.get(i);
			//System.out.println(fish);
				homeList.add(fish);
				winGame.remove(fish);
				world.remove(fish);
			}
			this.found.removeAll(homeList);
			}
	
	private void fastScared() {
			Random rand = ThreadLocalRandom.current();
			for (Fish lost : missing) {
				feeding();
				if (rand.nextDouble() < .8) {
					for (i=0; i < missing.size(); i++) {
						
						missing.get(i).moveRandomly();
		}
		
				}
			}
		}
	public void feeding() {
		for (Fish lost : missing) {
		List<WorldObject> inSpot = lost.findSameCell();
		for (WorldObject wo : inSpot) {
			if (wo instanceof FishFood) {
				world.remove(wo);
			}}}
	}

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		// (P2) use this print to debug your World.canSwim changes!
		System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		List<WorldObject> atPoint = world.find(x, y);
		System.out.println(atPoint);
		for (WorldObject item : atPoint) {
			if (item instanceof Rock) {
				world.remove(item);
			}
			else if (item instanceof Bubble ) {
				world.remove(item);
			}
		}
		// (P2) allow the user to click and remove rocks.

	}
	
}
