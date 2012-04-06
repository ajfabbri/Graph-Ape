package net.fabbrication.graph;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import sun.awt.WindowClosingListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * First example Graph Model Generator class.  Generates and draws acyclic 
 * (tree) graphs with randomly distributed or constant vertex degrees.
 * @author Aaron Fabbri
 */
public class SimpleTreeGenerator implements GraphModelGenerator {

	public static class Recipe {
		public int min_degree;
		public int max_degree;
		public int max_depth;
		public int pixel_width;
		public int pixel_height;
	}
	protected Recipe r;
	protected Random random;
	
	protected ArrayList vertexShapes;
	protected ArrayList edgeShapes;
	
	protected long next_vid = 1L;
	
	final static int MARGIN_PIXELS = 20;
	
	public SimpleTreeGenerator(Recipe r) {
		this.r = r;
	
		vertexShapes = new ArrayList();
		edgeShapes = new ArrayList();
		
		random = new Random();
	}
	
	public long getNextVid() {
		return (next_vid++);
	}
	
	public ArrayList getVertexShapes() {
		return (vertexShapes);
	}

	
	public ArrayList getEdgeShapes() {
		return (edgeShapes);
	}
	
	/**
	 * Determine locations for <code>num_children</code> vertices,
	 * which will be children of <code>parent</code>.
	 * @param num_children Number of vertices to generate coords. for.
	 * @param level 	Current level in the graph.  0 == root.	
	 * @param parent	Parent vertex or null.
	 * @return List of <code>Point</code> coordinates for vertices
	 */
	protected ArrayList getVertexCoords(int level, int num_children,
			VertexShape parent) {
	
		int y_spacing, x_spacing;
		int level_max_v;
		int y_coord;
		int i, x;
		ArrayList list;
		
		if (num_children == 0)
			return (null);
		
		list = new ArrayList(num_children);
		
		/* 
		 * Y-coords for all vertices start at MARGIN_PIXELS and go down to 
		 * (pixel_height-MARGIN_PIXELS).
		 */
		y_spacing = (r.pixel_height - (2*MARGIN_PIXELS)) / (r.max_depth);
	
		/* This level's row... */
		y_coord = MARGIN_PIXELS + (y_spacing * level);
		
		/* 
		 * X-coords are centered around the parent's X, with space enough to
		 * fit the worst case branching.
		 */
		if (parent == null) {
			x_spacing = 0;
			x = r.pixel_width / 2;
		} else {
			/* XXX this algorthim is thoopid */
			//double ave_degree = (r.max_degree * 3 + r.min_degree) / 4.0;
			if (level == 1) {
				// first level has full degree: root has no up-edge
				level_max_v = (int) r.max_degree;
			} else {
				level_max_v = (int)Math.pow((r.max_degree - 1),	(double)level)
						+ (int) r.max_degree;
			}
			// GraphApp.dprint("gvc: about " + Integer.toString(level_max_v) +
			// " vertices at level " + Integer.toString(level));
			x_spacing = (r.pixel_width - (2 * MARGIN_PIXELS)) / (level_max_v);

			// double check this logic vs. num_children-1
			x = ((int)parent.circle.getCenterX()) - (x_spacing * (num_children-1) / 2);
		}
		for (i = 0; i < num_children; i++) {
			Point p = new Point(x, y_coord);
			x = x + x_spacing;
			list.add(p);
		}
		
		return (list);
	}
	
	/**
	 * Generate a tree graph according to recipe. 
	 * @throws TGException 	If the recipe given to the 
	 * 		constructor cannot be generated.
	 */
	public void buildGraph() throws GMGException {
		
		LinkedList vfifo = new LinkedList();
		
		/* Base case: construct root. */
		TreeVertex vRoot = new TreeVertex(new Point(0,0), 0, null, getNextVid());
		vertexShapes.add(vRoot);
		
		/* Choose root's location */
		ArrayList coords = getVertexCoords(0, 1, null);
		// assert (!coords.isEmpty())
		
		Point ctr = (Point)coords.get(0);
		GraphApp.dprint("bg: root coords " + ctr.toString());
		vRoot.setCenterCoord(ctr);
		/* Seed fifo */
		vfifo.addFirst(vRoot);
		
		/* Main tree generation loop.  Could use recursion too. */
		while (! vfifo.isEmpty()) {
			TreeVertex v = (TreeVertex)vfifo.removeLast();
			
			int actual_degree = v.neighbors.size();
			int target_degree = chooseDegree();
			int needed_children = target_degree - actual_degree;
			
			/* Choose degree */
			v.setTargetDegree(target_degree);
			/*
			GraphApp.dprint("bgl: vtx " + v + 
					", ad " + Integer.toString(actual_degree) +
					", td " + Integer.toString(target_degree) +
					", nc " + Integer.toString(needed_children) + 
					", choose " + target_degree);
			*/
			coords = getVertexCoords(v.getLevel()+1, needed_children, v);
			
			/* Create needed children, calculate positions, and push on fifo. */
			for (int i = 0; i < needed_children; i++) {
				TreeVertex child = 
					new TreeVertex((Point)coords.get(i), v.getLevel()+1, v, getNextVid());
				v.neighbors.add(child);
				child.neighbors.add(v);
				vertexShapes.add(child);
				EdgeShape edge = new EdgeShape(v, child);
				edgeShapes.add(edge);
				if (child.getLevel() < r.max_depth) {
					vfifo.addFirst(child);
				}
			}
			
			
		}
		
	}

	protected int chooseDegree() {
		
		int degree;
		if (r.min_degree > r.max_degree)
			r.min_degree = r.max_degree;
		if (r.min_degree == r.max_degree)
			return (r.min_degree);
		
		degree = random.nextInt(r.max_degree - r.min_degree + 1) + r.min_degree;
		return (degree);
	}

	public void actionPerformed(ActionEvent e) {
		GraphApp.dprint("stgui-action: " + e);
	}
}
