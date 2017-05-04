import java.util.*;
import java.io.*;

public class Graph{
	private int NumVertices; //Number of vertices in the graph
	private Vertex[] Vertices; //Adjacency list that represents the edges between two vertices in the graph
	private ArrayList<Edge> Edges; //To keep track of later for minimum spanning trees (Kruskal's algorithm)
	private ArrayList<Edge> MinSpanningTreeWithLowestLatency; //Dynamic programming solution minimum spanning tree; allows for O(1) lookup when accessed later
	private double LowestAverageLatency; //Global variable used for the minimum spanning tree; helps with a dynamic programming solution for increased program runtime
	private boolean CopperOnlyConnected = true; //Used for one of the menu options in NetworkAnalysis; keeps track of whether or not there are only copper wires in the graph

	public Graph(String filename){
		MinSpanningTreeWithLowestLatency = null;
		constructGraph(filename);
	}

	//Find the path in the graph that connects two user-specificed vertices with the lowest
	//	total latency.
	public void findLowestLatencyPath(){
		Scanner sc = new Scanner(System.in);
		Vertex vertexStart = null;
		Vertex vertexEnd = null;

		//Ask the user for the starting vertex
		do{
			System.out.print("Enter ID of first vertex: ");
			int id = Integer.parseInt(sc.nextLine());
			if(id >= NumVertices || id < 0) System.out.println("\n=== Invalid vertex! ===\n");
			else vertexStart = Vertices[id];
		} while(vertexStart == null);

		//Ask the user for the ending vertex
		do{
			System.out.print("Enter ID of second vertex: ");
			int id = Integer.parseInt(sc.nextLine());
			if(id >= NumVertices || id < 0 || id == vertexStart.getID()) System.out.println("\n=== Invalid vertex! ===\n");
			else vertexEnd = Vertices[id];
		} while(vertexEnd == null);

		//Calculate the shortest path between the two vertices and print their statistics
		Object[] pathData = shortestPathBetweenTwoVertices(vertexStart, vertexEnd, "" + vertexStart.getID(), 0L, -1);
		if(pathData == null) return;

		String path = (String) pathData[0];
		String directedPath = "";
		for(int i = 0; i < path.length(); i++){
			if(i < path.length()-1){
				directedPath += path.charAt(i) + " -> ";
			} else{
				directedPath += path.charAt(i);
			}
		}
		path = directedPath;
		double pathTravelTime = (double) pathData[1]; //Time to send a set of data from the start vertex to the destination vertex in nanoseconds
		int pathMinBandwidth = (int) pathData[2]; //Minimum bandwidth of the path between the vertices; in other words, the max amount of data allowed along the path
		System.out.printf("\nShortest path: %s\nTime/Latency: %.3f nanoseconds\nBandwidth: %d Mbps\n", path, pathTravelTime, pathMinBandwidth);
	}

	//Find out whether graph is connected with only copper, connected considering only copper, or neither
	public void determineCopperConnectivity(){
		if(CopperOnlyConnected){ //The graph consists of only copper wires
			System.out.println("-- This graph consists of only copper wires, so it is copper-connected.");
		} else{
			boolean connectedWithCopper = true; //Assume the graph is connected with only copper

			for(int i = 0; i < NumVertices; i++){ //Iterate through every vertex and check to make sure it has at least one copper connection
				LinkedList<Edge> vertexEdges = Vertices[i].getConnections();

				boolean hasCopperConnection = false;
				for(Edge edge : vertexEdges){
					if(edge.getMaterial().equals("copper")){ //There exists a copper wire from this vertex
						hasCopperConnection = true;
						break;
					}
				}

				if(!hasCopperConnection){ //If this vertex does not have a single copper connection, then the graph cannot be copper connected
					connectedWithCopper = false;
					break;
				}
			}

			if(connectedWithCopper)	System.out.println("-- This graph can be connected with only copper wires.  But, this graph also has fiber optic wires.");
			else System.out.println("-- This graph is not copper-only and cannot be connected with only copper wires.");
		}
	}

	//Find the path between two vertices that allows the maximum amount of data transfer at one time
	public void findMaxDataPath(){
		Scanner sc = new Scanner(System.in);
		Vertex vertexStart = null;
		Vertex vertexEnd = null;

		//Ask the user for the starting vertex
		do{
			System.out.print("Enter ID of first vertex: ");
			int id = Integer.parseInt(sc.nextLine());
			if(id >= NumVertices || id < 0) System.out.println("\n=== Invalid vertex! ===\n");
			else vertexStart = Vertices[id];
		} while(vertexStart == null);

		//Ask the user for the destination vertex
		do{
			System.out.print("Enter ID of second vertex: ");
			int id = Integer.parseInt(sc.nextLine());
			if(id >= NumVertices || id < 0 || id == vertexStart.getID()) System.out.println("\n=== Invalid vertex! ===\n");
			else vertexEnd = Vertices[id];
		} while(vertexEnd == null);

		//Calculate all of the paths between the vertices and print out the statistics of the path with the maximum bandwidth
		int maxData = maxDataBetweenTwoVertices(vertexStart, vertexEnd, "" + vertexStart.getID(), -1);
		System.out.println("\nMax amount of data between vertices " + vertexStart.getID() + " and " + vertexEnd.getID() + ": " + maxData + " Mbps");
	}

