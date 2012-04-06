<?php
	include("../../../pro-template.php");
	$title = "GraphApp Launcher";
	gen_head($title);
?>
	<p class="projname">
	Graph Editing and Drawing Tool applet launcher 	
	</p>

	<p class="warning">01-22-2005 - Posted version 0.1. </p>

	<h3>GraphApp 0.1 Applet</h3>

<p align=center>
<applet archive="graph.jar" code="net/fabbrication/graph/GraphApplet.class" width=250 height=40
	codebase="/pro/project/GraphApp/GraphApp-0.1/"
           alt="(Applet 'GraphApplet' should be displayed here.)">
     <font color="#E70000">
     (Applet "GraphApplet" would be displayed here<br>
     if you had Java installed correctly.)</font>
</applet>
</p>

	<h3>Some things to try</h3>
	<ul>
	<li>Click to create vertices.  Click on a vertex to highlight it and to
unhighlight it.
	<li>When a vertex is highlighted, clicking on another allows you to
create an edge.
	<li>When a vertex is highlighted, pressing the Delete key will remove
it.
	<li>A tree generation algorithm is available in the Generate menu.</li>

	<li>Save and Load Graph functions are not yet implemented.
</ul>
	
<?php
	gen_tail($title);

?>
	
