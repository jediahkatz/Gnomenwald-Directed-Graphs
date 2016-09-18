package model;

import java.util.HashSet;

public interface HoldsGnomes {
	public HashSet<Gnome> getGnomes();
	public void moveGnome(Gnome g); //move gnome g to its next destination
	public void addGnome(Gnome g);
	public Gnome removeGnome(Gnome g);
	public int getSleepTime();
	public String getName();
	public int getCapacity();
}
