package model;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Village implements HoldsGnomes, Comparable<Village> {
	private final int id;
	private HashSet<Gnome> gnomes = new HashSet<Gnome>();
	private int maxCapacity;
	private Queue<Road> outboundRoads = new Queue<Road>();
	private Queue<Road> inboundRoads = new Queue<Road>();
	private int payment; //how much gnomes are paid for transporting goods
	
	private boolean markedForDeletion = false;
	private int passingThroughHere = 0; //number of gnomes whose route passes this village
	
	public Village(int id) {
		this.id = id;
		maxCapacity = MyRandom.randInt(2,6); //random capacity in range [2, 6)
		payment = MyRandom.randInt(20, 31);
	}
	
	public int outdegree() {
		return outboundRoads.size();
	}
	
	public int indegree() {
		return inboundRoads.size();
	}

	public Queue<Road> getOutboundRoads() {
		return outboundRoads;
	}

	
	public Queue<Road> getInboundRoads() {
		return inboundRoads;
	}

	
	public synchronized void addOutboundRoad(Road r) {
		outboundRoads.add(r);
	}

	
	public synchronized void addInboundRoad(Road r) {
		inboundRoads.add(r);
	}

	public synchronized Road removeOutboundRoad(Road r) {
		return outboundRoads.remove(r);
	}

	public synchronized Road removeInboundRoad(Road r) {
		return inboundRoads.remove(r);
	}

	public int getID() {
		return id;
	}

	@Override
	public HashSet<Gnome> getGnomes() {
		return gnomes;
	}

	//pretty sure this should NOT be synchronized - that will cause
	//deadlocks, and we only really need adding and removing to be atomic
	
	//moves a gnome in the village to its next road (if not full)
	@Override
	public void moveGnome(Gnome g) {
		Road next = null;
		try {
			//note: I know some dislike breaking out early from a function
			//but at least in this case I prefer it to wrapping this
			//entire method in a huge if block
			
			if(g.peekRoad().isFull()) return; //if full, just wait
			
			next = g.nextRoad();
		} catch(NoSuchElementException e) {
			//if we have reached the destination (stack empty), choose a new one
			g.takePayment(); //the village pays the gnome for transporting goods
			g.newDestination();
			
			next = g.nextRoad();
		}
		
		boolean moved = false;
		for(Road r : outboundRoads) {
			if(!moved && next == r) {
				moved = true;
				System.out.println(g.getName() + " moved from " + id + " to " + r.getName());

				removeGnome(g);
				r.addGnome(g);
				g.payToll(); //pay the toll
				
				subtractPasser();
			}
		}
		
		//pretty sure this should never happen
		if(!moved) throw new IllegalArgumentException();
	}

	@Override
	public int compareTo(Village o) {
		//just compares village IDs
		return Integer.compare(id, o.id);
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

	@Override
	public int getSleepTime() {
		return 1000;
	}

	@Override
	public String getName() {
		return "Village " + getID();
	}
	
	public boolean isFull() {
		return gnomes.size() >= maxCapacity;
	}

	@Override
	public int getCapacity() {
		return maxCapacity;
	}
	
	public int getPayment() {
		return payment;
	}
	
	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}
	
	public void markForDeletion() {
		markedForDeletion = true;
	}
	
	public synchronized void addPasser() {
		passingThroughHere++;
	}
	
	public synchronized void subtractPasser() {
		passingThroughHere--;
	}
	
	//can only be deleted if it has been marked for deletion
	//and no gnomes will be passing through this village later
	public boolean readyForDeletion() {
		return isMarkedForDeletion() && passingThroughHere <= 0 && gnomes.size() == 0;
	}
}
