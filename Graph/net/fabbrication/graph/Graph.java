/**
 * 
 */
package net.fabbrication.graph;
import java.util.*;
import java.io.*;

/**
 * Undirected graph representation.
 * @author Aaron Fabbri
 */
public class Graph implements Externalizable {

	/** For Java serialization. */
	private static final long serialVersionUID = 1L;

	protected LinkedList V;
	
	public Graph() {
		V = new LinkedList();
	}
	
	public LinkedList getVertices() {
		return V;
	}
	
    public void writeExternal(ObjectOutput out) throws IOException {
    
    	// for each vertex, out.writeObject
    }
    
    public void readExternal(ObjectInput in) throws IOException, 
                             java.lang.ClassNotFoundException {
    	
    	// for each vertex, in.readObject
    }
	
	
	
}