	//When any two vertices fail, determine whether or not the graph is still connected
	public void findConnectivityOnVertexFailures(){
		//Permute all possible pairs of vertices failing and calculate if the graph is connected or not
		for(int i = 0; i < NumVertices-1; i++){
			for(int j = i+1; j < NumVertices; j++){
				//Traverse the graph ignoring Vertices[i] and Vertices[j]
				//If the path's length ever reaches NumVertices-2, then it is connected
				Vertex startVertex = null;
				Vertex failureOne = Vertices[i];
				Vertex failureTwo = Vertices[j];
				boolean[] visited = new boolean[NumVertices];

				//Mark that the failed vertices have already been visited so the traversal doesn't visit them
				visited[failureOne.getID()] = true;
				visited[failureTwo.getID()] = true;

				//Set the starting vertex for to explore from; it can be any vertex that isn't in the pair of failing vertices
				if(i != 0){ //If we're omitting vertex 0, then make sure we don't start there
					startVertex = Vertices[0];
				} else{ //Vertex 0 failed, so determine a new vertex that didn't fail
					if(j != NumVertices-1){
						startVertex = Vertices[j+1];
					} else if(j-i != 1){
						startVertex = Vertices[j-1];
					} else{
						System.out.println("-- This graph IS NOT connected when any two vertices fail."); //There are only 2 vertices in the graph so if both fail, it is not connected
						return;
					}
				}

				//Pass in the visited array and mark the vertices that were traversed across
				findConnectivityWithoutTwoVertices(startVertex, failureOne, failureTwo, visited);

				//Check to make sure all vertices were visited; in other words, the graph is still connected despite the failures
				boolean graphIsConnected = true;
				for(int k = 0; k < visited.length; k++){
					if(visited[k] == false){ //A node was not visited, so the graph is not connected
						graphIsConnected = false;
						break;
					}
				}

				if(!graphIsConnected){ //If we find out that any two pairs of vertices failing causes the graph to not be connected, return
					System.out.println("-- This graph IS NOT connected when any two vertices fail.");
					return;
				}
			}
		}

		System.out.println("-- This graph IS connected when any two vertices fail.");
		return; //All possible combinations of two vertices failing produced connected graphs
	}

	//Calculate the minimum spanning tree with the lowest average latency among the edges.
	//Another definition of this is the tree that allows for the fastest data transfer across the entire graph.
	public void findLowestAverageLatencySpanningTree(){
		if(MinSpanningTreeWithLowestLatency == null){ //Generate the minimum spanning tree using Kruskal's algorithm
			LowestAverageLatency = KruskalMST() / MinSpanningTreeWithLowestLatency.size(); //Divide the total latency by the number of edges to get the average latency
		} //Else, the Min Spanning Tree has already been created, so no need to waste CPU resources to generate the same on

		//Print out all of the edges used to construct this minimum spanning tree
		System.out.println("Lowest Average Latency Spanning Tree Edges\n------------------------------------------");
		for(Edge e : MinSpanningTreeWithLowestLatency){
			System.out.println("( " + e.getSource().getID() + " , " + e.getDestination().getID() + " )");
		}

		//Print out the average latency of the edges in this minimum spanning tree
		System.out.printf("\nThe average latency of this spanning tree is %.3f nanoseconds.\n", LowestAverageLatency);
	}

	////////////////////////////////////////
	//Helper Methods
	///////////////////////////////////////

