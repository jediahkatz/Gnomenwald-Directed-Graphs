package model;

public class GnomeThread extends Thread {
	private Gnome gnome;
	
	public GnomeThread(Gnome g) {
		gnome = g;
	}
	
	@Override
	public void run() {
		while(true) {
			gnome.moveAround();
		}
	}
}
