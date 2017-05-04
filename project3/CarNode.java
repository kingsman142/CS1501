//James Hahn
//Class to store CarNode information for the DLB trie class

public class CarNode{
	private CarNode children;
	private CarNode sibling;
	private char value;
	private Car car;

	public CarNode(char val){
		value = val;
	}

	public char getVal(){
		return value;
	}

	public CarNode getChildren(){
		return children;
	}

	public CarNode getSibling(){
		return sibling;
	}

	public Car getCar(){
		return car;
	}

	public void setVal(char val){
		value = val;
	}

	public void setChildren(CarNode nextReference){
		children = nextReference;
	}

	public void setSibling(CarNode nextReference){
		sibling = nextReference;
	}

	public void setCar(Car newCar){
		car = newCar;
	}
}
