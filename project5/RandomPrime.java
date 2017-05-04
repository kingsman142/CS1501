import java.math.BigInteger;
import java.util.Random;

/**
 * A helper class for generating random prime integers
 */
public class RandomPrime {
	/**
	 * Generates a random n-bit number that is probably prime (2^-100 chance of
	 * being composite). Returns this (positive) integer as a byte[].
	 * @param n the bitlength of the requested integer
	 * @return a random n-bit (probable) prime
	 */
	public static byte[] generate(int n, Random rnd) {
		return BigInteger.probablePrime(n, rnd).toByteArray();
	}
}

