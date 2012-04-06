package net.fabbrication.graph;

/**
 * Generic exception class for the graph package.
 * @author Aaron Fabbri
 */
public class GraphException extends Exception {

	/** Shush serialization warning. */
	private static final long serialVersionUID = 1L;

	public GraphException(String s) {
		super(s);
	}
	public String getMessage() {
		return ("Generic Graph Error");
	}
}
