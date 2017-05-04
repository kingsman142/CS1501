//James Hahn
//
//Class for a DLB (De La Brandais) trie
//Can insert, check if a word exists, and find a Priority Queue associated with a word

import java.text.StringCharacterIterator;

public class PriorQueueDLB{
	private PQNode root;

	//Insert a make/model into the priority queue DLB
	public boolean insert(String word, CarPQ carPQ){
		if(word == null) return false;

		StringCharacterIterator it = new StringCharacterIterator(word);

		if(root == null){ //If nothing exists, an insert will be fairly easy and straightforward
			root = new PQNode(it.current());

			PQNode curr = root;
			it.next();

			while(it.getIndex() < it.getEndIndex()){ //Loop through the word
				PQNode newPQNode = new PQNode(it.current());
				curr.setChildren(newPQNode);
				curr = curr.getChildren();

				it.next();
			}

			curr.setChildren(new PQNode('\0')); //Terminate the word in the trie with \0
			if(carPQ != null){ //If a PQ is inserted, travel to the current PQNode's children with \0 and insert the PQ at that PQNode
				curr = curr.getChildren();
				curr.setPriorityQueue(carPQ);
			}
		} else{
			PQNode curr = root;

			while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
				//At this level in the DLB, search for a PQNode in the horizontal
				//	linked-list that matches the current character; create a new
				//	PQNode if it doesn't exist.
				while(it.current() != curr.getVal()){
					if(curr.getSibling() == null){
						PQNode newPQNode = new PQNode(it.current());
						curr.setSibling(newPQNode);
						curr = curr.getSibling(); //Traverse to this newly created PQNode
						break;
					} else{
						curr = curr.getSibling();
					}
				}

				if(curr.getChildren() == null){ //Add a new PQNode because the character we're looking for doesn't exist
					PQNode newPQNode = new PQNode(it.current());
					curr.setChildren(newPQNode);
				}

				it.next();
				curr = curr.getChildren();
			}

			curr.setVal('\0'); //Terminate the word in the trie with \0
			if(carPQ != null){ //If a PQ is inserted, travel to the current PQNode's children with \0 and insert the PQ at that PQNode
				curr.setPriorityQueue(carPQ);
			}
		}

		return true;
	}

	//Return a boolean indicating whether a make/model's priority queue exists in the trie
	public boolean exists(String word){
		if(word == null || root == null) return false;

		StringCharacterIterator it = new StringCharacterIterator(word);
		PQNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
			if(curr == null){ //If the PQNode we're at is null, then obviously the word does not exist
				return false;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling PQNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No PQNode exists to terminate the word, so it doesn't exist
			return false;
		} else if(curr.getVal() == '\0'){ //Reached the end of the word, so it exists
			return true;
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				return true;
			} else{
				curr = curr.getSibling();
			}
		}

		return false;
	}

	//Return the priority queue associated with a given make/model in the trie (in the form of: make + "%" + model)
	public CarPQ getPQ(String word){
		if(word == null || root == null) return null;

		StringCharacterIterator it = new StringCharacterIterator(word);
		PQNode curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
			if(curr == null){ //If the PQNode we're at is null, then obviously the word does not exist
				return null;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return null; //No sibling PQNode matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No PQNode exists to terminate the word, so no PQ exists
			return null;
		} else if(curr.getVal() == '\0'){ //Reached the end of the word, so a PQ exists
			return curr.getPriorityQueue();
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				return curr.getPriorityQueue();
			} else{
				curr = curr.getSibling();
			}
		}

		return curr.getPriorityQueue();
	}
}
