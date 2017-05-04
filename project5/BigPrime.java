import java.io.Serializable;

public class BigPrime implements Serializable{
	private byte[] data; //Store the byte array of the data in the class
	private final static BigPrime ONE = new BigPrime(new byte[] { 1 }); //Global constant object  containing the value 1

	public BigPrime(byte[] arr){
		if(arr == null) data = new byte[65]; //Default class is 65*8 0 bits
		else data = arr;
	}

	//Add two unsigned binary numbers together
	public BigPrime addPrimes(BigPrime num){
		//Null checks
		if(num == null) return null;
		byte[] numData = num.getData();
		if(data == null || numData == null) return null;

		//Do these checks to make sure we can add two differing length binary numbers together; stop considering the other
		//	binary number when i >= minLength.
		int maxLength = data.length >= numData.length ? data.length : numData.length;
		int minLength = data.length >= numData.length ? numData.length : data.length;
		boolean thisPrimeIsBigger = data.length > numData.length;
		byte[] output = new byte[maxLength];

		boolean carry = false; //Is there a bit carry?
		byte currBitVal = 1; //Used to AND with the current byte to get its bit; 1 = 0x00000001, 2 = 0x00000010, 4 = 0x00000100, ...
		for(int i = 0; i < maxLength; i++){ //Scan through the bytes of each number right to left
			byte currByteWrite = 0; //Write this byte to the output

			for(int j = 7; j >= 0; j--){ //Scan through each byte right to left
				byte bit1 = 0;
				byte bit2 = 0;

				if(i >= minLength){ //One number has more bits than the other, so if we exceed the bits of the smaller number, just directly copy the larger number over
					//Now decide which binary number is larger and read the bit from it
					if(thisPrimeIsBigger){
						bit1 = (byte) ((data[data.length-i-1] & currBitVal) == 0 ? 0 : 1);
						bit2 = 0;
					} else{
						bit1 = 0;
						bit2 = (byte) ((numData[numData.length-i-1] & currBitVal) == 0 ? 0 : 1);
					}
				} else{ //Have not exceeded the bit length of either binary number
					bit1 = (byte) ((data[data.length-i-1] & currBitVal) == 0 ? 0 : 1);
					bit2 = (byte) ((numData[numData.length-i-1] & currBitVal) == 0 ? 0 : 1);
				}

				byte newBit = 0; //New bit to write to the byte

				if(bit1 + bit2 == 2){ //bit1 = 1 and bit2 = 1
					if(!carry){ // 1 + 1 = 0 carry 1
						newBit = 0;
						carry = true;
					} else{ // 1 + 1 + 1 = 1 carry 1
						newBit = 1;
						carry = true;
					}
				} else if(bit1 + bit2 == 1){ //one of the bits is 1 and the other one is 0
					if(!carry){ // 1 + 0 = 1
						newBit = 1;
						carry = false;
					} else{ // 1 + 0 + 1 = 0 carry 1
						newBit = 0;
						carry = true;
					}
				} else if(bit1 + bit2 == 0){ //bit1 = 0 and bit2 = 0
					newBit = (byte) (carry ? 1 : 0); //If there's a carry, the new bit will be 1; otherwise, it'll stay at 0
					carry = false;
				}

				currByteWrite |= ((newBit == 0) ? 0 : currBitVal); //If the new bit is on (1), then set that bit in the byte
				currBitVal *= 2; //AND with 1, then 2, then 4, then 8, 16, 32, 64, 128 for each byte
			}

			output[output.length-i-1] = currByteWrite; //Write the new byte data to the output
			currBitVal = 1; //Reset the current spot in the byte
		}

		BigPrime outputPrime = new BigPrime(output); //Convert the byte array of the output to a BigPrime
		return outputPrime;
	}

	//Multiply two unsigned binary numbers
	public BigPrime multiplyPrimes(BigPrime num){
		return gradeSchoolMult(num); //Perform grade school multiplication on the two numbers
	}

