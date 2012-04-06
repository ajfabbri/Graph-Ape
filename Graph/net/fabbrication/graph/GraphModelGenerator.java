/**
 * 
 */
package net.fabbrication.graph;

import java.util.ArrayList;

/**
 * Graph Model Generators are classes which generate a graph and it's associated
 * drawing.
 * @author Aaron Fabbri
 */
public interface GraphModelGenerator {

	public class GMGException extends Exception {
		public GMGException(String msg) {
			super(msg); 
		}
	}
	
	
	public void buildGraph() throws GMGException;
	public ArrayList getVertexShapes();
	public ArrayList getEdgeShapes();
}
