package model;

import java.util.HashSet;

//a one way road
public class Road implements HoldsGnomes {
	private Village destination; //the village that this road leads to
	private Village origin; //the village that this road comes from
	private int length; //the length of this road - determines travel time
	private int cost; //the toll cost to take this road
	private int maxCapacity;
	private String name;
	private HashSet<Gnome> gnomes = new HashSet<Gnome>();
	
	private static int nRoads = 0;
	
	public Road(Village comingFrom, Village goingTo) {
		origin = comingFrom;
		destination = goingTo;
		
		//random cost, length, capacity
		length = MyRandom.randInt(1,10);
		cost = MyRandom.randInt(5,10);
		maxCapacity = MyRandom.randInt(4, 8);
		
		name = "Road " + (nRoads++) + " (" + length + "s/$" + cost + ")";
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setCost(int c) {
		cost = c;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
		
	}
	
	public Village getDestination() {
		return destination;
	}
	
	public void setDestination(Village d) {
		destination = (Village) d;
		
	}
	
	public Village getOrigin() {
		return origin;
	}
	
	public void setOrigin(Village o) {
		origin = (Village) o;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() == this.getClass()) {
			return equals((Road) o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (destination.hashCode()+origin.hashCode());
	}
	
	public boolean equals(Road e) {
		return (origin == e.getOrigin() && destination == e.getDestination());
	}

	@Override
	public HashSet<Gnome> getGnomes() {
		return gnomes;
	}

	//see comment above moveGnome method in village
	//moves a gnome in the road onto the next village (if not full)
	@Override
	public void moveGnome(Gnome g) {
		if(!destination.isFull()) { //if it's full, just wait
			System.out.println(g.getName() + " moved from " + name + " to " + destination.getID());
			removeGnome(g);
			destination.addGnome(g);
		}
	}

	@Override
	public synchronized void addGnome(Gnome g) {
		gnomes.add(g);
		g.setLocation(this);
	}

	@Override
	public synchronized Gnome removeGnome(Gnome g) {
		gnomes.remove(g);
		return g;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int getSleepTime() {
		return length*1000;
	}
	
	public boolean isFull() {
		return gnomes.size() >= maxCapacity;
	}

	@Override
	public int getCapacity() {
		return maxCapacity;
	}
}