	//Returns true if the values in two byte arrays representing primes are equal; returns false otherwise
	public boolean arePrimesEqual(BigPrime num){
		if(num == null) return false;
		byte[] numData = num.getData();
		if(data.length != numData.length || data == null || num == null) return false;

		int maxLength = Math.max(data.length, numData.length);

		for(int i = 0; i < maxLength; i++){ //Scan through the bits in the two numbers; if they differ at any point, then return false
			if(i < data.length && i < numData.length){ //We haven't exceeded the length of either binary number
				if(data[i] != numData[i]) return false;
			} else{ //At this point, one binary number is longer than the other.  So if the longer number has any 1s past this point, they're not equal
				if(i >= data.length){
					if(numData[i] != 0) return false;
				} else if(i >= numData.length){
					if(data[i] != 0) return false;
				}
			}
		}

		return true; //Scanned through both numbers completely and found no differences
	}

	//Subtract the value one from an odd binary number
	public BigPrime subtractOne(){
		byte[] output = copyArray(data);
		output[output.length-1] &= 0xFE; //AND with 1111 1110 to make sure the last bit goes to a 0, thus subtracting one
		return new BigPrime(output);
	}

	//Find the modular inverse (a^-1 mod b) of two numbers
	public BigPrime modInverse(BigPrime num){
		//Add one to phi(n)
		BigPrime phiPlusOne = new BigPrime(copyArray(num.getData()));
		phiPlusOne = phiPlusOne.addPrimes(ONE);

		//If the equation is d = e^-1 mod phi(n), then attempt to satisfy the equation ed = k*phi(n) + 1 where k*phi(n) is a multiple of phi(n)
		while(!phiPlusOne.modulus(this).isZero()){ //When e is divided from both sides, the remainder must be 0 to satisfy the equation, otherwise, add another multiple of phi(n)
			phiPlusOne = phiPlusOne.addPrimes(num);
		}

		return phiPlusOne.divide(this); //We know the remainder is zero, so just do phi(n)+1/n
	}

	//Divide two unsigned binary numbers
	public BigPrime divide(BigPrime num){
		if(compare(num) == -1) return new BigPrime(null); //a/b = a when a < b
		else if(compare(num) == 0) return ONE; //Return 1 when the numbers are equal

		BigPrime remainder = new BigPrime(null);
		BigPrime quotient = new BigPrime(null);
		quotient = quotient.setNumBits(this.getBitLength()/8); //Set the length of the quotient to be this BigPrime's size
		for(int i = 0; i < this.getBitLength(); i++){ //Iterate over the bits of this BigPrime object
			remainder = remainder.shiftLeft(1);
			if(getIthBit(i) == true) remainder = remainder.setLSB(true);

			BigPrime remainderCopy = remainder.trimLeadingZeros();
			if(remainderCopy.compare(num) >= 0){
				remainder = remainder.subtract(num);
				quotient = quotient.setIthBit(i);
			}
		}

		return quotient;
	}

	//Perform the modulus operation on two unsigned binary numbers (a mod b = remainder of a/b)
	public BigPrime modulus(BigPrime num){
		if(compare(num) == -1) return this; //a mod b = a when a < b
		else if(compare(num) == 0) return new BigPrime(null); //Return 1 when a = b

		//a % b
		//modulus = a - a/b where a/b is integer multiplication
		//Below uses a simple algorithm as discussed on the line above
		BigPrime divResult = this.divide(new BigPrime(num.getData()));
		BigPrime mulResult = divResult.multiplyPrimes(new BigPrime(num.getData()));
		BigPrime remResult = this.subtract(new BigPrime(mulResult.getData()));
		return remResult;
	}

