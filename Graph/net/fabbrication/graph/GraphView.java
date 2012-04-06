package net.fabbrication.graph;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

/**
 * View and Controller class for graph drawing and editing pane.
 * @author Aaron Fabbri
 *
 */
public class GraphView extends JComponent 
	implements MouseListener, MouseMotionListener, KeyListener {

	/** Shush serialization warning. */
	private static final long serialVersionUID = 1L;

	GraphApp app;

	protected class GvEditState {	
		public static final int GVE_READY = 0; 			/* initial state */
		public static final int GVE_VERTEX_MOVE = 1; 	/* dragging a vertex */
		public static final int GVE_VERTEX_CREATE = 2;	/* creating a vertex ? */
		public static final int GVE_EDGE_CREATE = 3; 	/* selected first v for edge, 
														   waiting for second */
		public static final int GVE_VERTEX_SELECT = 4; 	/* vertex is selected */
		public static final int GVE_EDGE_SELECT = 5;	/* edge is selected */
		public int s;
		public GvEditState(int v) {
			s = v;
		}
	}	
	
	protected GvEditState edit_state;
	
	private int r_size;
	
	/* XXX PERF should use object references instead */
	protected long selected_vertex;
	protected long dragged_vertex;
	
	public GraphView(GraphApp app) {
		this.app = app;
		edit_state = new GvEditState(GvEditState.GVE_READY);
		selected_vertex = Vertex.INVALID_ID;
		dragged_vertex = Vertex.INVALID_ID;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
		setRenderSize(VertexShape.VS_MEDIUM);
	}
	
	public boolean clearVertexSelection() {
		boolean change;
		if (selected_vertex == Vertex.INVALID_ID) {
			change = false;
		} else {
			selected_vertex = Vertex.INVALID_ID;
			change = true;
		}
		return (change);
	}
	
	protected boolean clearVertexDragged() {
		boolean change;
		if (dragged_vertex == Vertex.INVALID_ID) {
			change = false;
		} else {
			dragged_vertex = Vertex.INVALID_ID;
			change = true;
		}
		return (change);
	}

	protected void userAddEdge(long v1_id, long v2_id) {
		/* confirm action with user */
		Object options[] = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this, 
				"Do you want to add and edge from vertex " +
				Long.toString(v1_id) + " to vertex " +
				Long.toString(v2_id) + "?",
				"Confirm",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);

		if (n == JOptionPane.YES_OPTION) {
			try {
				app.gModel.addEdge(v1_id, v2_id);
			} catch (GraphException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
	public void mouseClicked(MouseEvent e) {
		boolean refresh = false;
		//app.dprint("mclick: " + e);
		switch (edit_state.s) {
		case GvEditState.GVE_READY:
			
			GraphModel.GHitDetectResult result;
			result = app.gModel.hitDetect(e.getPoint());
			app.iprint(result.toString() + ", Selected is " + 
						Long.toString(selected_vertex));
			// Handle vertex clicks
			if (result.result == GraphModel.GHitDetectResult.GHD_VERTEX) {
				// Toggle vertex selection
				Vertex v = result.vertex;
				app.dprint("Clicked vertex " + Long.toString(v.id));		
				/* clicked vertex, none selected */
				if (selected_vertex == Vertex.INVALID_ID) {
					selected_vertex = v.id;
				}
				/* vertex is selected, clicked other vertex */
				else if (selected_vertex != v.id) {
					userAddEdge(selected_vertex, v.id);
				} 
				/* clicked selected vertex */
				else {
					clearVertexSelection();
				}
				refresh = true;
				
		}
		else if (result.result == GraphModel.GHitDetectResult.GHD_EDGE) {
				app.iprint("Edge detected!?  TODO");
			} else if (result.result == GraphModel.GHitDetectResult.GHD_NONE) {
				boolean ok = app.gModel.addVertex(e.getPoint());
				app.iprint("Create vertex success " + Boolean.toString(ok) + " " +
						Integer.toString(r_size));
				refresh = true;
			}
			break;
			
		case GvEditState.GVE_EDGE_CREATE:
			//TODO
			break;

		case GvEditState.GVE_VERTEX_MOVE:
			//TODO
			break;
		}
	
		if (refresh) {
			app.repaint();
			e.consume();
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		//app.dprint("menter: " + e);
	}

	public void mouseExited(MouseEvent e) {
		//app.dprint("mexit: " + e);
	}

	public void mousePressed(MouseEvent e) {
		//app.dprint("mpress: " + e);
		boolean refresh = false;
		switch (edit_state.s) {
		case GvEditState.GVE_READY:
			
			GraphModel.GHitDetectResult result;
			result = app.gModel.hitDetect(e.getPoint());
			// Handle vertex drags
			if (result.result == GraphModel.GHitDetectResult.GHD_VERTEX) {
				Vertex v = result.vertex;
				app.dprint("Pressed vertex " + Long.toString(v.id));
				dragged_vertex = v.id;
				refresh = true;
				e.consume();
			}
		}
		if (refresh) {
			app.repaint();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		app.dprint("mrel: " + e);
		if (clearVertexDragged()) {
			app.repaint();
			e.consume();
		}
	}
	
	/** 
	 * Paint this graph display pane.
	 */
	public void paint(Graphics g) {
		// assert app
	
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setPaint(Color.BLACK);
	    
	    for (Iterator i = app.gModel.getEdgeIter(); i.hasNext(); ) {
	    	EdgeShape es = (EdgeShape)i.next();
	    	es.render(g2);
	    }
	    for (Iterator i = app.gModel.getVertexIter(); i.hasNext(); ) {
	    	VertexShape vs = (VertexShape)i.next();
	    	//app.dprint("Drawing shape " + v);
	    	if (selected_vertex == vs.id) {
	    		vs.setState(Vertex.V_SELECTED);
	    	} else {
	    		vs.setState(Vertex.V_NONE);
	    	}
	    	vs.render(g2, getRenderSize());
	    }
	}


	public void keyPressed(KeyEvent e) {
		
	
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		/*	char c = e.getKeyChar();
		switch (c) {
		case "" :
			break;
		}
		*/
	
		app.dprint("Key Pressed: " + e);
		int c = e.getKeyChar();
		if (c == KeyEvent.VK_DELETE) {
			if (selected_vertex != Vertex.INVALID_ID) {
				app.iprint("Removing vertex " + Long.toString(selected_vertex));
				app.gModel.removeVertex(selected_vertex);
				clearVertexSelection();
				e.consume();
				app.repaint();
			}
		}	
	}

	public void mouseDragged(MouseEvent e) {
		//app.dprint("mDrag: " + e);
		if (dragged_vertex != Vertex.INVALID_ID) {
			app.gModel.moveVertex(dragged_vertex, e.getPoint());
			e.consume();
			app.repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		//app.dprint("mMove: " + e);
	}

	/**
	 * @param s The render size to set.
	 */
	public void setRenderSize(int s) {
		this.r_size = s;
		//app.dprint("gView: render size now " + Integer.toString(this.r_size));
	}
	
	public int getRenderSize() {
		//app.dprint("gView: getRenderSize -> " + Integer.toString(this.r_size));
		return (this.r_size);
	}
}
