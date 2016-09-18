package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.NoSuchElementException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import model.Authenticator;
import model.Gnome;
import model.Model;
import model.Road;
import model.Village;

public class Console extends JPanel {
	private static final long serialVersionUID = -7617263274670972868L;
	
	private JTextField textField = new JTextField();
	private JTextArea textArea = new JTextArea();
	private Model model;
	private View parent;
	private Authenticator auth;
	
	public Console(View parent) {
		this.parent = parent;
		model = parent.getModel();
		auth = new Authenticator(model);
		setLayout(new BorderLayout());
		
		//so that the text area scrolls down
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		 
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		textArea.setEditable(false);
		textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 30));
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					addUserLine(textField.getText());
					doCommand(textField.getText());
					textField.setText("");
				}
			}
			
		});
		add(textField, BorderLayout.SOUTH);
		addLine("Type HELP for commands.");
	}
	
	private void addLine(String line) {
		textArea.append(line + "\n");
	}
	
	private void addUserLine(String line) {
		addLine("> " + line);
	}
	
	private void doCommand(String line) {
		line = line.toLowerCase();
		String command = getFirstWord(line);
		
		switch(command) {
		case "help":
			help();
			break;
		case "lookup":
			lookup(line);
			break;
		case "add":
			add(line);
			break;
		}
	}
	
	private String getFirstWord(String str) {
	    if (str.indexOf(' ') != -1) { //if there is a space
	      return str.substring(0, str.indexOf(' ')); //get first word
	    } else {
	      return str; //only one word
	    }
	}
	
	private void help() {
		addLine("You can use the following commands:");
		addLine("LOOKUP [gnome]\n    - Try to look up the specified gnome.");
		addLine("ADD ROAD [village1] [village2]\n    - Add a road going from village1 to village2.");
		addLine("ADD GNOME [village1] [village2]\n   - Add a gnome at village1 whose destination is village2.");
		//addLine("")
	}
	
	private void lookup(String line) {
		if(line.indexOf(' ') == -1) return;
		
		line = line.substring(line.indexOf(' ')+1); //cut out the first word ("lookup")
		
		Gnome g = null;
		try {
			int gnomeID = Integer.valueOf(getFirstWord(line));
			g = model.getGnome(gnomeID);
			
			auth.requestInfo(g);
			
			addLine(g.getName());
			addLine("Current location: " + g.getLocation().getName());
			addLine("Destination: " + g.getDestination().getName());
			addLine("Balance: $" + g.getMoney());
		} catch(NumberFormatException | NoSuchElementException e) {
			addLine("\""+line+"\" is not a valid gnome ID!");
		} catch(SecurityException e) {
			addLine(e.getMessage());
		}
	}
	
	private void add(String line) {
		if(line.indexOf(' ') == -1) return;
		
		line = line.substring(line.indexOf(' ')+1); //cut out the first word ("add")
		
		String next = getFirstWord(line);
		if(next.equals("gnome")) {
			addGnome(line);
		} else if(next.equals("road")) {
			addRoad(line);
		} else {
			addLine("Invalid command. You may ADD GNOME or ADD ROAD.");
		}
	}
	
	private void addGnome(String line) {
		if(line.indexOf(' ') == -1) return;
		
		line = line.substring(line.indexOf(' ')+1); //cut out the first word ("gnome")
		
		String location = getFirstWord(line);
		String destination = null;
		
		if(line.indexOf(' ') != -1) {
			line = line.substring(line.indexOf(' ')+1);
			destination = getFirstWord(line);
		}
		
		Village v = null;
		try {
			v = model.getGraph().getVillage(Integer.valueOf(location));
			if(v.isFull()) {
				addLine(v.getName() + " is full, and you cannot add a gnome!");
				return;
			}
		} catch(NumberFormatException | NoSuchElementException e) {
			addLine("\""+location+"\" is not a valid village ID!");
			return;
		}
		
		if(destination != null) {
			try {
				Village dest = model.getGraph().getVillage(Integer.valueOf(destination));
				model.addGnome(v, dest);
				addLine("A new gnome headed for " + dest.getName() + " was added to " + v.getName() + ".");
			} catch(NumberFormatException | NoSuchElementException e) {
				addLine("\""+destination+"\" is not a valid village ID!");
			}
		} else {
			model.addGnome(v);
			addLine("A new gnome was added to " + v.getName() + ".");
		}
	}
	
	private void addRoad(String line) {
		if(line.indexOf(' ') == -1) return;
		
		line = line.substring(line.indexOf(' ')+1); //cut out the first word ("road")
		
		String comingFrom = getFirstWord(line);
		
		if(line.indexOf(' ') == -1) return;
		line = line.substring(line.indexOf(' ')+1); //cut out the comingFrom village
		
		String goingTo = getFirstWord(line);
		
		Village origin;
		Village dest;
		
		try {
			origin = model.getGraph().getVillage(Integer.valueOf(comingFrom));
		} catch (NumberFormatException | NoSuchElementException e) {
			addLine("\""+comingFrom+"\" is not a valid village ID!");
			return;
		}
		
		try {
			dest = model.getGraph().getVillage(Integer.valueOf(goingTo));
		} catch (NumberFormatException | NoSuchElementException e) {
			addLine("\""+goingTo+"\" is not a valid village ID!");
			return;
		}
		
		Road r = new Road(origin, dest);
		model.getGraph().addRoad(r);
		parent.getGraphViewComponent().addRoad(r);
		addLine(r.getName() + " was added from " + r.getOrigin().getName() + " to " + r.getDestination().getName() + ".");
	}
}