	//Calculate the greatest common divisor of two unsigned binary numbers a and b
	public static BigPrime gcd(BigPrime a, BigPrime b){
		//If either number is zero, the GCD is the other number
		if(a.isZero()) return b;
		if(b.isZero()) return a;

		int shift; //Keep a shift counter variable that will be used later
		for(shift = 0; a.or(b).andOne().isZero(); shift++){
			a = a.shiftRight(1);
			b = b.shiftRight(1);
		}

		while (a.andOne().isZero()){ //If the LSB of a is 0, then shift right until the LSB is 1.
									 //This forces a to be odd.  If a LSB = 1, that means a is divisible by 2.
			a = a.shiftRight(1);
		}

		do{
			while (b.andOne().isZero()){ //If the LSB = 0, then b is divisible by 2, as mentioned above.
				b = b.shiftRight(1);
		    }

			if (a.compare(b) == 1) { //Make it so a < b so subtraction is easier and won't produce a negative number (since we're only working with unsigned)
				//Swap the references of a and b
				BigPrime temp = b;
				b = a;
				a = temp;
			}
			b = b.subtract(a); //An easy subtraction since b > a.  b is now even because odd - odd = even.  Repeat this process until b = 0
		} while(!b.isZero());

		return a.shiftLeft(shift);
	}

	//Logical AND with an unsigned binary number
	public BigPrime andOne(){
		byte[] newData = copyArray(data);
		boolean andIsOne = (newData[newData.length-1] & 0x1) != 0;

		for(int i = 0; i < newData.length; i++){ //Copy the old data into the new byte array
			newData[i] = 0;
		}

		if(andIsOne) newData[newData.length-1] = 0x1; //If the AND produced a 1, write a 1 to the appropriate byte

		return new BigPrime(newData);
	}

	//Logical OR with an unsigned binary number
	public BigPrime or(BigPrime num){
		byte[] numData = num.getData();
		byte[] output = new byte[data.length];

		for(int i = 0; i < data.length; i++){ //Iterate over the bytes of each binary number
			int currBitVal = 1;

			for(int j = 7; j >= 0; j--){ //Iterate through the bits in both numbers' bytes
				byte bit1 = 0;
				byte bit2 = 0;

				if(i >= numData.length){ //If one number is longer than the other, just grab the data from one number so we don't get an ArrayIndexOutOfBoundsException
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = 0;
				} else{ //We have not exceeded the bounds of either binary number, so grab the bit of each
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = (byte) (numData[numData.length-i-1] & currBitVal);
				}

				byte bitWrite = 0; //The byte to write to the new output
				if(bit1 == 0 && bit2 == 0) bitWrite = 0; // 0 | 0 = 0
				else if(bit1 != 0 && bit2 == 0) bitWrite = (byte) currBitVal; // 1 | 0 = 1
				else if(bit1 != 0 && bit2 != 0) bitWrite = (byte) currBitVal; // 1 | 1 = 1
				else if(bit1 == 0 && bit2 != 0) bitWrite = (byte) currBitVal; // 0 | 1 = 1

				output[data.length-i-1] |= bitWrite; //Flip the bit on if the bit being written is not 0

				currBitVal *= 2;
			}
		}

		return new BigPrime(output);
	}

	//Perform modular exponentiation with two numbers, a^pow mod n
	public BigPrime modPow(BigPrime pow, BigPrime mod){
		boolean useMod = (mod != null); //Determines whether we are using the modulus function in this modPow

		BigPrime result = new BigPrime(null);
		result = result.addPrimes(ONE); //Start with result = 1

		BigPrime base = new BigPrime(copyArray(data));
		BigPrime powCopy = new BigPrime(copyArray(pow.getData()));

		while(!powCopy.isZero()){ //Divide the power in half for each iteration
			if(!powCopy.isEven()){ //lsb = 1, so the power is odd.  Thus, multiply the result by the base and do a mod
				if(useMod) result = result.multiplyPrimes(base).modulus(mod);
				else result = result.multiplyPrimes(base);
			}

			//Divide the power in half
			powCopy = powCopy.shiftRight(1);

			//For every iteration, multiply the base by itself.  If performing mod function, mod this multiplication result.
			if(useMod) base = base.multiplyPrimes(base).modulus(mod);
			else base = base.multiplyPrimes(base);
		}

		return result;
	}

