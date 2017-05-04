# CS/COE 1501 Project 4

## Goal:
To gain a better understanding of graphs and graph algorithms through practical implementation.

## High-level description:
Your program will analyze a given graph representing a computer network according to several specified metrics.
The vertices of these graphs will represent switches in the network, while the edges represent either fiber optic or copper cables run between the switches.
Your program should operate entirely via a console interface menu (no GUI).

## Specifications:
1. Your program should accept a single command line argument that specifies the name of a file containing a description of a graph.  Two such files are provided ("network_data1.txt" and "network_data2.txt").  The format of these files is as follows:
	* The first line contains a single int stating the number of vertices in the graph.  These vertices will be numbered "0" to "v-1".
	* Each following line will describe a single edge in the graph, with each of the following data items listed separated by spaces.
		* First, integers specify the endpoints of the edge.
		* Next, a string describes the type of cable that edge represents (either "optical" or "copper").
		* Next, an integer states the bandwidth of the cable in megabits per second.
		* Finally, an integer states the length of the cable in meters.
		* E.g., the line "0 5 optical 10000 25" describes an edge between vertex 0 and vertex 5 that represents a 25 meter long optical cable with bandwidth of 10 gigabits per second.
	* You should assume that all cables are full duplex and hence represent connections in both directions (e.g., in the example above data can flow from vertex 0 to vertex 5 at 10 gigabits per second and from vertex 5 to vertex 0 at 10 gigabits per second simultaneously).
1. You must internally represent the graph as an adjacency list.
1. After loading the graph from the specified file, your program should present the user with a menu with the following options:
	1. Find the __lowest latency path__ between any two points, and give the bandwidth available along that path.
		1.  First, your program should prompt the user for the two vertices that they wish to find the lowest latency path between.
		1.  Then, your program should output the edges that comprise this lowest latency path in order from the first user-specified vertex to the second.
		1.  You must find the path between these vertices that will require the least amount of time for a single data packet to travel.  For this project, we will simply compute the time required to travel along a path through the graph as the sum of the times required to travel each edge, where the time to travel each edge is computed as the quotient of speed at which data can be send along a connection of that type and the length of the cable represented by that edge.
			* A single data packet can be sent along a copper cable at a speed of 230000000 meters per second.
			* A single data packet can be sent along a fiber optic cable at a speed of 200000000 meters per second.
		1.  Your program should also output the bandwidth that is available along the resulting path (minimum bandwidth of all edges in the path).
	1. Determine whether or not the graph is __copper-only connected__, or whether it is connected considering only copper links (i.e., ignoring fiber optic cables).
	1. Find the __maximum amount of data__ that can be transmitted from one vertex to another.
		1. First, your program should prompt the user for the two vertices that they wish to find the max bandwidth.
		1. Then, your program should output the value of the maximum amount of data (bandwidth) that can be transmitted from the first to second user specified vertices.
	1. Find the __lowest average latency spanning tree__ for the graph (i.e., a spanning tree with the lowest average latency per edge).
	1. Determine whether or not the graph would remain connected if __any two vertices in the graph were to fail__.
		1. Note that you are not prompting the users for two vertices that could fail, you will need to determine whether the failure of *any pair* of vertices would cause the graph to become disconnected.
	1. Quit the program.

## Additional Notes/Hints:
* The assumed calculation of network latency used here is a drastic simplification for this project.  Interested students are encouraged to investigate a more detailed study of computer networks independently (recommended reading:  _Computer Networks: A Systems Approach_ by Peterson and Davie).