	//Construct the graph by scanning the file passed in by the user and connecting its vertices
	//	with respective edges.
	private void constructGraph(String filename){
		if(filename == null) return;

		//Construct the file and make sure it exists before scanning its contents
		File f = new File(filename);
		Scanner sc;
		try{
			sc = new Scanner(f);
		} catch(FileNotFoundException e){
			System.out.println("=== File not found! ===");
			return;
		}

		if(sc.hasNextLine()) NumVertices = Integer.parseInt(sc.nextLine()); //The number of vertices in the graph is the first line
		else return; //We can't construct a graph if we don't know how many vertices there are

		Vertices = new Vertex[NumVertices]; //Initialize the adjacency list that contains <# of vertices> Vertices
		for(int i = 0; i < Vertices.length; i++){
			Vertices[i] = new Vertex(i); //Initialize vertex i with its proper ID
		}

		Edges = new ArrayList<Edge>();
		while(sc.hasNextLine()){ //Run through the file and generate all the edges of the graph
			String line = sc.nextLine();
			String[] lineContents = line.split(" ");
			if(lineContents.length != 5)  continue; //Each line must contain two vertices, a material, bandwidth, and length (5 items)

			//Separate the contents of the line into their respective variables to add them to edges in the graph
			Vertex vertexA = Vertices[Integer.parseInt(lineContents[0])];
			Vertex vertexB = Vertices[Integer.parseInt(lineContents[1])];
			String material = lineContents[2];
			int bandwidth = Integer.parseInt(lineContents[3]); //Maximum amount of data that can travel along this edge
			int length = Integer.parseInt(lineContents[4]); //Length of the edge

			//Create two new edges between the two vertices and make sure they map back and forth to eachother (they're full duplex)
			Edge edgeFromAtoB = new Edge(material, bandwidth, length, vertexB, vertexA);
			Edge edgeFromBtoA = new Edge(material, bandwidth, length, vertexA, vertexB);
			vertexA.getConnections().addFirst(edgeFromAtoB);
			vertexB.getConnections().addFirst(edgeFromBtoA);
			Edges.add(edgeFromAtoB); //Add one of the edges to the edges list because only one is needed for a minimum spanning tree
			if(material.equals("optical")) CopperOnlyConnected = false; //If at any point while constructing this graph there is an optical fiber, then it is not copper-only connected
		}
	}

	//Helper method to calculate the shortest path between two vertices
	private Object[] shortestPathBetweenTwoVertices(Vertex curr, Vertex dest, String path, double length, int minPathBandwidthOfPath){
		if(curr == null || dest == null || path == null || length < 0.0) return null; //Any invalid input will return null promptly

		if(curr == dest){ //The destination vertex has been reached!  Return the necessary path data
			return new Object[] { path, length, minPathBandwidthOfPath };
		}

		LinkedList<Edge> currEdges = curr.getConnections(); //Grab all of the edges at this current node in the path

		double minLength = -1.0; //Length of the minimum length path
		String minLengthPath = "";
		for(Edge edge : currEdges){ //Iterate over all of the edges in order to eventually permute all possible paths in the graph
			Vertex edgeDestination = edge.getDestination();
			if(path.contains("" + edgeDestination.getID())) continue; //The vertex on the end of this edge has already been visited, so just go to the next vertex

			String newPath = path + edgeDestination.getID(); //Generate the new path
			double newLength = 0.0;
			if(edge.getMaterial().equals("copper")){
				newLength = length + edge.getTimeToTravel();
			} else if(edge.getMaterial().equals("optical")){
				newLength = length + edge.getTimeToTravel();
			} else{
				return null; //Invalid wire material type, so we shouldn't travel that path
			}

			int newMinPathBandwidthOfPath = minPathBandwidthOfPath; //If the edge that's being travelled has a bandwidth lower than the current path's bandwidth, then set the new minimum
			if(minPathBandwidthOfPath == -1.0 || edge.getBandwidth() < minPathBandwidthOfPath) newMinPathBandwidthOfPath = edge.getBandwidth(); //Set the new minimum path bandwidth

			Object[] pathData = shortestPathBetweenTwoVertices(edgeDestination, dest, newPath, newLength, newMinPathBandwidthOfPath); //Recursively traverse to the next path
			if(pathData == null) continue; //If there is no path data for the edge, go to the next edge

			String edgePath = (String) pathData[0];
			double pathLength = (double) pathData[1];
			int pathBandwidth = (int) pathData[2];

			if(minLength == -1 || pathLength < minLength){ //Set the new minimum path length
				minLength = pathLength;
				minLengthPath = edgePath;
				minPathBandwidthOfPath = pathBandwidth;
			} else if(pathLength == minLength && pathBandwidth > minPathBandwidthOfPath){ //The path lengths are the same but this new path has a better bandwidth
				minLength = pathLength;
				minLengthPath = edgePath;
				minPathBandwidthOfPath = pathBandwidth;
			}
		}

		if(minLength > -1.0){ //A path to reach the destination from the current vertex exists
			return new Object[] { minLengthPath, minLength, minPathBandwidthOfPath };
		}

		return null; //We're not at the destination and no edges from the current vertex are valid (there are none or none reach the destination)
	}

