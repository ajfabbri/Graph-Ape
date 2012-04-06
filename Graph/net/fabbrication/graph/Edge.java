package net.fabbrication.graph;

/** Simple representation of an edge in a graph. */
public class Edge {

	protected Vertex u;
	protected Vertex v;
	
	protected boolean is_directed;
	
	public Edge(Vertex u, Vertex v) {
		this(u, v, false);
	}
	
	public Edge(Vertex u, Vertex  v, boolean is_directed) {
		this.is_directed = is_directed;
		this.u = u;
		this.v = v;
	}
	
	public boolean equals(Edge e) {
		return ((this.u == e.u && this.v == e.v) || 
			(this.v == e.u && this.u == e.v));
	}
	
	public boolean hasVertex(Vertex v) {
		return (this.u == v || this.v == v);
	}
}
