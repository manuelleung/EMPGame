package edu.emp.gameworld;

import com.badlogic.gdx.utils.Array;

public class PathFinder {
	private Array<Array<Node>> grid = new Array<Array<Node>>(); // Grid of map
	private Array<Node> openPath = new Array<Node>(); // List for checked nodes
	private Array<Node> closedPath = new Array<Node>(); // Nodes to be used for final
	private Array<Node> finalPath = new Array<Node>(); // Path found
	private int nodeWidth, nodeHeight; // Width and height of node (Tile size)
	private int startX = -1, startY = -1; // Start x and y coordinates
	private int endX = -1, endY = -1; // Ending x and y coordinates
	
	private boolean foundPath = true;
	private boolean noPath = false;
	
	public PathFinder(int screenWidth, int screenHeight, int tileSize) {
		for (int y = 0; y < (int)(screenHeight/tileSize); y++)
		{
			grid.add(new Array<Node>());
			for (int x = 0; x < (int)(screenWidth/tileSize); x++)
				grid.get(y).add(new Node(x * tileSize, y * tileSize, tileSize, tileSize));
		}
		nodeWidth = tileSize;
		nodeHeight = tileSize;
	}
	
	public boolean findPath() {
		/*Clear current path*/
		openPath.clear();
		closedPath.clear();
		finalPath.clear();
		/*Reset all F, G and H values to 0*/
		for (int y = 0; y < grid.size; y++) {
			for (int x = 0; x < grid.get(y).size; x++) {
				grid.get(y).get(x).reset();
			}
		}

		
		
		if (startX == -1 || startY == -1 || endX == -1 || endY == -1) {
			/*If no Start or End nodes have been set, quit the findPath.*/
			return noPath;
		}
		/*If Start = End, return found.*/
		else if (startX == endX && startY == endY) {
			System.out.println("SAME LOCATION");
			return noPath;
		}
		/*else if(grid.get(endY).get(endX).getType()==NodeType.END || grid.get(endY).get(endX).getType()==NodeType.BLOCKED) {
			System.out.println("BLOCKED");
			return noPath;
		}*/
		else {
			openPath.add(grid.get(startY).get(startX)); //Add Start node to open
			setOpenList(startX, startY); //Set neighbors for Start node.
			closedPath.add(openPath.first()); //Add Start node to closed.
			openPath.removeIndex(0); //Remove Start Node from open.
			/*If the last value in the closedPath array isn't the end node, go through the while loop*/
			while (closedPath.peek() != grid.get(endY).get(endX)) {				
				if (openPath.size != 0) {
					float bestF = 9999; //Big number to check F and hold F value
					int bestFIndex = -1; //Index of the new node with best F value
					//Get node with lowest F cost in the open list.
					for (int i = 0; i < openPath.size; i++) {
						if (openPath.get(i).getF() < bestF) {
							bestF = openPath.get(i).getF();
							bestFIndex = i;
						}
					}
					if (bestFIndex != -1) {
						closedPath.add(openPath.get(bestFIndex)); //Add node to closed list
						openPath.removeIndex(bestFIndex); //remove from open list
						//Set Neighbours for parent
						setOpenList((int)(closedPath.peek().getX()/nodeWidth), (int)(closedPath.peek().getY()/nodeHeight));
					}
					else {
						return noPath;
					}
				}
				else {
					return noPath;
				}
			}
		}
		
		/*Time to get our final path*/
		/*Add our end node to the final path*/
		Node tempNode = closedPath.peek();
		finalPath.add(tempNode);
		/*Then while our last finalPath element is not the start node...*/
		while (tempNode != grid.get(startY).get(startX)) {
			/*Add the parent of that last finalPath element to the finalPath Array*/
			tempNode = tempNode.getParent();
			finalPath.add(tempNode);
			/*Once the finalPath reaches the start node, we have a complete path.*/
		}
		/*Reverse the path so the start node will be the first element of the array
		 *not the last*/
		finalPath.reverse();
		
		return foundPath;
	}
	
