package model;

import javax.swing.DefaultListModel;

//this class holds info about the currently
//selected node or edge and then transmits
//that info back to the JList
public class ListModelHolder {
	private DefaultListModel<String> model;
	private Village v;
	private Road r;
	private boolean isVillage = true;
	
	public ListModelHolder(DefaultListModel<String> m) {
		model = m;
	}
	
	public void setVillage(Village v) {
		this.v = v;
		isVillage = true;
	}
	
	public void setRoad(Road r) {
		this.r = r;
		isVillage = false;
	}
	
	public String getName() {
		if(v == null && r == null) return "";
		return (isVillage) ? "Village " + v.getID() : r.getName();
	}
	
	public int getCapacity() {
		if(v == null && r == null) return -1;
		return (isVillage) ? v.getCapacity() : r.getCapacity();
	}
	
	public void clear() {
		v = null;
		r = null;
		model.clear();
	}
	
	public boolean isVillage() {
		return isVillage;
	}
	
	public DefaultListModel<String> getListModel() {
		return model;
	}
	
	public boolean isMarkedForDeletion() {
		return isVillage() ? v.isMarkedForDeletion() : false;
	}
}
