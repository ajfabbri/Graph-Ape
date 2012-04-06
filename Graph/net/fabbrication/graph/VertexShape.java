/**
 * 
 */
package net.fabbrication.graph;

import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * A Vertex which knows how to paint its self.
 * @author Aaron Fabbri
 */
class VertexShape extends Vertex {

	/** @see render(), setRenderSize() */
	public static final int VS_SMALL = 0;
	public static final int VS_MEDIUM = 1;
	public static final int VS_BIG = 2;
	protected static final int font_sizes[] = {12, 16, 20};
	protected static final float stroke_widths[] = {2.0f, 2.0f, 3.0f};
	protected static final int vertex_radii[] = {10, 20, 40};
	protected int render_size;

	protected static final String label_font = "Courier";
	protected static final Color FILL_SELECTED = Color.YELLOW;
	protected static final Color FILL_DEFAULT = Color.WHITE;
	
	protected Ellipse2D.Float circle;

	protected Stroke vStroke;
	protected Font vFont;
	protected int label_offs_x;
	protected int label_offs_y;
	
	public VertexShape(int x, int y, int size) {
		
		super();
		label_offs_x = label_offs_y = 0;
		circle = new Ellipse2D.Float(x, y, 0, 0);
		this.setRenderSize(size, true);
	}

	public int getRadius() {
		return (vertex_radii[render_size]);
	}
	
	/** 
	 * Set which size to draw this vertex.
	 * @param size VS_SMALL, VS_MEDIUM, or VS_BIG
	 */
	protected void setRenderSize(int size) {
		setRenderSize(size, false);
	}
	
	/** 
	 * Set render size, only regenerating shapes when it has changed.
	 * @param size
	 * @param force_update  true to force rebuilding of shapes and fonts.
	 */
	protected void setRenderSize(int size, boolean force_update) {
		if (size < VS_SMALL || size > VS_BIG) {
			System.out.println("vs: setRenderSize val " + Integer.toString(size) +
					" out of bounds");
			size = VS_MEDIUM;
		}
		
		//System.out.println("vs.setRenderSize: size " + Integer.toString(size) +
		//		", render_size " + Integer.toString(render_size));
		if (size != render_size || force_update) {
			vFont = new Font(label_font, Font.PLAIN, font_sizes[size]);
			vStroke = new BasicStroke(stroke_widths[size]);
			circle.setFrameFromCenter(circle.getCenterX(), circle.getCenterY(),
					circle.getCenterX() + vertex_radii[size], 
					circle.getCenterY() + vertex_radii[size]);
					label_offs_y = 0;
			}
			render_size = size;
	}
	
	public void setCenterCoord(Point p) {
		int r = getRadius();
		circle.setFrame(new Point(p.x, p.y), new Dimension(r*2, r*2));
	}
	
	protected void drawLabel(Graphics2D g, String label) {
		int x, y;
		
		/* 
		 * Only do this once in the paint fast path, 
		 * assuming labels are similar sizes. Could be smarter. 
		 */
		if (label_offs_y== 0) {
			FontMetrics fm = g.getFontMetrics();
			label_offs_x = fm.stringWidth(label) / 2;
			label_offs_y = fm.getHeight() / 3;
		}
		x = (int)circle.getCenterX() - label_offs_x;
		y = (int)circle.getCenterY() + label_offs_y;
		g.drawString(label, x, y);
	}
	
	/** 
	 * Draw this vertex. 
	 * @param size VS_SMALL, VS_MEDIUM, or VS_BIG
	 */
	public void render(Graphics2D g, int size) {
	
		String label;
		
		//System.out.println("vs.render("+ Integer.toString(size) + ")");
		this.setRenderSize(size);
		
		/* Paint background first.. */
		Paint p0 = g.getPaint();
		if (state == V_SELECTED) {
			/* highlight selected vertices */
			g.setPaint(FILL_SELECTED);
			g.fill(circle);
		} else {
			g.setPaint(FILL_DEFAULT);
			g.fill(circle);
		}
		g.setPaint(p0);
		
		/* paint vertex outline */
//		Stroke s0 = g.getStroke();
		g.setStroke(vStroke);
		g.draw(circle);
	//	g.setStroke(s0);
		
		/* draw label */
		label = Long.toString(id);
		g.setFont(vFont);
		drawLabel(g, label);
	}
	
	/** Performance could be better. */
	public int hashCode() {
		int x = (int)Math.floor(circle.getCenterX());
		int y = (int)Math.floor(circle.getCenterY());
		return (new Point(x,y)).hashCode();
	}
}