	//Compare two binary numbers.  Returns an int 1 (this > num), 0 (this = num), or -1 (this < num).
	public int compare(BigPrime num){
		//Trim the leading zeros from both binary numbers so no unnecessary comparisons are performed that might throw it off
		BigPrime thisShort = this.trimLeadingZeros();
		BigPrime numShort = num.trimLeadingZeros();

		//After trimming unneeded zeros, if one binary number is longer than the other, they are greater than the other
		if(thisShort.getBitLength() > numShort.getBitLength()){
			return 1;
		} else if(thisShort.getBitLength() < numShort.getBitLength()){
			return -1;
		}

		int lengthOfData = thisShort.getData().length;
		byte[] thisData = thisShort.getData();
		byte[] numData = numShort.getData();

		byte currBitVal = (byte) 0x40; //start currBitVal equal to 0x01000000 because Java screws everything up when right shifting 0x10000000
		for(int i = 0; i < lengthOfData; i++){ //Scan through the bytes of each number right to left
			//Perform the initial and with 0x10000000 that we couldn't do initially with currBitVal
			byte bit1Pre = (byte) ((thisData[i] & 0x80) == 0 ? 0 : 1);
			byte bit2Pre = (byte) ((numData[i] & 0x80) == 0 ? 0 : 1);

			if(bit1Pre > bit2Pre){
				return 1;
			} else if(bit1Pre < bit2Pre){
				return -1;
			}

			for(int j = 6; j >= 0; j--){ //Scan through each bit left to right (MSB to LSB).  If one of the bits is a 1 and the other is a 0, then we know how each binary number compares to the other.
				byte bit1 = (byte) ((thisData[i] & currBitVal) == 0 ? 0 : 1);
				byte bit2 = (byte) ((numData[i] & currBitVal) == 0 ? 0 : 1);

				if(bit1 > bit2){
					return 1;
				} else if(bit1 < bit2){
					return -1;
				}

				currBitVal >>= 1; //0x01000000 becomes 0x00100000 becomes 0x00010000 becomes 0x00001000 and so on...
			}

			currBitVal = (byte) 0x40; //Reset currBitVal to 0x01000000
		}

		return 0; //No for sure decision has been made yet whether one is greater than the other, so just return that they're equal
	}

	public String toString(){
		String output = "";

		for(int i = 0; i < data.length; i++){
			output += String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
		}

		return output;
	}

	public byte[] getData(){
		return data;
	}

	public int getBitLength(){
		return data.length * 8;
	}

	//Returns whether a binary number is equal to the value 1
	public boolean isOne(){
		if(data[data.length-1] != 1) return false; //If the smallest byte is not even 1, then immediately return false
		for(int i = 0; i < data.length-1; i++){ //Otherwise, iterate over all the bytes to make sure they equal 0 except for the least significant byte, which sould be 1
			if(data[i] != 0) return false;
		}

		return true;
	}

	//Returns whether a binary number is equal to the value 0
	public boolean isZero(){
		for(int i = 0; i < data.length; i++){ //Make sure every byte/bit has the value 0
			if(data[i] != 0) return false;
		}

		return true;
	}

	//Returns whether a binary number is even
	public boolean isEven(){
		byte lsb = (byte) (data[data.length-1] & 0x1); //If the LSB = 0, it's even; if the LSB = 1, it's odd
		return lsb == 0; //Check LSB
	}

	//Returns whether the MSB of a binary number is 1
	public boolean msbIsOne(){
		byte msb = (byte) (data[0] & 0x80);
		return msb != 0;
	}

	//Set the LSB of a binary number; set = true means assign a 1; set = false means assign a 0
	public BigPrime setLSB(boolean set){
		byte[] newData = copyArray(data);
		if(set) newData[newData.length-1] |= 0x1;
		else newData[newData.length-1] &= 0xFE;

		return new BigPrime(newData);
	}

