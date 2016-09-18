package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import model.ListModelHolder;
import model.Model;
import model.UndirectedGraph;

public class View {
	private JLabel label;
	private ListModelHolder listModel;
	private GraphViewComponent gView;
	private JPanel listPanel = new JPanel();
	private JButton addVillage;
	private JButton delVillage;
	private Model model;
	
	public View(UndirectedGraph u) {
		JFrame frame = new JFrame();
		gView = new GraphViewComponent(u);
		
		frame.getContentPane().add(gView.getComponent());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900,600);
		frame.setVisible(true);
	}
	
	public View(Model m) {
		model = m;
		JFrame frame = new JFrame();
		gView = new GraphViewComponent(m.getGraph(), this);
		JPanel panel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		
		panel.setLayout(new BorderLayout());
		panel.add(gView.getComponent(),BorderLayout.CENTER);
		
		JList<String> list = new JList<String>();
		((javax.swing.DefaultListCellRenderer)list.getCellRenderer()).setOpaque(false);
		list.setOpaque(false);
		listModel = gView.getListModelHolder();
		list.setModel(listModel.getListModel());
		list.setAlignmentX(0);
		
		label = new JLabel();
		listPanel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		listPanel.add(label);
		listPanel.add(list);
		
		addVillage = new JButton("Add Village");
		addVillage.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				gView.addVillage();
			}
		});
		
		delVillage = new JButton("Delete Village");
		delVillage.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				gView.removeVillage();
				updateList();
			}
			
		});
		
		listPanel.add(addVillage);
		listPanel.setVisible(false);
		listPanel.add(delVillage);
		listPanel.setVisible(false);
		
		panel.add(listPanel, BorderLayout.WEST);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.add(panel, JSplitPane.LEFT);
		Console console = new Console(this);
		splitPane.add(console, JSplitPane.RIGHT);
		
		if(!model.sim) {
			console.setVisible(false);
		}
		
		frame.add(splitPane);//,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900,600);
		frame.setVisible(true);
		splitPane.setDividerLocation(0.67);
	}

	public void updateList() {
		if(!model.sim) {
			return;
		}
		if(listModel.getName() == "") {
			listPanel.setVisible(false);
		} else {
			label.setText("<html><b>"+listModel.getName()+"</b><br><i>Capacity: "+listModel.getCapacity()+"</i></html>");
			if(listModel.isVillage() && !listModel.isMarkedForDeletion()) {
				delVillage.setVisible(true);
				addVillage.setVisible(true);
			} else {
				addVillage.setVisible(false);
				delVillage.setVisible(false);
			}
			listPanel.setVisible(true);
		}
	}
	
	public GraphViewComponent getGraphViewComponent() {
		return gView;
	}
	
	public Model getModel() {
		return model;
	}
}
