package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.mxgraph.layout.*;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import model.DirectedGraph;
import model.Edge;
import model.Gnome;
import model.ListModelHolder;
import model.Queue;
import model.Road;
import model.UndirectedGraph;
import model.Village;

public class GraphViewComponent {
	private DirectedGraph g;
	private UndirectedGraph u;
	private mxGraphComponent component;
	// holds the list of gnomes in the currently
	// selected village or road; used by JList
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private ListModelHolder listModelHolder = new ListModelHolder(listModel);
	private View view; // parent
	private mxFastOrganicLayout layout;
	private mxCell currentCell;
	private ArrayList<Object> nodes;

	public GraphViewComponent(DirectedGraph g, View view) {
		this.g = g;
		this.view = view;
		component = setupView();
	}
	
	public GraphViewComponent(UndirectedGraph u) {
		this.u = u;
		component = setupUndirectedView();
	}
	
	private mxGraphComponent setupUndirectedView() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();

		nodes = new ArrayList<Object>(u.getNumberOfVillages());
		try {
			for (int i = 0; i < u.getNumberOfVillages(); i++) {
				nodes.add(i, graph.insertVertex(parent, null, i, 0, 0, 40, 15, "ROUNDED"));
			}
			
			for (Edge e : u.getEdges()) {
				int v = e.getVillage1();
				int w = e.getVillage2();
				
				graph.insertEdge(parent, null, e.getName(), nodes.get(v), nodes.get(w));
				graph.insertEdge(parent, null, e.getName(), nodes.get(w), nodes.get(v));

			}
		} finally {
			// Updates the display
			graph.getModel().endUpdate();
		}

		graph.getModel().beginUpdate();
		layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(175);
		mxParallelEdgeLayout second = new mxParallelEdgeLayout(graph);
		// mxCompositeLayout layout = new mxCompositeLayout(graph, [first,
		// second], first); <--- it seems mxCompositeLayout no longer works
		layout.execute(graph.getDefaultParent());
		second.execute(graph.getDefaultParent());
		graph.getModel().endUpdate();

		graph.setCellsDisconnectable(false);
		graph.setCellsResizable(false);
		graph.setVertexLabelsMovable(false);
		graph.setCellsEditable(false);
		graph.setConnectableEdges(false);
		graph.setAllowDanglingEdges(false);
		graph.setAllowLoops(false);
		graph.setCellsDeletable(false);
		graph.setCellsCloneable(false);
		graph.setDropEnabled(false);
		graph.setSplitEnabled(false);
		graph.setCellsBendable(false);

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setConnectable(false);
		
		//fix the edges so they look nice
		graph.addListener(mxEvent.CELLS_MOVED, new mxIEventListener() {
			@Override
			public void invoke(Object arg0, mxEventObject arg1) {
				(new mxParallelEdgeLayout(graph)).execute(graph.getDefaultParent());
			}
		});

