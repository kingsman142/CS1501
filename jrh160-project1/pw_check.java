//James Hahn
//
//Nicholas Farnan - Tu/Th 11:00am-12:15pm
//Recitation: Fr 10-10:50am
//CS1501[W] Project 1
//Project 1 is a password cracker that generates a DLB trie
//	of all valid passwords based on a file of invalid passwords
//	provided by the professor.  A user can enter a password
//	and if it is valid, the time taken to crack/generate that
//	password will be printed.  Otherwise, if it is invalid,
//	10 other alternative passwords that are closest to the
//	user's password will be provided, along with the times taken
//	to generate each one.

import java.io.*;
import java.util.*;
import java.text.StringCharacterIterator;

public class pw_check{
	public static char[] AllChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '$', '^', '_', '*'};
	public static PrintWriter Writer = null;
	public static int ValidPasswordsCounter = 0;

	public static void main(String[] args){
		if(args.length == 1){
			if(args[0].equals("-find")){ //Program is ran with -find flag
				System.out.println("Finding passwords!");
				findPasswords();
			} else if(args[0].equals("-check")){ //Program is ran with -check flag
				System.out.println("Checking passwords!");
				checkPasswords();
			} else{
				System.out.println("Invalid flag! Only -find and -check are valid command-line arguments!");
			}
		} else{
			System.out.println("One command-line argument is required for this program: -find or -check");
		}
	}

	//When the program is ran with the -check flag, check the user-entered passwords to see whether they are valid or invalid.
	//If the password is valid, report the time to crack that password; if invalid, suggest 10 valid passwords and their respective times.
	public static void checkPasswords(){
		File all_passwords = new File("all_passwords.txt");
		if(!all_passwords.exists()){
			System.out.println("Please run the program with the -find flag first, then run it with -check again.");
			return;
		}

		//Add all the valid passwords into a DLB trie, which acts as the s ymbol table
		long getValidPasswordsStart = System.nanoTime();
		DLB validPasswords = populateValidPasswordDLB(all_passwords);
		long getValidPasswordsTotalTime = (System.nanoTime() - getValidPasswordsStart)/1000000000;
		System.out.println("Time to set up symbol table with valid passwords: " + getValidPasswordsTotalTime + " seconds");

		while(true){
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter a password ('quit' to quit): ");
			String password = sc.nextLine();

			if(password.equals("quit")) break; //Quit the -check
			else if(password.equals("")) continue;

			password = password.toLowerCase();

			if(validPasswords.exists(password)){
				double timeToFindPassword = validPasswords.findTime(password);
				System.out.println("Total time to crack password: " + timeToFindPassword + " ms (" + (timeToFindPassword/1000) + " seconds)\n");
			} else{
				System.out.println("Invalid password! Next 10 closest passwords: ");
				String replacements[] = findAlternatives(password, AllChars);
				for(int i = 0; i < replacements.length; i++){ //Print all of the alternatives and their times
					System.out.println("\t" + replacements[i] + " : " + validPasswords.findTime(replacements[i]) + " ms");
				}
				System.out.println(); //Just make a new line for the next prompt
			}
		}
	}

	//Find all valid passwords and write them to all_passwords.txt
	public static void findPasswords(){
		try{
			//Trie of invalid passwords, given in dictionary.txt
			DLB trie = new DLB();
			populateDLB(trie);

			long start = System.nanoTime();

			File f = new File("all_passwords.txt");
			if(f.exists()) f.delete();
			f.createNewFile();
			Writer = new PrintWriter(f);

			//Generate all permutations of valid passwords and write them to the file
			printAllValidPermutations(trie, AllChars, "", 5, System.nanoTime());

			//Print statistics for the user
			long time = (System.nanoTime() - start)/1000000000;
			System.out.println("Program time: " + time + " seconds");
			System.out.println("Valid passwords: " + ValidPasswordsCounter);

			Writer.close();
		} catch(IOException e){

		}
	}

	//Populate a DLB with all of the valid passwords contained in all_passwords.txt
	public static DLB populateValidPasswordDLB(File all_passwords){
		try{
			File f = new File("all_passwords.txt");
			Scanner sc = new Scanner(f);
			DLB validPasswordsTrie = new DLB(); //New DLB to store the valid passwords in (this will be the symbol table)
			int numberOfPasswords = 0;

			while(sc.hasNextLine()){ //Loop through the liens in the file
				String line = sc.nextLine();
				if(line.length() > 5){
					if(line.length() < 5) System.out.println(line + " is less than 5");
					String password = line.substring(0, 5); //Get the password from the current line in the text file
					double time = Double.parseDouble(line.substring(6, line.length())); //Get the time from the current line in the text file
					validPasswordsTrie.insert(password, time);
					numberOfPasswords++;
				}
			}

			System.out.println("Number of passwords inserted: " + numberOfPasswords);

			return validPasswordsTrie;
		} catch(FileNotFoundException e){
			return null;
		}
	}

	//For an invalid password, generate the next 10 closest passwords to it.
	//This method is very complex; if length 5, it tries to find alternatives with the longest prefix (4); for length 4, longest prefix is 3, etc...
	public static String[] findAlternatives(String password, char[] set){
		int replacements = 0; //Keep count of the number of replacements so far
		String alternatives[] = new String[10];
		String prefix;

		//Need to generate the invalid DLB trie to check if a password is valid
		DLB invalidPasswords = new DLB();
		populateDLB(invalidPasswords);

		StringCharacterIterator it = new StringCharacterIterator(password);
		int earliestInvalidCharacter = -1;
		int currentIndex = 0;
		while(it.getIndex() < it.getEndIndex()){ //Loop through the password and preliminarly check that an a, i, 1, and 4 aren't in it
			if(it.current() == 'a' || it.current() == 'i' || it.current() == '1' || it.current() == '4'){
				earliestInvalidCharacter = currentIndex;
			}

			currentIndex++;
			it.next();
		}
		if(earliestInvalidCharacter > -1) password = (earliestInvalidCharacter == 0) ? "" : password.substring(0, earliestInvalidCharacter+1);

		if(password.length() == 5){
			prefix = password.substring(0, 4); //Get the first 4 characters and start replacing the 5th
			for(int i = 0; i < set.length; i++){
				if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++;
				String newPrefix = prefix + set[i];
				if(validPassword(newPrefix, invalidPasswords)){
					alternatives[replacements++] = newPrefix;
				}

				if(replacements == 10) break;
			}
		}

		if(replacements < 10 && password.length() >= 4){
			prefix = password.substring(0, 3); //Get the first 3 characters and start replacing the 4th and 5th
			for(int i = 0; i < set.length; i++){
				if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++;
				boolean breakI = false;
				for(int j = 0; j < set.length; j++){
					if(set[j] == 'a' || set[j] == 'i' || set[j] == '1' || set[j] == '4') j++;
					String newPrefix = prefix + set[i] + set[j];
					if(validPassword(newPrefix, invalidPasswords)){
						alternatives[replacements++] = newPrefix;
					}

					if(replacements == 10){
						breakI = true;
						break;
					}
				}
				if(breakI) break;
			}
		}

		if(replacements < 10 && password.length() >= 3){
			prefix = password.substring(0, 2); //Get the first 2 characters and start replacing the 3rd, 4th, and 5th
			for(int i = 0; i < set.length; i++){
				if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++;
				boolean breakI = false;
				for(int j = 0; j < set.length; j++){
					if(set[j] == 'a' || set[j] == 'i' || set[j] == '1' || set[j] == '4') j++;
					boolean breakJ = false;
					for(int k = 0; k < set.length; k++){
						if(set[k] == 'a' || set[k] == 'i' || set[k] == '1' || set[k] == '4') k++;
						String newPrefix = prefix + set[i] + set[j] + set[k];
						if(validPassword(newPrefix, invalidPasswords)){
							alternatives[replacements++] = newPrefix;
						}

						if(replacements == 10){
							breakJ = true;
							break;
						}
					}

					if(breakJ){
						breakI = true;
						break;
					}
				}
				if(breakI) break;
			}
		}

		if(replacements < 10 && password.length() >= 1){
			prefix = "" + password.charAt(0); //Get the first character and start replacing the 2nd, 3rd, 4th, and 5th
			for(int i = 0; i < set.length; i++){
				if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++;
				boolean breakI = false;
				for(int j = 0; j < set.length; j++){
					if(set[j] == 'a' || set[j] == 'i' || set[j] == '1' || set[j] == '4') j++;
					boolean breakJ = false;
					for(int k = 0; k < set.length; k++){
						if(set[k] == 'a' || set[k] == 'i' || set[k] == '1' || set[k] == '4') k++;
						boolean breakK = false;
						for(int l = 0; l < set.length; l++){
							if(set[l] == 'a' || set[l] == 'i' || set[l] == '1' || set[l] == '4') l++;
							String newPrefix = prefix + set[i] + set[j] + set[k] + set[l];
							if(validPassword(newPrefix, invalidPasswords)){
								alternatives[replacements++] = newPrefix;
							}

							if(replacements == 10){
								breakK = true;
								break;
							}
						}

						if(breakK){
							breakJ = true;
							break;
						}
					}

					if(breakJ){
						breakI = true;
						break;
					}
				}
				if(breakI) break;
			}
		}
		
		if(password.length() == 0){
			for(int i = 0; i < set.length; i++){
				if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++;
				boolean breakI = false;
				for(int j = 0; j < set.length; j++){
					if(set[j] == 'a' || set[j] == 'i' || set[j] == '1' || set[j] == '4') j++;
					boolean breakJ = false;
					for(int k = 0; k < set.length; k++){
						if(set[k] == 'a' || set[k] == 'i' || set[k] == '1' || set[k] == '4') k++;
						boolean breakK = false;
						for(int l = 0; l < set.length; l++){
							if(set[l] == 'a' || set[l] == 'i' || set[l] == '1' || set[l] == '4') l++;
							boolean breakL = false;
							for(int m = 0; m < set.length; m++){
								if(set[m] == 'a' || set[m] == 'i' || set[m] == '1' || set[m] == '4') m++;
								String newPrefix = "" + set[i] + set[j] + set[k] + set[l] + set[m];
								if(validPassword(newPrefix, invalidPasswords)){
									alternatives[replacements++] = newPrefix;
								}

								if(replacements == 10){
									breakL = true;
									break;
								}
							}

							if(breakL){
								breakK = true;
								break;
							}
						}

						if(breakK){
							breakJ = true;
							break;
						}
					}

					if(breakJ){
						breakI = true;
						break;
					}
				}
				if(breakI) break;
			}
		}

		return alternatives;
	}

	//Add all of the invalid passwords to the DLB supplied by dictionary.txt
	public static void populateDLB(DLB trie){
		try{
			File source = new File("dictionary.txt");
			Scanner sc = new Scanner(source);

			while(sc.hasNextLine()){
				trie.insert(sc.nextLine().toLowerCase(), 0.0); //Every inserted word is lowercase
			}
		} catch(IOException e){

		}
	}

	//Return true if any of the subsequences exist in the trie; false otherwise
	public static boolean anySubSequencesExist(String password, DLB trie){
		for(int i = 0; i < password.length(); i++){
			for(int j = i+1; j < password.length()+1; j++){ // j-i is the subsequence length from index i to j-1, inclusive
				String sequence = password.substring(i, j);
				if(trie.exists(sequence)) return true;
			}
		}

		return false;
	}

	//Recursively create all permutations of words and check whether they are valid or not.
	//If the password is valid, write it to the file (all_passwords.txt)
	public static void printAllValidPermutations(DLB trie, char set[], String prefix, int k, long startTime) {
		//Base case: k is 0, write prefix to all_passwords.txt
        if(k == 0){
            if(validPassword(prefix, trie)){ //Password is valid
				prefix += "," + (((double) (System.nanoTime() - startTime))/1000000); //Add the elapsed time (in milliseconds) to the word
				Writer.println(prefix);
				ValidPasswordsCounter++;
			}
            return;
        }

        //One by one add all characters from set and recursively call for k-1
        for(int i = 0; i < set.length; i++){
			if(set[i] == 'a' || set[i] == 'i' || set[i] == '1' || set[i] == '4') i++; //Our password cannot contain the words 'a' ('4' = 'a') or 'i' ('1' = 'i') -- This immediately prunes a large portion of the generated permutations
			String newPrefix = prefix + set[i];

			printAllValidPermutations(trie, set, newPrefix, k-1, startTime);
        }
    }

	//Convert all of the special characters in the password to their character representations by the project specifications
	public static String convertPasswordFromSpecialToLetters(String password){
		StringCharacterIterator it = new StringCharacterIterator(password);

		while(it.getIndex() < it.getEndIndex()){ //Loop through String
			byte currAsciiValue = (byte) it.current();
			switch(currAsciiValue){
				case 55: //Encountered a 7, replace it with t
					password = password.replace('7', 't');
					break;
				case 48: //Encountered a 0, replace it with o
					password = password.replace('0', 'o');
					break;
				case 51: //Encountered a 3, replace it with e
					password = password.replace('3', 'e');
					break;
				case 36: //Encountered a $, replace it with s
					password = password.replace('$', 's');
					break;
				default:
					break;
			}

			it.next();
		}

		return password;
	}

	//Return whether or not a string is a valid password
	public static boolean validPassword(String password, DLB trie){
		int characters = 0; //Number of characters in the password
		int numbers = 0; //Number of numbers in the password
		int symbols = 0; //Number of symbols in the password

		StringCharacterIterator it = new StringCharacterIterator(password);

		while(it.getIndex() < it.getEndIndex()){ //Loop through the 5-character password
			byte charAscii = (byte) it.current(); //Grab the current character's ASCII value
			if(charAscii >= 97 && charAscii <= 122){ //a-z
				characters++;
			} else if(charAscii >= 48  && charAscii <= 57){ //0-9
				numbers++;
			} else if(charAscii == 33 || charAscii == 36 || charAscii == 42 || charAscii == 64 || charAscii == 94 || charAscii == 95){ // ! @ ^ _ * $
				symbols++;
			}

			it.next();
		}

		if(!(characters >= 1 && characters <= 3 && (numbers == 1 || numbers == 2) && (symbols == 1 || symbols == 2))) return false; //Doesn't meet password requirements

		String convertedString = convertPasswordFromSpecialToLetters(password); //Convert characters such as '7' and '$' to 't' and 's' respectively
		if(anySubSequencesExist(convertedString, trie)) return false; //If any subsequences of the converted password are in the trie, then it's invalid
		else return true;
	}
}
