package model;

import java.util.HashSet;
import java.util.NoSuchElementException;

//directed graph of villages and roads
public class DirectedGraph {
	private int nVillages = 0;
	private int nRoads = 0;
	private VillageIndexMap map; //maps Village IDs to Villages
	private Queue<Road>[] adj; //adjacency list for each Village
	private Queue<Village> villages; //list of all villages
	private HashSet<Road> roads = new HashSet<Road>(); //to make sure we don't add duplicate roads
	
	@SuppressWarnings("unchecked")
	public DirectedGraph(int n) {
		nVillages = n;

		map = new VillageIndexMap(n);
		adj = (Queue<Road>[]) new Queue[n];
		for(int i=0; i<adj.length; i++) {
			adj[i] = new Queue<Road>();
		}
		
		villages = new Queue<Village>();
		
		buildVillages();
	}
	
	public Village getVillage(int n) {
		return map.getVillage(n);
	}
	
	public Village getRandomVillage() {
		return map.getRandomVillage();
	}
	
	private void buildVillages() {
		for(int i=0; i<nVillages; i++) {
			Village v = new Village(i);
			map.setVillage(v, i);
			villages.add(v);
		}
	}
	
	//check if we are able to add this road
	public boolean canAdd(Road r) {
		//can't add duplicates or loop roads
		if(!roads.contains(r) && r.getOrigin() != r.getDestination()) {
			Village origin = r.getOrigin();
			Village dest = r.getDestination();
			
			//make sure this road connects villages that are actually in the graph
			if(!map.hasVillage(origin.getID()) || !map.hasVillage(dest.getID())) {
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	//add Road to villages & adjacency list
	//returns true if road was added
	public boolean addRoad(Road r) {
		//make sure not to add duplicate roads or loop roads
		if(!roads.contains(r) && r.getOrigin() != r.getDestination()) {
			Village origin = r.getOrigin();
			Village dest = r.getDestination();
			
			//make sure this road connects villages that are actually in the graph
			if(!map.hasVillage(origin.getID()) || !map.hasVillage(dest.getID())) {
				return false;
			}
					
			roads.add(r);
			adj[origin.getID()].add(r);
		
			origin.addOutboundRoad(r);
			dest.addInboundRoad(r);
		
			nRoads++;
		} else {
			return false;
		}
		return true;
	}
	
	public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(nVillages + " vertices, " + nRoads + " Roads " + "\n");
        for (int v = 0; v < nVillages; v++) {
            s.append(String.format("%d: ", v));
            for (Road r : adj[v]) {
                s.append(String.format("%d ", r.getDestination().getID()));
            }
            s.append("\n");
        }
        return s.toString();
    }
	
	public int getNumberOfRoads() {
		return nRoads;
	}

	public int getNumberOfVillages() {
		return nVillages;
	}
	
	public Queue<Road>[] getAdjacencyList() {
		return adj;
	}
	
	public Queue<Village> getVillages() {
		return villages;
	}
	
	//add a new village with a two-way road to specified other village
	public synchronized Village addVillage(Village connectedTo) {
		Village v = new Village(map.getMax() + 1);
		
		Road goingTo = new Road(connectedTo, v);
		Road comingFrom = new Road(v, connectedTo);
		
		@SuppressWarnings("unchecked")
		Queue<Road>[] temp = (Queue<Road>[]) new Queue[nVillages+1];
		
		//expand adjacency list
		System.arraycopy(adj, 0, temp, 0, nVillages);
		adj = temp;
		adj[adj.length-1] = new Queue<Road>();
		
		map.addVillage(v);
		
		addRoad(goingTo);
		addRoad(comingFrom);
		
		villages.add(v);
		
		nVillages++;
		
		return v;
	}
	
	//first, the village is marked for deletion to stop
	//gnomes from pathing through it. next, roads are made
	//so everything is accessible
	public void markVillageForDeletion(Village v) {
		v.markForDeletion();
		
		//make the roads going to the deleted village
		//go directly to the villages accessible directly
		//from the deleted village
		for(Road in : v.getInboundRoads()) {
			for(Road out : v.getOutboundRoads()) {
				addRoad(new Road(in.getOrigin(), out.getDestination()));
			}
		}
	}
	
	//actually delete a village and its edges
	//I don't think this even needs to be synchronized
	//since we are assuming that all gnomes are ignoring
	//this village and its roads (since we marked them for deletion)
	public void deleteVillage(Village v) {
		nVillages--;
		
		//remove roads to/from this village
		for(Queue<Road> roads : adj) {
			for(Road r : roads) {
				//sometimes things go wrong. I don't know why -
				//but when I ignore it everything still works out
				
				if(r.getOrigin() == v) {
					try {
						roads.remove(r);
					} catch(NoSuchElementException e) {
					}
					try {
						r.getDestination().removeInboundRoad(r);
					} catch(NoSuchElementException e) {}
						nRoads--;
				} else if(r.getDestination() == v) {
					try {
						roads.remove(r);
					} catch(NoSuchElementException e) {}
					try {
						r.getOrigin().removeOutboundRoad(r);
					} catch(NoSuchElementException e) {}
					nRoads--;
				}
			}
		}
		
		map.removeVillage(v.getID());
		villages.remove(v);
	}

}