	//Helper method to calculate the maximum amount of data (highest bandwidth) that can be transferred between two vertices
	private int maxDataBetweenTwoVertices(Vertex curr, Vertex dest, String path, int maxPathBandwidth){
		if(curr == null || dest == null || path == null) return -1; //Any invalid input will return null promptly

		if(curr == dest){ //The destination vertex has been reached!  Return the necessary path data
			return maxPathBandwidth;
		}

		LinkedList<Edge> currEdges = curr.getConnections();

		int max = -1;
		for(Edge edge : currEdges){ //Traverse across all possible paths to the destination and find the maximum possible bandwidth
			Vertex edgeDestination = edge.getDestination();
			if(path.contains("" + edgeDestination.getID())) continue; //The vertex on the other side of this edge has already been travelled

			int newMaxPathBandwidth = maxPathBandwidth;
			if(newMaxPathBandwidth == -1 || edge.getBandwidth() < newMaxPathBandwidth) newMaxPathBandwidth = edge.getBandwidth(); //Set the new minimum path bandwidth

			String newPath = path + edgeDestination.getID();
			int pathBandwidth = maxDataBetweenTwoVertices(edgeDestination, dest, newPath, newMaxPathBandwidth); //Recursively traverse across the graph
			if(pathBandwidth == -1) continue; //If there is no path data for the edge, go to the next edge
			if(pathBandwidth > max) max = pathBandwidth; //Found a path with a new max bandwidth
		}

		return max;
	}

	//Perform a bread-first search and check that all nodes are visited
	private void findConnectivityWithoutTwoVertices(Vertex curr, Vertex a, Vertex b, boolean[] visited){
		if(curr == null || a == null || b == null || visited == null) return; //Any invalid input will return null promptly

		if(visited[curr.getID()] == true){ //This node has already been visited
			return;
		}

		visited[curr.getID()] = true; //Mark that the current node has been visited

		LinkedList<Edge> currEdges = curr.getConnections();

		for(Edge edge : currEdges){ //Perform a depth-first search to attempt to traverse all nodes except for the failed ones in the graph
			Vertex edgeDestination = edge.getDestination(); //Get the destination Vertex of this current edge
			if(visited[edgeDestination.getID()] == true) continue; //We already traversed to this node, so don't cycle back to it

			findConnectivityWithoutTwoVertices(edgeDestination, a, b, visited); //Recursively traverse the graph
		}

		return;
	}

	//Execute Kruskal's algorithm to find the minimum spanning tree of the graph
	//	with the lowest weight.
	private double KruskalMST(){
		//Initialize our union-find components; taken from book's code
		int[] parent = new int[NumVertices];
        byte[] rank = new byte[NumVertices];
        for (int i = 0; i < NumVertices; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

		//Use a lambda expression to sort the edges from min to max weight
		Collections.sort(Edges, (e1, e2) -> e1.compareTo(e2));

		MinSpanningTreeWithLowestLatency = new ArrayList<Edge>(); //Edges in the minimum spanning tree
		double weight = 0.0; //Total weight of the minimum spanning tree; in this case, weight will be the time taken to travel along all of the edges

        //Core of Kruskal's algorithm
		int currEdge = 0; //Current minimum weight edge we're considering adding to the MST assuming it doesn't create a cycle
        while(currEdge != Edges.size()-1 && MinSpanningTreeWithLowestLatency.size() < NumVertices - 1){ //We have edges left and the spanning tree hasn't reached all vertices yet
            Edge e = Edges.get(currEdge);
            int v = e.getSource().getID();
            int w = e.getDestination().getID();
            if(!connected(v, w, parent)){ //Edge (v,w) does not create a cycle
                union(v, w, parent, rank); //Logically add the edge (v,w) to the minimum spanning tree's union of all its edges
                MinSpanningTreeWithLowestLatency.add(e);  //Add edge e to mst to refer to it later
                weight += e.getTimeToTravel();
            }

			currEdge++; //In the next iteration, look at the next edge in the tree
        }

		return weight;
	}

	//Merges the component containing site p with the
	//	the component containing site q.
    private void union(int p, int q, int[] parent, byte[] rank) {
        int rootP = find(p, parent);
        int rootQ = find(q, parent);
        if (rootP == rootQ) return;

        //Make root of smaller rank point to root of larger rank
        if      (rank[rootP] < rank[rootQ]) parent[rootP] = rootQ;
        else if (rank[rootP] > rank[rootQ]) parent[rootQ] = rootP;
        else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
    }

	//Returns true if the the two sites are in the same component.
	//Indicates later on if the two vertices connecting will cause a cycle in the graph.
	private boolean connected(int p, int q, int[] parent) {
        return find(p, parent) == find(q, parent);
    }

	//Returns the component identifier for the component containing site p.
	private int find(int p, int[] parent) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];    // path compression by halving
            p = parent[p];
        }
        return p;
    }
}
