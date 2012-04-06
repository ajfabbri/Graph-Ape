package net.fabbrication.graph;

import java.util.*;

/**
 * A vertex in the graph theory sense.
 * @author Aaron Fabbri
 */
public class Vertex {

	public static final long INVALID_ID = -1;
	public static final int V_NONE = 0;
	public static final int V_SELECTED = 1;
	
	
	/** Lazy init this list for performance: may be null */
	protected LinkedList neighbors;
	protected String description;
	
	protected int state;
	public long id; /* XXX */

	
	public Vertex() {
		neighbors = new LinkedList();
		description = null;
		id = Long.MIN_VALUE;
	}
	
	public long getDegree() {
		if (neighbors == null) {
			return 0;
		} else {
			return neighbors.size();
		}
	}
	
	/**
	 * Add new neighbor to this vertex.  Does not check if v has already been added.
	 * @param v 	adjacent vertex
	 */
	public void addEdge(Vertex v) {
		addEdge2(v, false);
	}
	
	/**
	 * Remove edge from this vertex to v.
	 * @param v
	 * @return 	true if edge existed and was removed
	 */
	public boolean deleteEdge(Vertex v) {
		if (neighbors == null) {
			return false;
		}
		return neighbors.remove(v);
	}
	
	/**
	 * Add new edge, optionally checking if it already exists.
	 * @param v		adjacent vertex
	 * @param checkFirst	when true, if v is already adjacent, do nothing	
	 */
	public void addEdge2(Vertex v, boolean checkFirst) {
		/* assert v != null; */
		if (checkFirst && neighbors != null && (neighbors.contains(v))) {
			return;
		} else {
			if (neighbors == null) {
				neighbors = new LinkedList();
			}
			neighbors.add(v);
		}
	}
	
	/**
	 * Get list of neighbors.
	 * @return null or list of adjacent vertices
	 */
	public LinkedList getNeighbors() {
		return neighbors;
	}
	
	public boolean removeNeighbor(Vertex u) {
		return neighbors.remove(u);
	}
	
	/** Vertices are uniquely identified by <code>id</code>. */
	public boolean equals(Vertex v) {
		return (v.id == this.id);
	}

	/**
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	public String toString() {
		return (Long.toString(id));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
