package model;

import java.util.NoSuchElementException;

//a table that maps the integer ID of a Village to an index in the array
public class VillageIndexMap {
	private int size; //size of array (not number of Villages)
	private Village[] table;
	
	public VillageIndexMap() {
		this(0);
	}

	//n = starting size of array
	public VillageIndexMap(int n) {
		size = n;
		table = new Village[n];
	}
	
	public void setVillage(Village v, int n) throws ArrayIndexOutOfBoundsException {
		table[n] = v;
	}
	
	//expands the size of the array by 1 and puts v in the new spot
	//if I were smarter and had more time, I'd use a system like
	//arraylist for growing the array, where the array size is
	//always a power of two
	public synchronized void addVillage(Village n) {
		Village[] temp = new Village[size+1];
		System.arraycopy(table, 0, temp, 0, size);
		table = temp;
		size = table.length;
		table[size-1] = n;
	}
	
	public boolean hasVillage(int n) {
		try {
			if(table[n] == null) return false;
		} catch(ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		return true;
	}
	
	public Village getVillage(int n) throws NoSuchElementException {
		if(!hasVillage(n)) throw new NoSuchElementException();
		
		return table[n];
	}
	
	public int getMax() {
		return size - 1;
	}
	
	public Village getRandomVillage() {
		int n;
		do {
			n = MyRandom.randInt(size);
		} while (table[n] == null);
		
		return table[n];
	}
	
	public Village removeVillage(int index) throws NoSuchElementException {
		if(!hasVillage(index)) throw new NoSuchElementException();
		
		Village temp = table[index];
		table[index] = null;
		
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO " + hasVillage(index));
		return temp;
	}
}
