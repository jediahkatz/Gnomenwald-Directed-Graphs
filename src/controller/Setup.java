package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import model.DirectedGraph;
import model.Edge;
import model.GraphAlgorithms;
import model.GraphAlgorithms.MinimumSpanningTree;
import model.Model;
import model.UndirectedGraph;
import model.Village;
import view.View;

public class Setup {
	
	private Setup() {
	}
	
	public static void main(String[] args) {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		String response = "";
		int which = -1;
		boolean goodResponse = false;
		
		System.out.println("Type 0 for the main gnome simulation.\nType 1 for a demonstration of topological sort.\nType 2 for a demonstration of a minimum spanning tree.");
		
		while(!goodResponse) {
			try {
				response = reader.readLine();
				which = Integer.valueOf(response);
			} catch (IOException e) {
				System.out.println("A fatal error occurred.");
				System.exit(0);
			} catch(NumberFormatException e) {
				System.out.println("Try again. Please type only 0, 1, or 2.");
			}
			
			if(which >= 0 && which <= 2) {
				goodResponse = true;
			} else {
				System.out.println("Try again. Please type only 0, 1, or 2.");
			}
		}
		
		System.out.println("How many villages/nodes would you like to build?\nMin: 3 | Max : 50 | Recommended: 10");
		goodResponse = false;
		int number = -1;
		
		while(!goodResponse) {
			try {
				response = reader.readLine();
				number = Integer.valueOf(response);
			} catch (IOException e) {
				System.out.println("A fatal error occurred.");
				System.exit(0);
			} catch(NumberFormatException e) {
				System.out.println("Try again. Please type an integer between 3 and 50.");
			}
			
			if(number >= 3 && number <= 50) {
				goodResponse = true;
			} else {
				System.out.println("Try again. Please type an integer between 3 and 50.");
			}
		}
		
		DirectedGraph g = null;
		Model m = null;
		
		if(which == 0) {
			g = GraphAlgorithms.createStrongGraph(number);
			m = new Model(g);
			m.start();
			
			new View(m);
		} else if(which == 1) {
			g = GraphAlgorithms.createDAG(number);
			m = new Model(g, false);
			System.out.println("TOPOLOGICAL SORT OF VILLAGES:\n");

			for(Village v : GraphAlgorithms.topologicalSort(g)) {
				System.out.println(v.getName());
			}
			
			new View(m);
		} else if(which == 2) {
			UndirectedGraph undir = GraphAlgorithms.createUndirected(number);
			MinimumSpanningTree distMST = GraphAlgorithms.minimumSpanningTree(undir, true);

			System.out.println("MIN. SPANNING TREE FOR DISTANCE:\nLENGTH: " + distMST.getLength() + " sec");
			for(Edge e : distMST.getTree()) {
				System.out.println(e.getName());
			}
			
			System.out.println();
			MinimumSpanningTree costMST = GraphAlgorithms.minimumSpanningTree(undir, false);

			System.out.println("MIN. SPANNING TREE FOR COST:\nCOST: $" + costMST.getLength());
			for(Edge e : costMST.getTree()) {
				System.out.println(e.getName());
			}
			
			new View(undir);
		}
		
	}
}
