package model;

//undirected edge
public class Edge {
	private final int v;
	private final int w;
	private int length;
	private int cost;
	private static int nEdges = 0;
	
	private String name;
	
	public Edge(int v, int w) {
		this.v = v;
		this.w = w;
		
		//random cost, length
		length = MyRandom.randInt(1,10);
		cost = MyRandom.randInt(5,10);
		
		name = "Road " + (nEdges++) + " (" + length + "s/$" + cost + ")";
	}
	
	public String getName() {
		return name;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getVillage1() {
		return v;
	}
	
	public int getVillage2() {
		return w;
	}
	
	@Override
	public int hashCode() {
		return (v + w) * (v + w) * (v + w);
	}
	
	public boolean equals(Edge e) {
		if(v == e.getVillage1()) {
			if(w == e.getVillage2()) {
				return true;
			}
		}
		
		if(w == e.getVillage1()) {
			if(v == e.getVillage2()) {
				return true;
			}
		}
		
		return false;
	}
}