	//Set the iTH bit of a binary number to true (1)
	public BigPrime setIthBit(int bitNum){
		int byteNumber = bitNum/8;
		int bitIndex = 7 - (bitNum - (byteNumber*8));
		int bitIndexVal = (int) Math.pow(2, bitIndex); //If the bit index = 3, then 2^3 = 0x00001000, so turn that bit "on" in order to OR it later

		byte[] newData = copyArray(data);
		newData[byteNumber] |= bitIndexVal; //Find that specific byte of the bit and OR it at the appropriate bit value
		return new BigPrime(newData);
	}

	//Get the iTH bit of a binary number; returns true (1) or false (1)
	public boolean getIthBit(int bitNum){
		//Calculate the appropriate index of the bit and the byte index within the byte array
		int byteNumber = bitNum/8;
		int bitIndex = 7 - (bitNum - (byteNumber*8));
		int bitIndexVal = (int) Math.pow(2, bitIndex);

		byte result = (byte) (data[byteNumber] & bitIndexVal); //If result = 0, the bit is 0; otherwise, the bit is 1
		return result != 0;
	}

	//Set the number of bytes/bits that a binary number has.  This helps correct the default byte number (65) in the constructor if necessary.
	public BigPrime setNumBits(int num){
		byte[] newData = new byte[num];
		return new BigPrime(newData);
	}

	////////////////////////////////////////
	//Helper Methods
	////////////////////////////////////////

	//Shift this data to the left by n bits
	public BigPrime shiftLeft(int n){
		byte[] dataCopy = data;
		boolean carry = false;

		for(int i = 0; i < n; i++){ //Perform n shifts
			carry = false;

			int mustExpandData = dataCopy[0] & 128; //If the MSB is a 1, when we shift left, we must add more space to hold the shifted values
			if(mustExpandData == 0) dataCopy = copyArray(dataCopy);
			else dataCopy = increaseByOneByte(dataCopy);

			for(int j = dataCopy.length-1; j >= 0; j--){ //Shift every bit to the left by 1
				byte msb = (byte) (dataCopy[j] & 128); //Get the msb of this byte so if we shift it left by 1, make sure to transfer it to the next byte of data

				dataCopy[j] <<= 1; //Shift left by 1
				if(carry) dataCopy[j] |= 1; //If there's a carry, set the LSB to 1

				if(msb == 0) carry = false;
				else carry = true;
			}
		}

		return new BigPrime(dataCopy);
	}

	//Shift this data to the right by n bits
	public BigPrime shiftRight(int n){
		byte[] dataCopy = copyArray(data);

		for(int i = 0; i < n; i++){ //Perform n shifts
			boolean carry = false;

			for(int j = 0; j < dataCopy.length; j++){ //Shift every bit to the right by 1
				byte lsb = (byte) (dataCopy[j] & 1); //Get the LSB of this byte so if we shift it right by 1, make sure to transfer it to the next byte of data

				boolean hasMSBOfByte = (dataCopy[j] & 0x80) != 0;
				dataCopy[j] = (byte) ((dataCopy[j] & 0xFF) >> 1); //Shift right by 1;
				if(carry) dataCopy[j] |= 128; //If there's a carry, set the MSB to 1

				if(lsb == 0) carry = false;
				else carry = true;
			}

			dataCopy[0] &= 0x7F;
		}

		return new BigPrime(dataCopy);
	}

	//Perform the grade school multiplication algorithm on two unsigned binary numbers
	private BigPrime gradeSchoolMult(BigPrime num){
		if(num == null) return null;
		byte[] numData = num.getData();
		if(numData == null) return null;

		BigPrime sum = new BigPrime(null); //Start at a sum of 0
		BigPrime thisCopy = new BigPrime(copyArray(data));
		BigPrime numCopy = new BigPrime(copyArray(num.getData()));

		while(!numCopy.isZero()){ //For every iteration, while the multiplier is not 0, keep on adding and shifting in 0s
			if(!numCopy.isEven()){ //lsb = 1, so we add on the current value
				sum = sum.addPrimes(new BigPrime(increaseByOneByte(thisCopy.getData())));
			}

			thisCopy = thisCopy.shiftLeft(1);
			numCopy = numCopy.shiftRight(1);
		}

		return sum;
	}

