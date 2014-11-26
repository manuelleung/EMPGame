package edu.emp.gameworld;

public class Node {
	private float x, y;
	private int nodeWidth, nodeHeight;
	private NodeType type = NodeType.NONE;
	
	/*These three floats are for path scoring, F Cost, G Cost and Heuristical value*/
	private float F, G, H;
	
	/*Parent Node is used for tracing the final path. 
	 * The node set to be the parent should be the node that leads the shortest route from
	 * the End to the Start*/
	private Node parent = null;
	
	public Node(float x, float y, int nodeWidth, int nodeHeight) {
		this.x = x;
		this.y = y;
		this.nodeWidth = nodeWidth;
		this.nodeHeight = nodeHeight;
	}

	public void calculateNodeCost(Node parent, Node Start, Node end) {
		this.setParent(parent);
		if (parent != null) {
			/*G Cost is movement cost between this node's parent and itself.
			 * If Horizontal/Vertical, the G Cost is 10.
			 * If Diagonal, the G Cost is sqrt(10^2 + 10^2) = 14.44... or 14 for rounding.*/
			if (Math.abs(x - parent.x) != 0 && Math.abs(y - parent.y) != 0){
				G = parent.G + 14;
			}
			else {
				G = parent.G + 10;
			}
		}
		/*Heuristic Value is the distance between this node and the end node. It's used to optimise the search
		 *for the best node in each turn.		
		 */
		/*Manhattan Method of Calculating Heuristic, simpler to calculate but innaccurate for 8-way pathfinding*/
		H = ((Math.abs(x-end.x)/nodeWidth) + (Math.abs(y - end.y)/nodeHeight)) * 10;
		
		/* We are only use up, down, right left...
		//Diagonal Shortcut of Calculating Heuristic, takes diagonal movements into account but slower.
		float xDistance = (Math.abs(x-end.x)/tileWidth);
		float yDistance = (Math.abs(end.y-y)/tileHeight);
		
		if (xDistance > yDistance)
			H = 14*yDistance + 10*(xDistance-yDistance);
	    else
	    	H = 14*xDistance + 10*(yDistance-xDistance);
		*/
		
		/*F cost is the sum of G cost and Heuristic, and is used to compare each node to see which
		 *is closest to the end node. */
		F = G + H;
	}
	
	public void reset() {
		F = G = H = 0;
		setParent(null);
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getF() {
		return F;
	}
	public float getH() {
		return H;
	}
	public float getG() {
		return G;
	}
	public void setX(float x) {
		this.x=x;
	}
	public void setY(float y) {
		this.y=y;
	}
	public void setF(float F) {
		this.F=F;
	}
	public void setG(float G) {
		this.G=G;
	}
	public void setH(float H) {
		this.H=H;
	}
	public NodeType getType() {
		return type;
	}
	public void setType(NodeType type) {
		this.type = type;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
}
