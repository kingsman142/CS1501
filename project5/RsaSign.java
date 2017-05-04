import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.security.MessageDigest;

public class RsaSign{
	private static File file;

	public static void main(String[] args){
		if(args.length == 2){ //Requires two command-line arguments for the flag (sign or verify) and the file
			char flag = args[0].charAt(0);
			if(flag != 's' && flag != 'v'){
				System.out.println("===== Invalid flag! =====");
				return;
			}
			String filename = args[1];
			file = new File(filename);
			if(!file.exists()){
				System.out.println("===== File doesn't exist! =====");
				return;
			}

			if(flag == 's') sign();
			else if(flag == 'v') verify();
		} else{
			System.out.println("===== This program requires 2 arguments! =====");
			return;
		}
	}

	//Sign a file using RSA encryption
	private static void sign(){
		try{
			Path path = file.toPath();
			byte[] data = Files.readAllBytes(path);

			//Create MessageDigest class to generate a SHA-256 hash for our message
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			byte[] digest = md.digest(); //Generate a hash of the file

			File f = new File("privkey.rsa");
			if(!f.exists()){ //If the private key file doesn't exist in this directory, exit
				System.out.println("===== Private Key File Not Found! =====");
				return;
			}
			FileInputStream privateKey = new FileInputStream("privkey.rsa"); //Opens privkey.rsa
			ObjectInputStream keyReader = new ObjectInputStream(privateKey);
			BigPrime d = (BigPrime) keyReader.readObject();	//Reads the first object as d
			BigPrime n = (BigPrime) keyReader.readObject();	//Reads the second object as n

			keyReader.close();

			BigPrime hash = new BigPrime(n.increaseByOneByte(digest)); //Pass the hash into a BigPrime class so we can perform operations
			BigPrime decryptedData = hash.modPow(d, n);

			FileOutputStream signedFile = new FileOutputStream(file.getName() + ".sig"); //Add the ".sig" extension to the newly signed file
			ObjectOutputStream signedWriter = new ObjectOutputStream(signedFile);
			signedWriter.writeObject(data); //Write the file data to the signed file
			signedWriter.writeObject(decryptedData); //Write the encrypted data to the signed file
			signedWriter.close();
		} catch(Exception e){
			System.out.println("Error: " + e);
			e.printStackTrace();
			return;
		}
	}

	//Verify the sender of a file for their identify using RSA
	private static void verify(){
		try{
			//Create a MessageDigest class to generate a SHA-256 hash of the file data
			MessageDigest mdOne = MessageDigest.getInstance("SHA-256");
			mdOne.update(Files.readAllBytes(file.toPath()));
			byte[] hash = mdOne.digest(); //Generate the file hash

			File f = new File(file.getName() + ".sig");
			if(!f.exists()){ //If we're verifying file.txt, make sure file.txt.sig is in the current directory
				System.out.println("===== The file " + file.getName() + ".sig doesn't exist! =====");
				return;
			}

			//Read both the data and encrypted data that was stored in the signed file
			FileInputStream signedFile = new FileInputStream(file.getName() + ".sig");
			ObjectInputStream signedReader = new ObjectInputStream(signedFile);
			byte[] data = (byte[]) signedReader.readObject();
			BigPrime decryptedData = (BigPrime) signedReader.readObject();

			signedReader.close();

			//Hash the original data with SHA-256
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data);
			byte[] hashTwo = md.digest();
			BigPrime originalHash = new BigPrime(BigPrime.increaseByOneByte(hashTwo)); //The original hash of the file

			File f2 = new File("pubkey.rsa");
			if(!f2.exists()){ //Make sure the public key is stored in the current directory
				System.out.println("===== Public Key File Not Found! =====");
				return;
			}

			//Read the e and n values (public key) from the public key file
			FileInputStream publicKey = new FileInputStream("pubkey.rsa");
			ObjectInputStream keyReader = new ObjectInputStream(publicKey);
			BigPrime e = (BigPrime) keyReader.readObject();
			BigPrime n = (BigPrime) keyReader.readObject();

			keyReader.close();

			BigPrime encryptedData = decryptedData.modPow(e, n); //c = m^e mod n

			//Trim the leading zeros from both binary values so comparison is easy and simple
			encryptedData = encryptedData.trimLeadingZeros();
			originalHash = originalHash.trimLeadingZeros();

			boolean validSignature = encryptedData.arePrimesEqual(originalHash); //Ensure that the read in data and the encrypted data are the same, hence the verification is passed and the sender is using the correct key values
			if(validSignature) System.out.println("This signature is valid!");
			else System.out.println("This signature is NOT valid!");
		} catch(Exception exception){
			System.out.println("Error: " + exception);
		}
	}
}
