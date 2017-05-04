import java.util.Random;
import java.io.*;

public class RsaKeyGen{
	public static void main(String[] args){
		Random rand = new Random();

		//Generate p and q
		BigPrime p = new BigPrime(RandomPrime.generate(256, rand));
		BigPrime q = new BigPrime(RandomPrime.generate(256, rand));

		//n = p*q
		BigPrime n = p.multiplyPrimes(q);

		BigPrime pMinusOne = p.subtractOne(); // p-1
		BigPrime qMinusOne = q.subtractOne(); // q-1
		BigPrime phi = new BigPrime(pMinusOne.increaseByOneByte(pMinusOne.multiplyPrimes(qMinusOne).getData())); // phi(n) = (p-1)*(q-1)
		BigPrime e = generateE(); //Generate an e that's co-prime to phi(n); it's ok if this value is low
		BigPrime d = new BigPrime(null); //Initialize d to zero until further calculations are made

		while(!BigPrime.gcd(e, phi).isOne()){
			e = e.addPrimes(new BigPrime(new byte[] { 2 })); //e += 2
		}

		d = e.modInverse(phi); //d = e^-1 mod phi(n)

		writeVals(e, d, n); //Write the public and private key files
	}

	//Generate an initialized e value of 3
	public static BigPrime generateE(){
		byte three = 3;
		BigPrime e = new BigPrime(null);
		BigPrime initValue = new BigPrime(new byte[] { three }); //Initial value to 3
		e = e.addPrimes(initValue); //Add 3 to e, initializing it to 3
		return e;
	}

	public static void writeVals(BigPrime e, BigPrime d, BigPrime n){
		try{
			//Write e and n to the public key file
			FileOutputStream publicKeyFile = new FileOutputStream("pubkey.rsa");
			ObjectOutputStream publicWrite = new ObjectOutputStream(publicKeyFile);
			publicWrite.writeObject(e);
			publicWrite.writeObject(n);
			publicWrite.close();

			//Write d and n to the private key file
			FileOutputStream privateKeyFile = new FileOutputStream("privkey.rsa");
			ObjectOutputStream privateWrite = new ObjectOutputStream(privateKeyFile);
			privateWrite.writeObject(d);
			privateWrite.writeObject(n);
			privateWrite.close();

			System.out.println("Wrote keys to pubkey.rsa and privkey.rsa!");
		} catch(IOException exception){
			System.out.println("Error: " + exception.toString());
			return;
		}
	}
}
