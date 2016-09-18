package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class GraphAlgorithms {
	private static DirectedGraph graph;

	private GraphAlgorithms() {
	}

	public static ArrayList<Village> topologicalSort(DirectedGraph g) {
		TopologicalSort topSort = new TopologicalSort(g);
		return topSort.getSortedQueue();
	}
	
	//breadth-first; based on Kahn's algorithm
	private static class TopologicalSort {
		private DirectedGraph g;
		private Hashtable<Village, Integer> indegrees;
		private ArrayList<Village> sort = new ArrayList<Village>(); 
		// using arraylist for contains()

		private TopologicalSort(DirectedGraph g) {
			this.g = g;
			indegrees = new Hashtable<Village, Integer>(g.getNumberOfVillages());

			// map villages to their original indegrees
			for (Village v : g.getVillages()) {
				indegrees.put(v, v.indegree());
			}

			sort();
		}

		private void sort() {
			Queue<Village> temp = new Queue<Village>();

			while (indegrees.size() > 0) {

				// add villages with an indegree of 0 to the queue
				for (Village v : g.getVillages()) {
					if (!sort.contains(v) && indegrees.containsKey(v) && indegrees.get(v) == 0) {
						indegrees.remove(v);
						temp.enqueue(v);
					}
				}

				// get next item on queue and decrement indegrees of adjacent
				// nodes
				Village next = temp.dequeue();
				sort.add(next); // add the item we just dequeued to the sorted
								// list
				for (Road r : next.getOutboundRoads()) {
					Village v = r.getDestination();
					indegrees.put(v, indegrees.get(v).intValue() - 1); // decrement
																		// by 1
				}
			}
		}

		private ArrayList<Village> getSortedQueue() {
			return sort;
		}
	}

	// the minimum spanning tree of an undirected graph
	public static MinimumSpanningTree minimumSpanningTree(UndirectedGraph g, boolean useLength) {
		MinimumSpanningTree tree = new MinimumSpanningTree(g, useLength);
		return tree;
	}
	
	//uses Prim's algorithm to find the minimum spanning tree
	public static class MinimumSpanningTree {
		private HashSet<Edge> tree = new HashSet<Edge>();
		private HashSet<Integer> nodes = new HashSet<Integer>();
		private int length = 0;
		private UndirectedGraph g;
		private boolean useLength;

		private MinimumSpanningTree(UndirectedGraph g, boolean useLength) {
			this.useLength = useLength;
			this.g = g;
			nodes.add(0);
			while (findNextVillage()) {
			}
		}

		private boolean findNextVillage() {
			int min = Integer.MAX_VALUE;
			Edge shortest = null;
			int newNode = -1;
			
			for(int i : nodes) {
				for(Edge e : g.getAdjacencyList()[i]) {
					int weight;
					if(useLength) {
						weight = e.getLength();
					} else {
						weight = e.getCost();
					}
					
					//find the smallest road
					if(weight < min) {
						//make sure this doesn't lead to an already visited village
						boolean visited = true;
						if(e.getVillage1() == i) {
							if(!nodes.contains(e.getVillage2())) {
								visited = false;
								newNode = e.getVillage2();
							}
						} else if(e.getVillage2() == i) {
							if(!nodes.contains(e.getVillage1())) {
								visited = false;
								newNode = e.getVillage1();
							}
						}
						
						if(!visited) {
							shortest = e;
							min = weight;
						}
					} //end of (weight < min) if block
				} //end of inner for loop
			} //end of outer for loop

			if (shortest != null) {
				length += min;
				tree.add(shortest);
				nodes.add(newNode);
				return true;
			}
			return false; // if we didn't find any new roads
		}

		public int getLength() {
			return length;
		}
		
		public HashSet<Edge> getTree() {
			return tree;
		}
	}

	// uses the last graph created by createStrongGraph()
	public static Village chooseRandomVillage() {
		return graph.getRandomVillage();
	}

	// uses the last graph created by createStrongGraph()
	public static Stack<Road> dijkstraAlgorithm(Village startNode, Village endNode, boolean useLength) {
		Djikstra algo = new Djikstra(graph, startNode, useLength);
		return algo.getRoute(endNode);
	}

	// Djikstra's algorithm - used for calculating the shortest path
	private static class Djikstra {
		//distance or cost from source to specified village
		private Hashtable<Village, Integer> weights;
		// the last edge on shortest path to specified village
		private Hashtable<Village, Road> lastEdge; 
		//priority queue that indexes distances by their node IDs
		private IndexedMinPriorityQueue<Integer> nodesPQ; 
		//true: optimize for distance; false: optimize for cost
		private boolean useLength;

		// precondition: villages are in the graph
		private Djikstra(DirectedGraph g, Village start, boolean useLength) {
			synchronized(g) {
			this.useLength = useLength;
			graph = g;
			weights = new Hashtable<Village, Integer>(g.getNumberOfVillages());
			lastEdge = new Hashtable<Village, Road>(g.getNumberOfVillages());
			nodesPQ = new IndexedMinPriorityQueue<Integer>(g.getNumberOfVillages());

			for (Village v : g.getVillages()) {
				weights.put(v, Integer.MAX_VALUE); // set all weights to "infinity"
			}
			weights.put(start, 0);

			nodesPQ.insert(start.getID(), 0);
			while (!nodesPQ.isEmpty()) {
				int v = nodesPQ.delMin(); // get the id of the root

				for (Road r : g.getAdjacencyList()[v]) {
					// we can't go through villages marked for deletion
					if (!r.getDestination().isMarkedForDeletion()) {
						update(r);
					}
				}
			}
			}
		}

		private void update(Road r) {
			Village origin = r.getOrigin();
			Village dest = r.getDestination();

			// determine whether to use length or cost as the weight
			int weight = (useLength) ? r.getLength() : r.getCost();

			// if taking this road to Village dest is shorter/cheaper than
			// whatever we had before...
			if (weights.get(dest) > weights.get(origin) + weight) {
				weights.put(dest, weights.get(origin) + weight); // update weight
				lastEdge.put(dest, r); // update path

				if (nodesPQ.contains(dest.getID())) {
					nodesPQ.decreaseKey(dest.getID(), weights.get(dest));
					//update the priority queue
				} else {
					nodesPQ.insert(dest.getID(), weights.get(dest));
					//add to priority queue if not alreadty there
				}
			}
		}

		private Stack<Road> getRoute(Village destination) {
			Stack<Road> route = new Stack<Road>();
			for (Road r = lastEdge.get(destination); r != null; r = lastEdge.get(r.getOrigin())) {
				route.push(r);
			}

			return route;
		}
	}

	// create a strongly connected graph with n nodes and 2n edges
	public static DirectedGraph createStrongGraph(int n) {
		int E = 2 * n;

		DirectedGraph G = new DirectedGraph(n);
		HashSet<Road> set = new HashSet<Road>();

		// create a strongly connected graph by
		// combining a rooted in-tree and a rooted out-tree

		int[] vertices = new int[n];
		int i = 0;
		for (int v = 0; v < n; v++) {
			vertices[i++] = v; // assign the vertices 0 thru n - 1
		}
		MyRandom.shuffle(vertices);

		// rooted-in tree with root = vertices[n-1] (last vertex)
		// give every village an inbound road from a random other village (each
		// one once)
		for (int v = 0; v < n - 1; v++) {
			int w = MyRandom.randInt(v + 1, n);
			Road e = new Road(G.getVillage(vertices[w]), G.getVillage(vertices[v]));
			set.add(e);
			G.addRoad(e);
		}

		// rooted-out tree with root = vertices[n-1] (last vertex)
		// give every village an outbound road to a random other village (each
		// one once)
		for (int v = 0; v < n - 1; v++) {
			int w = MyRandom.randInt(v + 1, n);
			Road e = new Road(G.getVillage(vertices[v]), G.getVillage(vertices[w]));
			set.add(e);
			G.addRoad(e);
		}

		// add extra edges randomly until we have reached our nEdges target
		while (G.getNumberOfRoads() < E) {
			int v = MyRandom.randInt(n);
			int w = MyRandom.randInt(n);
			Road e = new Road(G.getVillage(v), G.getVillage(w));
			if (!set.contains(e) && v != w) {
				set.add(e);
				G.addRoad(e);
			}
		}

		graph = G;
		return G;
	}

	// create a directed acyclic graph with n nodes
	public static DirectedGraph createDAG(int n) {
		int nEdges = 2 * n;
		DirectedGraph G = new DirectedGraph(n);
		HashSet<Road> set = new HashSet<Road>();

		while (G.getNumberOfRoads() < nEdges) {
			int v = MyRandom.randInt(n);
			int w = MyRandom.randInt(n);

			Road r = new Road(G.getVillage(v), G.getVillage(w));
			if (v < w && !set.contains(r)) {
				set.add(r);
				G.addRoad(r);
			}
		}

		return G;
	}
	
	//build a connected undirected graph with n nodes
	public static UndirectedGraph createUndirected(int n) {
		int nEdges = (int) (1.5*n);
		
		UndirectedGraph G = new UndirectedGraph(n);
		HashSet<Edge> set = new HashSet<Edge>();
		
		int[] vertices = new int[n];
		for (int i = 0; i < n; i++)
			vertices[i] = i;
		MyRandom.shuffle(vertices);
		
		for (int v = 0; v < n - 1; v++) {
			int w = MyRandom.randInt(v + 1, n);
			Edge e = new Edge(vertices[w], vertices[v]);
			set.add(e);
			G.addEdge(e);
		}
		
		while (G.getNumberOfEdges() < nEdges) {
			int v = MyRandom.randInt(n);
			int w = MyRandom.randInt(n);

			Edge e = new Edge(v, w);
			if (v < w && !set.contains(e)) {
				set.add(e);
				G.addEdge(e);
			}
		}
		
		return G;
	}

}
