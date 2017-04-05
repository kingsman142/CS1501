/*
James Hahn
CS1501 - Algorithm Implementation
Project 4

This project is aimed towards heavy graph theory and algorithms.
The user feeds in one command-line argument, a filename containing a properly formatted
	graph with its set of vertices.
Then, a menu is supplied with five graph operations.  The user can ask for the lowest
	latency path between two vertices, the copper connectivity of the graph, the maximum
	amount of data transferrable between two vertices, the spanning tree with the lowest
	average latency, and finally whether or not the graph is still connected even after
	any two vertices fail.
Highlights of this project include algorithms such as Djikstra's, Kruskal's, and even
	brute-force approaches with pruning.
*/

import java.util.*;

public class NetworkAnalysis{
	public static void main(String[] args){
		if(args.length == 1){
			Graph graph = new Graph(args[0]);
			Scanner sc = new Scanner(System.in);

			while(true){
				System.out.println("\t1. Find the lowest latency path");
				System.out.println("\t2. Determine the graph's copper connectivity");
				System.out.println("\t3. Find the maximum amount of data that can be transferred from one vertex to another");
				System.out.println("\t4. Find the lowest average latency spanning tree");
				System.out.println("\t5. Determine if the graph disconnects if any two vertices fail");
				System.out.println("\t6. Quit");
				System.out.print  ("Choose one of the above (enter number): ");

				String userInput = sc.nextLine();

				System.out.println(); //Add an extra empty line for spacing to make the interface look nicer
				if(userInput.equals("1")){
					graph.findLowestLatencyPath();
				} else if(userInput.equals("2")){
					graph.determineCopperConnectivity();
				} else if(userInput.equals("3")){
					graph.findMaxDataPath();
				} else if(userInput.equals("4")){
					graph.findLowestAverageLatencySpanningTree();
				} else if(userInput.equals("5")){
					graph.findConnectivityOnVertexFailures();
				} else if(userInput.equals("6")){
					System.exit(0);
				} else{
					System.out.println("=== Invalid choice! ===");
				}
				System.out.println(); //Add an extra empty line for spacing to make the interface look nicer
			}
		} else{
			System.out.println("=== Exactly 1 argument is required! ===");
		}
	}
}
