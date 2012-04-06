package net.fabbrication.graph;

import java.applet.Applet;
import java.awt.Graphics;

/**
 * Applet wrapper for the <code>GraphApp</code> application.
 * @author Aaron Fabbri
 */
public class GraphApplet extends Applet {

    StringBuffer buffer;

    public void init() {
        buffer = new StringBuffer();
        addItem("Initializing...");
    }

    public void start() {
        addItem("Starting GraphApp...");
//      Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GraphApp.createAndShowGUI();
            }
        });
    }

    public void stop() {
        addItem("Stopping... ");
    }

    public void destroy() {
        addItem("Unloading...");
    }

    private void addItem(String newWord) {
        System.out.println(newWord);
        buffer.append(newWord);
        repaint();
    }

    public void paint(Graphics g) {
	//Draw a Rectangle around the applet's display area.
        g.drawRect(0, 0, 
		   getWidth() - 1,
		   getHeight() - 1);

	//Draw the current string inside the rectangle.
        g.drawString(buffer.toString(), 5, 15);
    }
}