	//Trim the bytes containing only 0s from a binary number
	public BigPrime trimLeadingZeros(){
		int leadingZeroBytes = 0;
		for(int i = 0; i < data.length; i++){ //Count the number of leading bytes that have value 0
			if(data[i] == 0) leadingZeroBytes++;
			else break;
		}

		byte[] newData = new byte[data.length-leadingZeroBytes]; //Initialize a new byte array with size of the original minus the number of leading 0 bytes
		for(int i = 0; i < newData.length; i++){ //Copy the old data into the new array
			newData[i] = data[i+leadingZeroBytes];
		}

		return new BigPrime(newData);
	}

	//Subtract two unsigned binary numbers
	public BigPrime subtract(BigPrime num){
		byte[] numData = num.getData();
		byte[] output = new byte[data.length];

		//Trim leading zeros of the binary number so a simple length comparison is an easy calculation
		BigPrime thisShort = this.trimLeadingZeros();
		BigPrime numShort = num.trimLeadingZeros();
		if(thisShort.getData().length < numShort.getData().length || (data.length == numData.length && (data[0] < numData[0]))) return new BigPrime(null); //We're subtracting a larger number from a smaller number, so the result will just be 0
		if(thisShort.compare(numShort) == -1 || thisShort.compare(numShort) == 0) return new BigPrime(null); //If a-b and a < b OR a=b, then return 0

		boolean borrow = false;
		boolean carry = false;
		for(int i = 0; i < data.length; i++){ //Iterate over the bytes in both numbers
			int currBitVal = 1; //1, then 2, then 4, then 8, then 16, and so on.  This helps grab the bit value of each byte; multiply by 2 for every iteration
			if(i == data.length-1 && data[0] == 0) continue;

			for(int j = 7; j >= 0; j--){ //Iterate through the bits in both numbers' bytes
				byte bit1 = 0;
				byte bit2 = 0;

				if(i >= numData.length){ //One binary number is longer than the other so once we reach this point, subtract the longer number from 0
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = 0;
				} else{ //Both binary numbers are still within the correct indexable range so we don't have to worry about an ArrayIndexOutOfBoundsException
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = (byte) (numData[numData.length-i-1] & currBitVal);
				}

				if(borrow){ //If we have to borrow a bit, turn a 1 into a 0 and perform necessary operations below
					if(bit1 != 0){ //bit1 = 1
						bit1 = 0;
						borrow = false;
					} else{ //bit1 = 0
						bit1 = (byte) currBitVal; //We can't borrow anything
					}
				}

				byte bitWrite = 0;
				if(bit1 == 0 && bit2 == 0) bitWrite = 0; // 0 - 0 = 0
				else if(bit1 != 0 && bit2 == 0) bitWrite = bit1; // 1 - 0 = 1
				else if(bit1 != 0 && bit2 != 0) bitWrite = 0; // 1 - 1 = 0
				else if(bit1 == 0 && bit2 != 0){ //0 - 1 = 1 borrow 1   --   edge case
					bitWrite = (byte) currBitVal; //Assume that this carry will work and turn this bit to 1
					borrow = true; //Borrow from the first 1 we find
				}

				output[data.length-i-1] |= bitWrite; //Flip the bit on if the bit being written is not 0

				currBitVal *= 2; //Multiply by 2 so we can get the bit in the next index of the byte
			}
		}

		return new BigPrime(output);
	}

	//Copy the contents of a byte array to another new byte array with a different reference
	private static byte[] copyArray(byte[] arr){
		byte[] output = new byte[arr.length];

		for(int i = 0; i < arr.length; i++){
			output[i] = arr[i];
		}

		return output;
	}

	//Copy the contents of a byte array into another new byte array with a different reference; this time, add an extra byte to the front with 8 leading 0 bits
	public static byte[] increaseByOneByte(byte[] arr){
		byte[] output = new byte[arr.length+1];

		for(int i = 0; i < arr.length; i++){
			output[i+1] = arr[i];
		}

		return output;
	}
}
