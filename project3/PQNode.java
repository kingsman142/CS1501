//James Hahn
//Class to store PQNode information for the DLB trie class

public class PQNode{
	private PQNode children;
	private PQNode sibling;
	private char value;
	private CarPQ priorityQueue;

	public PQNode(char val){
		value = val;
	}

	public char getVal(){
		return value;
	}

	public PQNode getChildren(){
		return children;
	}

	public PQNode getSibling(){
		return sibling;
	}

	public CarPQ getPriorityQueue(){
		return priorityQueue;
	}

	public void setVal(char val){
		value = val;
	}

	public void setChildren(PQNode nextReference){
		children = nextReference;
	}

	public void setSibling(PQNode nextReference){
		sibling = nextReference;
	}

	public void setPriorityQueue(CarPQ newPQ){
		priorityQueue = newPQ;
	}
}
