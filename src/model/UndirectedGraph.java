package model;

import java.util.HashSet;

//for minimum spanning tree

public class UndirectedGraph {
	private Queue<Edge>[] adj; //adjacency list for each node
	private int nNodes;
	private int nEdges = 0;
	private HashSet<Edge> edges = new HashSet<Edge>(); //holds all the edges
	
	@SuppressWarnings("unchecked")
	public UndirectedGraph(int n) {
		nNodes = n;
		
		adj = (Queue<Edge>[]) new Queue[n];
		for(int i=0; i<adj.length; i++) {
			adj[i] = new Queue<Edge>();
		}
	}
	
	public void addEdge(Edge e) {
		if(!contains(e)) {
			nEdges++;
		
			int v = e.getVillage1();
			int w = e.getVillage2();
		
			adj[v].add(e);
			adj[w].add(e);
			edges.add(e);
		}
	}
	
	public HashSet<Edge> getEdges() {
		return edges;
	}
	
	public int getNumberOfEdges() {
		return nEdges;
	}
	
	public int getNumberOfVillages() {
		return nNodes;
	}
	
	public Queue<Edge>[] getAdjacencyList() {
		return adj;
	}
	
	//for some reason HashSet contains isn't working even though I overrode hashCode
	private boolean contains(Edge edge) {
		for(Edge e : edges) {
			if(e.equals(edge) || edge.equals(e)) {
				return true;
			}
		}
		
		return false;
	}
}