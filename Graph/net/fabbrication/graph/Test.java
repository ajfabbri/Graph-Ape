package net.fabbrication.graph;

/** Dummy class for throwing unit test code into. */
public class Test {

	GraphApp app;
	GraphView gView;
	
	public Test() {
		app = new GraphApp(null, true);
		app.iprint("Test run...");

		app.iprint("gView.getRenderSize ->" + 
				Integer.toString(app.gView.getRenderSize()));
		app.iprint("gView.getRenderSize ->" + 
				Integer.toString(app.gView.getRenderSize()));
		app.iprint("gView.setRenderSize(VS_BIG) aka " + 
				Integer.toString(VertexShape.VS_BIG));
		app.gView.setRenderSize(VertexShape.VS_BIG);
		app.iprint("gView.getRenderSize ->" + 
				Integer.toString(app.gView.getRenderSize()));
		app.iprint("gView.getRenderSize ->" + 
				Integer.toString(app.gView.getRenderSize()));
		app.iprint("gView.getRenderSize ->" + 
				Integer.toString(app.gView.getRenderSize()));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Test t = new Test();
		if (t != null) { t = null; } // shutup warnings
	}

}
