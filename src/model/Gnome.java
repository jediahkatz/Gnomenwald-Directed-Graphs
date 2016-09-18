package model;

import java.util.NoSuchElementException;

public class Gnome {
	//when not urgent, a gnome will take the cheapest path
	//when urgent, a gnome will take the shortest path
	private boolean isUrgent;
	private Village destination;
	private Stack<Road> route;
	private HoldsGnomes location;
	private int id;
	private int money;
	
	private Point secretPoint; //for authentication
	private static int nGnomes = 0;
	
	public Gnome(HoldsGnomes loc) {
		location = loc;
		id = nGnomes++;
		money = MyRandom.randInt(50,101);
		
		// 50-50 to be urgent or not
		isUrgent = MyRandom.randInt(2) == 0 ? false : true;
		
		newDestination();
		loc.addGnome(this);
	}
	
	public String getName() {
		return "Gnome " + id;
	}
	
	public int getID() {
		return id;
	}
	
	public void newDestination() {
		Village v;
		do { //re-pick if we're already there or it's marked for deletion
			v = GraphAlgorithms.chooseRandomVillage();
		} while(v == location || v.isMarkedForDeletion());
		chooseDestination(v);
	}
	
	private synchronized void chooseDestination(Village v) {
		if(route != null) {
			for(Road r : route) {
				//if we are switching routes early, make sure
				//to tell the roads that we would have passed
				//through that we are no longer passing through
				r.getDestination().subtractPasser();
			}
		}
		
		if(v == location) {
			newDestination();
			return;
		}
		destination = v;
		
		//if we are currently on a road (which should never be true)
		//then use the village at the end of the road
		Village loc = (location.getClass() == Road.class) ? ((Road) location).getDestination() : (Village) location;
		route = GraphAlgorithms.dijkstraAlgorithm(loc, destination, isUrgent);
		
		for(Road r : route) {
			//tell all the stops on our route that we will be passing through
			r.getDestination().addPasser();
		}
		
		System.out.println(getName() + ", currently at: " + loc.getID() + ", new destination: " + v.getID() + (isUrgent ? " (urgent)" : " (meandering)"));
		if(route.isEmpty()) {
			newDestination();
		}
	}
	
	public Village getDestination() {
		return destination;
	}
	
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void moveAround() {
		location.moveGnome(this);
		sleep(location.getSleepTime());
	}
	
	public Road peekRoad() throws NoSuchElementException {
		if(route.isEmpty()) {
			throw new NoSuchElementException();
		}
		return route.peek();
	}
	
	//the next road to take
	public Road nextRoad() throws NoSuchElementException {
		if(route.isEmpty()) {
			throw new NoSuchElementException();
		}
		return route.pop();
	}
	
	public synchronized void setLocation(HoldsGnomes loc) {
		location = loc;
	}
	
	public HoldsGnomes getLocation() {
		return location;
	}
	
	public void setSecretPoint(Point p) {
		secretPoint = p;
	}
	
	public Point requestSecretPoint() {
		//30% chance that the gnome gives the correct point
		//60% chance he refuses to give a point (returns null)
		//10% chance he gives an incorrect point
		double ok = MyRandom.random();
		if(ok < 0.1) { //incorrect point
			return new Point(MyRandom.randInt(1000), MyRandom.randInt(1000));
		} else if(ok < 0.7) {
			return null;
		} else {
			return secretPoint;
		}
	}
	
	//pay toll to current road
	public void payToll() {
		money -= ((Road) location).getCost();
	}
	
	//get payment from current village
	public void takePayment() {
		money += ((Village) location).getPayment();
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setDestination(Village d) {
		chooseDestination(d);
	}
}
