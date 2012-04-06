/**
 * 
 */
package net.fabbrication.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;


/** 
 * An edge and it's associated shape in a drawing.  This simple edge class 
 * only supports straight lines.
 * @author Aaron Fabbri
 */
class EdgeShape extends Edge {
	protected Line2D.Float line;
	protected Stroke eStroke;
	
	protected Color default_stroke;
	
	/**
	 * @TODO support for directed edges.
	 * @param u
	 * @param v
	 */
	public EdgeShape(VertexShape u, VertexShape v) {
		super((Vertex)u, (Vertex)v);
		eStroke = new BasicStroke(2.0f);
		default_stroke  = new Color(128,64,64);
		line = new Line2D.Float((float)u.circle.getCenterX(), 
				(float)u.circle.getCenterY(),
				(float)v.circle.getCenterX(), 
				(float)v.circle.getCenterY());
	}

	public void updateShape() {
		line.setLine(((VertexShape)super.u).circle.getCenterX(),
				((VertexShape)super.u).circle.getCenterY(),
				((VertexShape)super.v).circle.getCenterX(),
				((VertexShape)super.v).circle.getCenterY());
	}
	
	/**
	 * @return Returns the line.
	 */
	public Line2D.Float getLine() {
		return line;
	}

	/**
	 * @param line The line to set.
	 */
	public void setLine(Line2D.Float line) {
		this.line = line;
	}
	

	public void render(Graphics2D g) {
		g.draw(line);
	}
}