		return graphComponent;
	}

	private mxGraphComponent setupView() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();

		nodes = new ArrayList<Object>(g.getNumberOfVillages());
		try {
			for (int i = 0; i < g.getNumberOfVillages(); i++) {
				nodes.add(i, graph.insertVertex(parent, null, i, 0, 0, 40, 15, "ROUNDED"));
			}

			Queue<Road>[] adj = g.getAdjacencyList();

			for (int i = 0; i < g.getNumberOfVillages(); i++) {
				for (Road road : adj[i]) {
					graph.insertEdge(parent, null, road.getName(), nodes.get(i), nodes.get(road.getDestination().getID()));
				}
			}
		} finally {
			// Updates the display
			graph.getModel().endUpdate();
		}

		graph.getModel().beginUpdate();
		layout = new mxFastOrganicLayout(graph);
		layout.setForceConstant(175);
		mxParallelEdgeLayout second = new mxParallelEdgeLayout(graph);
		// mxCompositeLayout layout = new mxCompositeLayout(graph, [first,
		// second], first); <--- it seems mxCompositeLayout no longer works
		layout.execute(graph.getDefaultParent());
		second.execute(graph.getDefaultParent());
		graph.getModel().endUpdate();

		graph.setCellsDisconnectable(false);
		graph.setCellsResizable(false);
		graph.setVertexLabelsMovable(false);
		graph.setCellsEditable(false);
		graph.setConnectableEdges(false);
		graph.setAllowDanglingEdges(false);
		graph.setAllowLoops(false);
		graph.setCellsDeletable(false);
		graph.setCellsCloneable(false);
		graph.setDropEnabled(false);
		graph.setSplitEnabled(false);
		graph.setCellsBendable(false);

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setConnectable(false);
		
		graph.addListener(mxEvent.CELLS_MOVED, new mxIEventListener() {
			@Override
			public void invoke(Object arg0, mxEventObject arg1) {
				//fix the edges so they look nice
				(new mxParallelEdgeLayout(graph)).execute(graph.getDefaultParent());
			}
		});

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
				currentCell = cell;
				
				selectCell(cell);
			}
		});

		return graphComponent;
	}
	
	private void selectCell(mxCell cell) {
		if (cell != null) {
			if (cell.isVertex()) {
				// get the village object that this cell represents
				Village v = g.getVillage((int) cell.getValue());

				listModel.clear();
				for (Gnome g : v.getGnomes()) {
					listModel.addElement(g.getName());
				}
				
				view.updateList();
				listModelHolder.setVillage(v);
			} else if (cell.isEdge()) {
				// get the id of the origin & destination nodes
				int origin = (int) cell.getSource().getValue();
				int dest = (int) cell.getTarget().getValue();
										
				Road thisRoad = null;
				for (Road r : g.getAdjacencyList()[origin]) {
					// find the Road object that this edge represents
					if (thisRoad == null && r.getDestination().getID() == dest) {
						thisRoad = r;
					}
				}

				listModel.clear();
				for (Gnome g : thisRoad.getGnomes()) {
					listModel.addElement(g.getName());
				}

				listModelHolder.setRoad(thisRoad);
			}

		} else {
			listModelHolder.clear();
		}

		view.updateList();
	}
	
	public void addRoad(Road r) throws IllegalArgumentException, NoSuchElementException {
		int v = r.getOrigin().getID();
		int w = r.getDestination().getID();
		
		if(v == w) throw new IllegalArgumentException();
		
		mxCell origin = null;
		mxCell dest = null;
		
		for(Object o : nodes) {
			if((int) ((mxCell) o).getValue() == v) {
				origin = (mxCell) o;
			} else if((int) ((mxCell) o).getValue() == w) {
				dest = (mxCell) o;
			}
		}
		
		if(dest == null || origin == null) {
			//this happens when a village still holds roads
			//connected to a now deleted village
			throw new NoSuchElementException();
		}
		
		mxGraph graph = component.getGraph();
		
		graph.getModel().beginUpdate();
		graph.insertEdge(graph.getDefaultParent(), null, r.getName(), origin, dest);
		graph.getModel().endUpdate();
	}
	
	public void addVillage() {
		addVillage(currentCell);
	}

	private synchronized void addVillage(mxCell connectedTo) {
		mxGraph graph = component.getGraph();
		graph.getModel().beginUpdate();
		
		Village connected = g.getVillage((int) connectedTo.getValue());
		Village v = g.addVillage(connected);
		
		Road to = v.getInboundRoads().iterator().next();
		Road from = v.getOutboundRoads().iterator().next();
		
		Object vCell = graph.insertVertex(graph.getDefaultParent(), null, v.getID(), connectedTo.getGeometry().getCenterX(), connectedTo.getGeometry().getCenterY(), 40, 15, "ROUNDED");
		nodes.add(vCell);
		
		graph.insertEdge(graph.getDefaultParent(), null, to.getName(), connectedTo, vCell);
		graph.insertEdge(graph.getDefaultParent(), null, from.getName(), vCell, connectedTo);
		
		layout.execute(graph.getDefaultParent());
		(new mxParallelEdgeLayout(graph)).execute(graph.getDefaultParent());
		graph.getModel().endUpdate();
	}
	
	public void removeVillage() {
		markVillageForDeletion(currentCell);
	}
	
	private void markVillageForDeletion(mxCell village) {
		mxGraph graph = component.getGraph();
		graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "red", new Object[] {village});
		
		Village toDelete = g.getVillage((int) village.getValue());
		
		Queue<mxCell> inboundEdges = new Queue<mxCell>();
		Queue<mxCell> outboundEdges = new Queue<mxCell>();

		for(int i=0; i<village.getEdgeCount(); i++) {
			mxCell edge = (mxCell) village.getEdgeAt(i);
			if(edge.getSource() == village) {
				outboundEdges.add(edge);
			} else if(edge.getTarget() == village) {
				inboundEdges.add(edge);
			}
		}
		
		for(mxCell in : inboundEdges) {
			for(mxCell out : outboundEdges) {
				Village origin = g.getVillage((int) in.getSource().getValue());
				Village dest = g.getVillage((int) out.getTarget().getValue());

				Road r = new Road(origin, dest);
				
				try {
					if(g.canAdd(r)) {
						addRoad(r);
						g.addRoad(r);
					}
				} catch(NoSuchElementException | IllegalArgumentException e) {
					//this is fine - it's caused by a bug where
					//the graph doesn't realize villages are gone,
					//since we didn't delete them from the graph,
					//because deleting them from the graph causes another bug
				}
			}
		}
		
		//make edges look nice
		(new mxParallelEdgeLayout(graph)).execute(graph.getDefaultParent());
		
		//mark the village for deletion in the model
		g.markVillageForDeletion(toDelete);
		
		CheckForDeletionThread checker = new CheckForDeletionThread(village, toDelete, this);
		checker.start();
	}
	
	//a thread that checks every second if a village is ready
	//to be deleted, and then when it is, it deletes it
	private static class CheckForDeletionThread extends Thread {
		private mxCell cell;
		private Village v;
		private GraphViewComponent parent;
		
		public CheckForDeletionThread(mxCell cell, Village v, GraphViewComponent parent) {
			this.cell = cell;
			this.v = v;
			this.parent = parent;
		}
		
		@Override
		public void run() {
			boolean ready = false;
			
			//keep checking until village is ready to be deleted
			while(!ready) {
				ready = v.readyForDeletion();

				if(!ready) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
			
			parent.deleteVillage(cell, v);
		}
	}
	
	//this actually deletes the village when it is ready to be deleted
	//also deletes its edges
	private void deleteVillage(mxCell village, Village v) {
		nodes.remove(village);
		
		component.getGraph().getModel().remove(village);
		
		//g.deleteVillage(v);
		
		//I decided to not actually delete the villages from the model
		//deleting them just messes things up
	}

	public mxGraphComponent getComponent() {
		return component;
	}

	public ListModelHolder getListModelHolder() {
		return listModelHolder;
	}
}
