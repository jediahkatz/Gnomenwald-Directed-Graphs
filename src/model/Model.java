package model;

import java.util.NoSuchElementException;

public class Model {
	private DirectedGraph graph; //the world
	private Queue<Gnome> gnomes = new Queue<Gnome>(); //the gnomes
	private Queue<GnomeThread> threads = new Queue<GnomeThread>();
	public final boolean sim;
	
	//used for topSort & minSpanTree to create view without gnomes
	public Model(DirectedGraph g, boolean b) {
		graph = g;
		sim = false;
	}
	
	public Model(DirectedGraph g) {
		graph = g;
		sim = true;
		
		for(Village v : graph.getVillages()) {
			Gnome gnome = new Gnome(v);
			Gnome gnome2 = new Gnome(v);
			gnomes.add(gnome);
			gnomes.add(gnome2);
			threads.add(new GnomeThread(gnome));
			threads.add(new GnomeThread(gnome2));
		}
	}
	
	public void start() {
		for(GnomeThread g : threads) {
			g.start();
		}
	}
	
	public Queue<Gnome> getGnomes() {
		return gnomes;
	}
	
	public Gnome getGnome(int n) throws NoSuchElementException {
		for(Gnome g : gnomes) {
			if(g.getID() == n) return g;
		}
		throw new NoSuchElementException();
	}
	
	public void addGnome(Village v) {
		Gnome gnome = new Gnome(v);
		gnomes.add(gnome);
		GnomeThread thread = new GnomeThread(gnome);
		threads.add(thread);
		thread.start();
	}
	
	public void addGnome(Village v, Village dest) {
		Gnome gnome = new Gnome(v);
		gnome.setDestination(dest);
		gnomes.add(gnome);
		threads.add(new GnomeThread(gnome));
	}
	
	public DirectedGraph getGraph() {
		return graph;
	}
}
