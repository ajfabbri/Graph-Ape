/**
 * 
 */
package net.fabbrication.graph;

import java.awt.Point;

/**
 * VertexShape subclass which includes state useful for tree generation and 
 * drawing. 
 * @author Aaron Fabbri
 */
public class TreeVertex extends VertexShape {
	
	/** Tree depth.  Root == 0 */
	protected int 			level;
	/** Parent vertex (toward the root), or null. */
	protected TreeVertex	parent;
	/** The degree (num. neighbors) this vertex aspires to have. */
	protected int			target_degree;
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public TreeVertex getParent() {
		return parent;
	}

	public void setParent(TreeVertex parent) {
		this.parent = parent;
	}

	/**
	 * @param x			X-coord for painting
	 * @param y			Y-coord for painting.
	 * @param level		Level in tree.  Root is 0.
	 * @param parent	Parent vertex or null.
	 */
	public TreeVertex(Point p, int level, TreeVertex parent, long id) {
		this(p.x, p.y, VertexShape.VS_MEDIUM);
		this.setId(id);
		this.level = level;
		this.parent = parent;
	}
	
	/**
	 * @param x			X-coord for painting
	 * @param y			Y-coord for painting.
	 * @param size		e.g. VertexShape.VS_MEDIUM
	 */
	public TreeVertex(int x, int y, int size) {
		super(x, y, size);
		// TODO Auto-generated constructor stub
	}

	public int getTargetDegree() {
		return target_degree;
	}

	public void setTargetDegree(int target_degree) {
		this.target_degree = target_degree;
	}

}
