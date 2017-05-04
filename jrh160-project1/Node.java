//James Hahn
//Class to store node information for the DLB trie class

public class Node{
	private Node children;
	private Node sibling;
	private char value;
	private double time;

	public Node(char val){
		value = val;
	}

	public char getVal(){
		return value;
	}

	public Node getChildren(){
		return children;
	}

	public Node getSibling(){
		return sibling;
	}

	public double getTime(){
		return time;
	}

	public void setVal(char val){
		value = val;
	}

	public void setChildren(Node nextReference){
		children = nextReference;
	}

	public void setSibling(Node nextReference){
		sibling = nextReference;
	}

	public void setTime(double newTime){
		time =  newTime;
	}
}