	public void setOpenList(int x, int y) {
		/*// Diagonals (not using)
		if (!(x - 1 < 0) && !(y - 1 < 0)) { // UP-LEFT
			LookNode(grid.get(Y).get(X), grid.get(Y-1).get(X-1));
		}
		if (!(x + 1 >= grid.get(y).size) && !(y - 1 < 0)) { // UP-RIGHT
			LookNode(grid.get(Y).get(X), grid.get(Y-1).get(X+1));
		}
		if (!(x - 1 < 0) && !(y + 1 >= grid.size)) { // DOWN-LEFT
			LookNode(grid.get(Y).get(X), grid.get(Y+1).get(X-1));
		}
		if (!(x + 1 >= grid.get(y).size) && !(y + 1 >= grid.size)) { // DOWN-RIGHT
			LookNode(grid.get(Y).get(X), grid.get(Y+1).get(X+1));
		}*/
		
		/*Check position of X and Y to avoid IndexOutofBounds.*/
		if (!(y - 1 < 0)) { // UP
			lookNode(grid.get(y).get(x), grid.get(y-1).get(x));
		}	
		if (!(x - 1 < 0)) { // LEFT
			lookNode(grid.get(y).get(x), grid.get(y).get(x-1));
		}
		if (!(x + 1 >= grid.get(y).size)) { // RIGHT
			lookNode(grid.get(y).get(x), grid.get(y).get(x+1));
		}
		if (!(y + 1 >= grid.size)) { // DOWN
			lookNode(grid.get(y).get(x), grid.get(y+1).get(x));
		}
	}
	
	public void compareParentToOpen(Node parent, Node open)
	{
		/*Compares to see if Open Listed node would lead to a better path than the Parent node.
		 *This is done by setting a temporary G cost using the open node and an added cost
		 *depending on whether the Parent Node is Diagonal or not to said open node.*/
		
		float tempGCost = open.getG();
		
		if (Math.abs(open.getX() - parent.getX())/nodeWidth == 1 && Math.abs(open.getY() - parent.getY())/nodeHeight == 1) {
			tempGCost += 14;
		}
		else {
			tempGCost += 10;
		}
		
		/*If the temporary G cost is smaller than the Parent Node's G cost,
		 *the open node is recalcuated and the Parent Node is set as the
		 *open node's parent node.*/
		if (tempGCost < parent.getG()) {
			open.calculateNodeCost(parent, grid.get(startY).get(startX), grid.get(endY).get(endX));
			openPath.set(openPath.indexOf(open, true), open);
		}
	}
	
	public void lookNode(Node parent, Node current) {
		/*The Adjacent Node must be ignored if it's either an blocked grid type or it's in the closedPath list*/
		if (current.getType()!=NodeType.BLOCKED && !(closedPath.contains(current, true) || closedPath.contains(current, false))) {
			if (!(openPath.contains(current, true) || openPath.contains(current, false))) {
				/*Since the node is valid, it must be added to the openPath, with the current node
				 *set as its Parent and the F, G and H costs calculated based on the start and end node.*/
				current.calculateNodeCost(parent, grid.get(startY).get(startX), grid.get(endY).get(endX));
				openPath.add(current);
			}
			else {
				/*If the node is already in the openPath list, it must be compared with the current node
				 *to see if this node will lead to a better path than the current node's path.*/
				compareParentToOpen(parent, openPath.get(openPath.indexOf(current, true)));
			}
		}
	}
	
	public void setNode(int screenX, int screenY, NodeType type) {
		/*Sets the Node Type, to either START, END, BLOCKED or NONE.*/
		int pointX = (int)(screenX/nodeWidth);
		int pointY = (int)(screenY/nodeHeight);
		if (pointY >= 0 && pointY < grid.size) {
			if (pointX >=0 && pointX < grid.get(pointY).size) {
				if (type == NodeType.START || type == NodeType.END) {
					for (int y = 0; y < grid.size; y++) {
						for (int x = 0; x < grid.get(y).size; x++) {
							if (grid.get(y).get(x).getType() == type) {
								if (type == NodeType.START) {
									startX = -1;
									startY = -1;
								}
								else if (type == NodeType.END) {
									endX = -1;
									endY = -1;
								}
								grid.get(y).get(x).setType(NodeType.NONE);
							}
						}
					}
				}
				if (grid.get(pointY).get(pointX).getType() == type) {
					grid.get(pointY).get(pointX).setType(NodeType.NONE);
				}
				else {
					if (type == NodeType.START) {
						startX = pointX;
						startY = pointY;
					}
					else if (type == NodeType.END) {
						endX = pointX;
						endY = pointY;
					}
					grid.get(pointY).get(pointX).setType(type);
				}
			}
		}
	}
	
	public boolean isNodeBlocked(int screenX, int screenY) {
		if(grid.get(screenY).get(screenX).getType() == NodeType.BLOCKED) {
			return true;
		}
		else	
			return false;
	}
	
	public Array<Node> GetPath() {
		return finalPath;
	}
}
