
package net.fabbrication.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A graph and how it is currently drawn.  See Model View Controller
 * software architecture for GUIs.
 * @author Aaron Fabbri
 */
public class GraphModel {

	/** Main application object. */
	protected GraphApp app;
	
	/** 
	 * Result of a graph hit detection query on a <code>GraphModel</code>.
	 * @author Aaron Fabbri
	 */
	public class GHitDetectResult {
		public final static int GHD_NONE = 0;
		public final static int GHD_VERTEX = 1;
		public final static int GHD_EDGE = 2;
		
		public int result;
		public Vertex vertex;
		/** iff result is <code>GHD_EDGE</code>, this is defined */
		public Vertex neighbor;
	
		public String toString() {
			String s = "Hit ";
			switch (result) {
			case GHD_NONE:
				s = s + "None";
				break;
			case GHD_VERTEX:
				s = s + "Vertex " + vertex;
				break;
			case GHD_EDGE:
				s = s + "Edge (" + vertex + ") -> (" + neighbor +
				")";
			}
			return (s);
		}
	}
	
	/** A graph in the mathematical sense: vertices and edges. 
	 * XXX redundant, should be input parameter */
	//protected Graph g;
	
	/** For each vertex in <code>g</code>, it's location and shape in the drawing. */
	protected LinkedList vShape;
	
	/** For each edge, it's location and shape in the drawing. */
	protected LinkedList eShape;
	
	/** Keep track of next autogenerated vertex id. */
	private long next_vertex_id;	
	
	/** Constructor. */
	public GraphModel(GraphApp app) {
		
		this.app = app;
		vShape = new LinkedList();
		eShape = new LinkedList();
	}
	
	/**
	 * Given the current drawing of the graph and a point on the grid,
	 * this function detects whether or not the point lies on any objects 
	 * in the drawing.  Performance need not be optimal; graphs should be
	 * small and frequency is limited by the user.
	 * @param point
	 * @return
	 */
	public GHitDetectResult hitDetect(Point2D point) {
		
		GHitDetectResult result = new GHitDetectResult();
		/* Search vertices.. */
		for (Iterator i = vShape.iterator(); i.hasNext(); ) {
			VertexShape vs = (VertexShape)i.next();
			if (vs.circle.contains(point)) {
				result.result = GHitDetectResult.GHD_VERTEX;
				result.vertex = (Vertex)vs;
				return (result);
			}
		}
		
		/* Search edges */
		for (Iterator i = eShape.iterator(); i.hasNext(); ) {
			EdgeShape es = (EdgeShape)i.next();
			if (es.line.contains(point)) {
				result.result = GHitDetectResult.GHD_EDGE;
				result.vertex = (Vertex)es.u;
				result.neighbor = (Vertex)es.v;
				return (result);
			}
		}
		
		/* Default return none.. */
		result.result = GHitDetectResult.GHD_NONE;
		return (result);
	}
	
	protected void refreshStats() {
		int n = vShape.size();
		int e = eShape.size();
		float ave_d = 0.0f;
		
		/* calc ave. degree */
		for (Iterator i = vShape.iterator(); i.hasNext(); ) {
			ave_d += ((Vertex)i.next()).neighbors.size();
		}
		if (n > 0)
			ave_d = ave_d / n;
		app.setStatsLabel(n, e, ave_d);
	}
	
	/** Bounds checking must be performed by caller. */
	boolean addVertex(Point2D point) {
		/*float x, y;*/
		// XXX layering violation...
		VertexShape vs = new VertexShape((int)point.getX(),
				(int)point.getY(), app.gView.getRenderSize());
		
		vs.id = next_vertex_id;
		next_vertex_id++;
		
		/*int r = vs.getRadius();
		x = (float)point.getX() - r;
		y = (float)point.getY() - r;
		
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		vs.circle = new Ellipse2D.Float(x, y, r*2, r*2);
		*/
		vShape.add(vs);
		refreshStats();
		
		return (true);
	}
	
	public boolean addEdge(long u_id, long v_id) throws GraphException{
	
		VertexShape u,v;
		EdgeShape e;
		u = getVertexShapeById(u_id);
		v = getVertexShapeById(v_id);
		 
		if (u == null || v == null) {
			throw new GraphException("teh sux");
		}
		
		if (u.neighbors.contains(v)) {
			app.iprint("gm.addEdge(): " + u + " is already" +
					" adjacent to " + v);
			return (false);
		} else {
			/* 
			 * Add edge. Invariant: symmetry in neighbor lists.
			 */
			v.neighbors.add(u);
			u.neighbors.add(v);
			e = new EdgeShape(u,v);
			eShape.add(e);
		}
		refreshStats();
		return (true);
	}
	
	/** Clear graph. */
	void clear() {
		while (vShape.size() > 0) {
			vShape.removeFirst();
		}
		while (eShape.size() > 0) {
			eShape.removeFirst();
		}
		next_vertex_id = 0L;
		refreshStats();
	}
	
	/**
	 * Remove a vertex and associated edges.
	 * @param v
	 * @return true on success
	 */
	boolean removeVertex(Vertex v) {
	
		Vertex u;
		
		/* PERF*/
		if (!vShape.contains(v))
			return (false);
		
		// Tell neighbors we're leaving
		// Invariant: symmetry in neighbor lists
		for (Iterator i = v.neighbors.iterator(); i.hasNext(); ) {
			u = (Vertex)i.next();
			u.removeNeighbor(v);
		}
		
		// Remove edges
		LinkedList dead_edges = new LinkedList();
		for (Iterator i = eShape.iterator(); i.hasNext(); ) {
			Edge e = (Edge)i.next();
			if (e.hasVertex(v)) {
				dead_edges.add(e);
			}
		}
		eShape.removeAll(dead_edges);
		
		// Remove vertex
		vShape.remove(v);
		return (true);
	}
	
	protected VertexShape getVertexShapeById(long id) {
		VertexShape vs;
		/* PERF */
		for (Iterator i = vShape.iterator(); i.hasNext(); ) {
			vs = (VertexShape)i.next();
			if (vs.id == id) {
				return (vs);
			}
		}
		return (null);
	}
	
	boolean removeVertex(long id) {
		VertexShape vs = getVertexShapeById(id);
		if (vs != null) {
			return removeVertex((Vertex)vs);
		}
		return (false);
	}
	
	boolean moveVertex(long id, Point2D point) {
		VertexShape vs = getVertexShapeById(id);
		int r = vs.getRadius();
		if (vs == null) {
			return (false);
		}
		int x = (int)point.getX() - r;
		int y = (int)point.getY() - r;
		
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		
		vs.circle.setFrame(new Point(x,y), new Dimension(r*2, r*2));
	
		// update affected edges
		for (Iterator i = eShape.iterator(); i.hasNext() ;) {
			EdgeShape es = (EdgeShape)i.next();
			if (es.hasVertex(vs)) {
				es.updateShape();
			}
		}
		return (true);
	}
	/** Initial slow implementation. XXX Performance. */
	public ArrayList getShapes() {
		ArrayList shapes = new ArrayList(vShape.size());
		for (Iterator i = vShape.iterator(); i.hasNext() ;) {
			VertexShape vs = (VertexShape)i.next();
			shapes.add(vs.circle);
		}

		// TODO edges, lables
		return (shapes);
	}
	
	public Iterator getVertexIter() {
		return vShape.iterator();
	}
	
	public Iterator getEdgeIter() {
		return eShape.iterator();
	}
	
	public void setNextVertexID(long vid) {
		next_vertex_id = vid;
	}
}




