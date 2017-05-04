public class Edge implements Comparable<Edge>{
	private final int COPPER_SPEED = 230000000; //The speed at which a single data packet can be sent across copper wire in meters per second
	private final int FIBER_SPEED = 200000000; //The speed at which a single data packet can be sent across fiber optic wire in meters per second
	private String Material; //Optical of Copper edge
	private int Bandwidth; //Bandwidth of edge in megabits per second
	private int Length; //Length of edge in meters
	private Vertex Source; //The source of this edge
	private Vertex Destination; //ID of the destination vertex
	private double TimeToTravel; //Time to travel on this edge based on its length and material in nanoseconds

	public Edge(String material, int bandwidth, int length, Vertex destination, Vertex source){
		Material = material;
		Bandwidth = bandwidth;
		Length = length;
		Destination = destination;
		Source = source;

		if(Material.equals("copper")){
			TimeToTravel = ((double) 1/COPPER_SPEED) * Length * Math.pow(10, 9); //For every meter, travelling along copper wire takes 1/230,000,000 seconds; convert it to nanoseconds
		} else if(Material.equals("optical")){
			TimeToTravel = ((double) 1/FIBER_SPEED) * Length * Math.pow(10, 9); //For every meter, travelling along optical wire takes 1/200,000,000 seconds; convert it to nanoseconds
		}
	}

	public void setMaterial(String newMaterial){
		Material = newMaterial;
	}

	public void setBandwidth(int newBandwidth){
		Bandwidth = newBandwidth;
	}

	public void setLength(int newLength){
		Length = newLength;
	}

	public void setDestination(Vertex newDestination){
		Destination = newDestination;
	}

	public String getMaterial(){
		return Material;
	}

	public int getBandwidth(){
		return Bandwidth;
	}

	public int getLength(){
		return Length;
	}

	public Vertex getDestination(){
		return Destination;
	}

	public double getTimeToTravel(){
		return TimeToTravel;
	}

	public Vertex getSource(){
		return Source;
	}

	public int compareTo(Edge otherEdge){
		if(TimeToTravel > otherEdge.getTimeToTravel()){
			return 1;
		} else if(TimeToTravel == otherEdge.getTimeToTravel()){
			return 0;
		} else{
			return -1;
		}
	}
}
