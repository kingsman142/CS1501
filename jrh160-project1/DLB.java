//James Hahn
//
//Class for a DLB (De La Brandais) trie
//Can insert, check if a word exists, and find a time associated with a word

import java.text.StringCharacterIterator;

public class DLB{
	private Node root;

	//Insert a word into the DLB
	public boolean insert(String word, double time){
		if(word == null) return false;

		word = word.toLowerCase(); //All passwords are lowercase
		StringCharacterIterator it = new StringCharacterIterator(word);

		if(root == null){ //If nothing exists, an insert will be fairly easy and straightforward
			root = new Node(it.current());

			Node curr = root;
			it.next();

			while(it.getIndex() < it.getEndIndex()){ //Loop through the word
				Node newNode = new Node(it.current());
				curr.setChildren(newNode);
				curr = curr.getChildren();

				it.next();
			}

			curr.setChildren(new Node('\0')); //Terminate the word in the trie with \0
			if(time != 0.0){ //If a time is inserted, travel to the current node's children with \0 and insert the time at that node
				curr = curr.getChildren();
				curr.setTime(time);
			}
		} else{
			Node curr = root;

			while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
				//At this level in the DLB, search for a node in the horizontal
				//	linked-list that matches the current character; create a new
				//	node if it doesn't exist.
				while(it.current() != curr.getVal()){
					if(curr.getSibling() == null){
						Node newNode = new Node(it.current());
						curr.setSibling(newNode);
						curr = curr.getSibling(); //Traverse to this newly created node
						break;
					} else{
						curr = curr.getSibling();
					}
				}

				if(curr.getChildren() == null){ //Add a new node because the character we're looking for doesn't exist
					Node newNode = new Node(it.current());
					curr.setChildren(newNode);
				}

				it.next();
				curr = curr.getChildren();
			}

			curr.setVal('\0'); //Terminate the word in the trie with \0
			if(time != 0.0){ //If a time is inserted, travel to the current node's children with \0 and insert the time at that node
				curr.setTime(time);
			}
		}

		return true;
	}

	//Return a boolean indicating that a word exists in the trie
	public boolean exists(String word){
		if(word == null || root == null) return false;

		word = word.toLowerCase(); //All passwords are lowercase
		StringCharacterIterator it = new StringCharacterIterator(word);
		Node curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
			if(curr == null){ //If the node we're at is null, then obviously the word does not exist
				return false;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return false; //No sibling node matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No node exists to terminate the word, so it doesn't exist
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

	//Find the time associated with a given word in the trie; this is how long it took to crack the given password
	public double findTime(String word){
		if(word == null || root == null) return 0.0;

		word = word.toLowerCase(); //All passwords are lowercase
		StringCharacterIterator it = new StringCharacterIterator(word);
		Node curr = root;

		while(it.getIndex() < it.getEndIndex()){ //Loop through each character in the word
			if(curr == null){ //If the node we're at is null, then obviously the word does not exist
				return 0.0;
			}

			while(it.current() != curr.getVal()){ //Loop through sibling linked list
				if(curr.getSibling() == null){
					return 0.0; //No sibling node matches the current character
				} else{
					curr = curr.getSibling();
				}
			}

			curr = curr.getChildren();
			it.next();
		}

		if(curr == null){ //No node exists to terminate the word, so no time exists
			return 0.0;
		} else if(curr.getVal() == '\0'){ //Reached the end of the word, so a time exists
			return curr.getTime();
		}

		while(curr.getSibling() != null){ //Loop through sibling linked list
			if(curr.getVal() == '\0'){
				return curr.getTime();
			} else{
				curr = curr.getSibling();
			}
		}

		return curr.getTime();
	}
}
