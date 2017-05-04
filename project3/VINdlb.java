//James Hahn
//
//Class for a DLB (De La Brandais) trie
//Can insert, check if a make/model priority queue exists, and find a priority queue associated with a make/model, as well as remove
//
//Throughout this file, VIN is referring to the VIN variable in Car.java, which is a 17-character identification string unique to each car

import java.text.StringCharacterIterator;

public class VINdlb{
	private CarNode root;

	//Insert a car's VIN into the DLB
	public boolean insert(Car car){
		String word = car.getVIN();
		if(word == null) return false;

		CarNode existsInTrie = existsButNotSet(word);
		if(existsInTrie != null){ //A CarNode will be returned to easily set a value
			existsInTrie.setCar(car);
			return true;
		}

		StringCharacterIterator it = new StringCharacterIterator(word);

		if(root == null){ //If nothing exists, an insert will be fairly easy and straightforward
			root = new CarNode(it.current());

			CarNode curr = root;
			it.next();

			while(it.getIndex() < it.getEndIndex()){ //Loop through the VIN
				CarNode newCarNode = new CarNode(it.current());
				curr.setChildren(newCarNode);
				curr = curr.getChildren();

				it.next();
			}

			curr.setChildren(new CarNode('\0')); //Terminate the VIN in the trie with \0
			if(car != null){ //If a PQ is inserted, travel to the current CarNode's children with \0 and insert the PQ at that CarNode
				curr = curr.getChildren();
				curr.setCar(car);
			}
		} else{
			CarNode curr = root;

			while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
				//At this level in the DLB, search for a CarNode in the horizontal
				//	linked-list that matches the current character; create a new
				//	CarNode if it doesn't exist.
				while(it.current() != curr.getVal()){
					if(curr.getSibling() == null){
						CarNode newCarNode = new CarNode(it.current());
						curr.setSibling(newCarNode);
						curr = curr.getSibling(); //Traverse to this newly created CarNode
						break;
					} else{
						curr = curr.getSibling();
					}
				}

				if(curr.getChildren() == null){ //Add a new CarNode because the character we're looking for doesn't exist
					CarNode newCarNode = new CarNode(it.current());
					curr.setChildren(newCarNode);
				}

				it.next();
				curr = curr.getChildren();
			}

			curr.setVal('\0'); //Terminate the word in the trie with \0
			if(car != null){ //If a PQ is inserted, travel to the current CarNode's children with \0 and insert the PQ at that CarNode
				curr.setCar(car);
			}
		}

		return true;
	}

	//Return a boolean indicating whether a specific VIN already exists in the trie
	public boolean exists(String word){
		if(word == null || root == null) return false;

		StringCharacterIterator it = new StringCharacterIterator(word);
		CarNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the VIN
			if(curr == null){ //If the CarNode we're at is null, then obviously the VIN does not exist
				return false;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No CarNode exists to terminate the VIN, so it doesn't exist
			return false;
		} else if(curr.getVal() == '\0'){ //Reached the end of the VIN, so it exists
			if(curr.getCar() != null) return true;
			else return false;
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				if(curr.getCar() != null) return true;
				else return false;
			} else{
				curr = curr.getSibling();
			}
		}

		return false;
	}

	//Does a VIN exist without a set end value (Car object)
	private CarNode existsButNotSet(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator it = new StringCharacterIterator(word);
		CarNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the VIN
			if(curr == null){ //If the CarNode we're at is null, then obviously the VIN does not exist
				return null;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return null; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No CarNode exists to terminate the VIN, so it doesn't exist
			return null;
		} else if(curr.getVal() == '\0'){ //Reached the end of the VIN, so it exists
			if(curr.getCar() == null) return curr;
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				if(curr.getCar() == null) return curr;
			} else{
				curr = curr.getSibling();
			}
		}

		return null;
	}

	//Find the Car associated with a given VIN in the trie
	public Car getCar(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator it = new StringCharacterIterator(word);
		CarNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the VIN
			if(curr == null){ //If the CarNode we're at is null, then obviously the VIN does not exist
				return null;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return null; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No CarNode exists to terminate the VIN, so no Car exists
			return null;
		} else if(curr.getVal() == '\0'){ //Reached the end of the VIN, so a Car exists
			return curr.getCar();
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				return curr.getCar();
			} else{
				curr = curr.getSibling();
			}
		}

		return curr.getCar();
	}

	//Remove a Car (by it's VIN) from the DLB by setting its null terminating CarNode's Car object to null
	public boolean remove(String word){
		if(word == null || root == null) return false;

		StringCharacterIterator it = new StringCharacterIterator(word);
		CarNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the VIN
			if(curr == null){ //If the CarNode we're at is null, then obviously the VIN does not exist
				return false;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling CarNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No CarNode exists to terminate the VIN, so it doesn't exist
			return false;
		} else if(curr.getVal() == '\0'){ //Reached the end of the VIN, so it exists
			if(curr.getCar() != null){
				curr.setCar(null);
				return true;
			}
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				if(curr.getCar() != null){
					curr.setCar(null);
					return true;
				}
			} else{
				curr = curr.getSibling();
			}
		}

		return false;
	}
}
