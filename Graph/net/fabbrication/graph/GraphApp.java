package net.fabbrication.graph;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import net.fabbrication.graph.GraphModelGenerator.GMGException;
/**
 * Graphical application for manipulation and visualization of graphs.
 * @author Aaron Fabbri
 */
public class GraphApp extends JPanel implements ActionListener,
	WindowListener {

	private static final long serialVersionUID = 1L;
	public static final int LOG_DEBUG = 5;
	public static final int LOG_INFO = 4;
	public static final int LOG_NONE = 0;
	
	private static final String VERSION_STRING = "0.1";
	
	private static int debug_level = LOG_DEBUG;
	
	/** View/controller object */
	public GraphView gView;
	
	/** Model */
	public GraphModel gModel;
	
	// GUI Stuff
	public JFrame appFrame;
	
	JPanel mainPanel, statusPanel;
	
	JScrollPane displayPane;
	
	// Menu bar
	JMenuBar menuBar;

	// Status panel stuff
	JLabel statusLabel, statsLabel;
	
	// Display panel stuff
	GraphView graphView;
	
	RecipeDialog recipeDialog;
	
	public GraphApp(JFrame f) {
		this(f, false);
	}
	
	public GraphApp(JFrame f, boolean test) {
		
		dprint("GraphApp()");
		// Create view and controller
		gView = new GraphView(this);
		dprint("created gView");
		// Create model
		gModel = new GraphModel(this);
		
		if (test) {
			return;
		}
		appFrame = f;
		
		// Build menu bar
		buildMenuBar();
		
		//Create the phase selection and display panels.
		statusPanel = new JPanel();
		displayPane = new JScrollPane();
		
		//Add various widgets to the sub panels.
		buildSubpanels();
		
		// This class serves as the main panel.
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		add(displayPane);
		add(statusPanel);
	}
	
	public void actionPerformed(ActionEvent e) {
		boolean refresh = true;
		String cmd = e.getActionCommand();
		dprint("action '" + cmd +"':" + e);
		
		if (cmd.equals("Small")) {
			gView.setRenderSize(VertexShape.VS_SMALL); 
		} else if (cmd.equals("Medium")) {
			gView.setRenderSize(VertexShape.VS_MEDIUM);
		} else if (cmd.equals("Big")) {
			iprint("biggin");
			gView.setRenderSize(VertexShape.VS_BIG);
		} else if (cmd.equals("Clear Graph")) {
			refresh = clearGraph();
			gView.clearVertexSelection();
		} else if (cmd.equals("Debug")) {
				iprint("gView.getRenderSize() -> " +
						Integer.toString(gView.getRenderSize()));
		} else if (cmd.equals("Simple Tree")) {
			startGenSimpleTree();
		} else {
			refresh = false;
		}
		if (refresh) {
			repaint();
		}
	}

	/** 
	 * Implement Simple Tree generation menu option.  Ask user for
	 * parameters and generate graph.
	 */
	protected void startGenSimpleTree() {
		//SimpleTreeGenerator.Recipe r = new SimpleTreeGenerator.Recipe();
		
		recipeDialog = new RecipeDialog(this);
		recipeDialog.addWindowListener(this);
	}
	
	protected void finishGenSimpleTree() {
		if (! recipeDialog.isSuccess()) {
			dprint("Simple Tree Gen cancelled by user.");
		} else {
			setStatusLabel("Generating simple tree..");
			SimpleTreeGenerator.Recipe r = new SimpleTreeGenerator.Recipe();
			r.min_degree = recipeDialog.getMinDegree();
			r.max_degree = recipeDialog.getMaxDegree();
			r.max_depth = recipeDialog.getDepth();
			r.pixel_width = gView.getWidth();
			r.pixel_height = gView.getHeight();
			SimpleTreeGenerator stg = new SimpleTreeGenerator(r);
			generateGraph(stg);
			setStatusLabel("Done.");
		}
	}
	
	/**
	 * Request clearing of the graph.  
	 * @return true if the graph was cleared
	 */
	protected boolean clearGraph() {
		String options[] = {"Yes", "Heck, no"};
		int n = JOptionPane.showOptionDialog(this, 
				"Are you sure you want to clear the current graph?",
				"Confirm",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);

		if (n == JOptionPane.YES_OPTION) {
			gModel.clear();
			gView.clearVertexSelection();
			return (true);
		}
		return (false);
	}

	/**
	 * Generate a new graph.
	 * @param gmg The model generation algorithm to use.
	 */
	protected void generateGraph(GraphModelGenerator gmg) {
		
		long max_vertex_id = 0;
		
		gModel.clear();
		iprint("Building graph...");
		try { 
			gmg.buildGraph();
		} catch (GMGException e) {
			iprint(e.getMessage());
			e.printStackTrace();
		}
		iprint("Installing graph...");
		ArrayList vertices = gmg.getVertexShapes();
		for (Iterator i = vertices.iterator(); i.hasNext(); ) {
			VertexShape vs = (VertexShape)i.next();
			if (vs.id > max_vertex_id) {
				max_vertex_id = vs.id;
			}
			gModel.vShape.addFirst(vs);
		}
		ArrayList edges = gmg.getEdgeShapes();
		for (Iterator i = edges.iterator(); i.hasNext(); ) {
			EdgeShape es = (EdgeShape)i.next();
			gModel.eShape.addFirst(es);
		}
		gModel.setNextVertexID(max_vertex_id + 1);
		gModel.refreshStats();
		repaint();
	}
	
	private void buildMenuBar() {
		menuBar = new JMenuBar();

		// File Menu
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem menuItem = new JMenuItem("Open Graph");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save Graph");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		// Edit Menu
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		
		menuItem = new JMenuItem("Clear Graph");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		// View Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Small");
		rbMenuItem.setMnemonic(KeyEvent.VK_S);
		rbMenuItem.addActionListener(this);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Medium");
		rbMenuItem.setMnemonic(KeyEvent.VK_M);
		rbMenuItem.setSelected(true);
		rbMenuItem.addActionListener(this);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);
		
		rbMenuItem = new JRadioButtonMenuItem("Big");
		rbMenuItem.setMnemonic(KeyEvent.VK_B);
		rbMenuItem.addActionListener(this);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);
		menu.addSeparator();
		
		menuItem = new JMenuItem("Debug");
		menuItem.setMnemonic(KeyEvent.VK_D);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		// Generate Menu
		menu = new JMenu("Generate");
		menu.setMnemonic(KeyEvent.VK_G);
		
		menuItem = new JMenuItem("Simple Tree");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuBar.add(menu);

		// assert appFrame != null
		appFrame.setJMenuBar(menuBar);
	}
	
	
	public void setStatusLabel(String s) {
		statusLabel.setText(s);
	}
	
	public void setStatsLabel(int n, int e, float ave_degree) {
		String s = "|V| " + Integer.toString(n) +
			", |E| " + Integer.toString(e) +
			", AveD " + Float.toString(ave_degree);
		statsLabel.setText(s);
	}

	private void buildSubpanels() {

		// status panel
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		statusLabel = new JLabel("GraphApp " + VERSION_STRING + ", Aaron Fabbri 2006");
		statusLabel.setVerticalAlignment(JLabel.TOP);
		statusLabel.setHorizontalAlignment(JLabel.LEFT);
		statsLabel = new JLabel("");
		statsLabel.setVerticalAlignment(JLabel.TOP);
		statsLabel.setHorizontalAlignment(JLabel.RIGHT);
		statusPanel.add(statusLabel);
		
		statusPanel.add(Box.createHorizontalGlue());
		
		// display pane
		displayPane = new JScrollPane(gView);
        displayPane.setPreferredSize(new Dimension(640, 480));
        displayPane.setViewportBorder(
                BorderFactory.createLineBorder(Color.black));
        statusPanel.add(statsLabel);
		
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
	}

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Grape Ape");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new GraphApp(frame);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
	/** Verbose debugging printfs */
	public static void dprint(String str) {
		if (debug_level >= LOG_DEBUG) {
			System.out.println(str);
		}
	}
	
	/** Informative printfs */
	public static void iprint(String str) {
		if (debug_level >= LOG_INFO) {
			System.out.println(str);
		}
	}

	public void windowStateChanged(WindowEvent e) {
		// TODO Auto-generated method stub
		iprint("wsc: " + e);
		
		/***
		 * r.min_degree = 2;
		r.max_degree = 4;
		r.max_depth = 5;
		r.pixel_height = gView.getHeight();
		r.pixel_width = gView.getWidth();
		SimpleTreeGenerator stg = new SimpleTreeGenerator(r);
		generateGraph(stg);
		**/
		
	
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent e) {
		dprint("wclosed: " + e);
		
	}

	public void windowClosing(WindowEvent e) {
		dprint("wclosing: " + e);
	}

	public void windowDeactivated(WindowEvent e) {
		dprint("wDeact: " + e);
		if (e.getWindow() == recipeDialog) {
			/* The input dialog has closed, generate graph */
			finishGenSimpleTree();
		}
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent e) {
		dprint("wOpend: " + e);
	}
    
}
