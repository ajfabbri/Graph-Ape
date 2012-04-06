package net.fabbrication.graph;

import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** 
 * A dialog which prompts the user for graph generation parameters. 
 */
class RecipeDialog extends JDialog implements ActionListener {
	
	private GraphApp app;
	private JLabel lMinDegree, lMaxDegree, lDepth;
	private JSpinner sMinDegree, sMaxDegree, sDepth;
	private JButton bOk, bCancel;
	private boolean success;

	public RecipeDialog(GraphApp parent) {
		super(parent.appFrame, true);
		app = parent;
		
		Point p = parent.appFrame.getLocation();
		setLocation(p.x + 100, p.y + 100);
	
		setTitle("Tree Generation Parameters");
		lMinDegree = new JLabel("Minimum degree");
		lMaxDegree = new JLabel("Maximum degree");
		lDepth = new JLabel("Maximum tree depth");
		sMinDegree = new JSpinner(new SpinnerNumberModel(3, 0, 20, 1));
		sMaxDegree = new JSpinner(new SpinnerNumberModel(3, 0, 20, 1));
		sDepth = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
		bOk = new JButton("Ok");
		bOk.setMnemonic('o');
		bOk.addActionListener(this);
		bCancel = new JButton("Cancel");
		bCancel.setMnemonic('c');
		bCancel.addActionListener(this);
		
		getContentPane().setLayout(new GridLayout(4,2,10,10));
		getContentPane().add(lMinDegree);
		getContentPane().add(sMinDegree);
		getContentPane().add(lMaxDegree);
		getContentPane().add(sMaxDegree);
		getContentPane().add(lDepth);
		getContentPane().add(sDepth);
		getContentPane().add(bOk);
		getContentPane().add(bCancel);
	
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		show();
	}
	
	public int getMinDegree() {
		return ((Integer)sMinDegree.getValue()).intValue(); 
	}
	public int getMaxDegree() {
		return ((Integer)sMaxDegree.getValue()).intValue(); 
	}
	public int getDepth() {
		return ((Integer)sDepth.getValue()).intValue();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		GraphApp.dprint("rd-action: " + e);
		if (cmd.equals("Ok")) {
			success = true;
		} else if (cmd.equals("Cancel")) {
			success = false;
		} else {
			return;
		}
		// close window, firing window event on app
		hide();
	}

	/**
	 * @return true if the user pressed OK with valid params.
	 */
	public boolean isSuccess() {
		return success;
	}
}