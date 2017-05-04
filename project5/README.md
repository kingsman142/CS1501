# CS/COE 1501 Assignment 5

## Goal:

To get hands on experience with the use of digital signatures.

Note that the result of this project should NEVER be used for any security applications.  It is purely instructive.  Always use trusted and tested crypto libraries.

## High-level description:
You will be writing two programs.  The first will generate a 512-bit RSA keypair and store the public and private keys in files named `pubkey.rsa` and `privkey.rsa`, respectively.
The second will generate and verify digital signatures using a SHA-256 hash.  You will use Java's [MessageDigest](https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html) class to complete this project.

## Specifications:
1.  Write a program named `RsaKeyGen` to generate a new RSA keypair.
	1.  To generate a keypair, follow the following steps, as described in lecture.
		1.  Pick p and q to be random primes of an appropriate size to generate a 512-bit key
		1.  Calculate n as p*q
		1.  Calculate φ(n) as (p-1)*(q-1)
		1.  Choose an e such that 1 < e < φ(n) and gcd(e, φ(n)) = 1 (e must not share a factor with φ(n))
		1.  Determine d such that d = e⁻¹ mod φ(n)
	1.  After generating e, d, and n, save e and n to `pubkey.rsa`, and d and n to `privkey.rsa`.
1.  Write a second program named `RsaSign` to sign files and verify signatures.  This program should accept two command-line arguments: a flag to specify whether to sign or verify (`s` or `v`), and the name of the file to sign/verify.
	1.  If called to sign (e.g., `java RsaSign s myfile.txt`) your program should:
		1.  Generate a SHA-256 hash of the contents of the specified file (e.g., `myfile.txt`).
		1.  "Decrypt" this hash value using the private key stored in `privkey.rsa` (i.e., raise the hash value to the d<sup>th</sup> power mod n).
			*  Your program should exit and display an error if `privkey.rsa` is not found in the current directory.
		1.  Write out the signature to a file named as the original, with an extra `.sig` extension (e.g., `myfile.txt.sig`).
	1.  If called to verify (e.g., `java RsaSign v myfile.txt`) your program should:
		1.  Read the contents of the original file (e.g., `myfile.txt`).
		1.  Generate a SHA-256 hash of the contents of the original file.
		1.  Read the signed hash of the original file from the corresponding `.sig` file (e.g., `myfile.txt.sig`).
			*  Your program should exit and display an error if the `.sig` file is not found in the current directory.
		1.  "encrypt" this value with the key from `pubkey.rsa` (i.e., raise it to the e<sup>th</sup> power mod n).
			*  Your program should exit and display an error if `pubkey.rsa` is not found in the current directory.
		1.  Compare the hash value that was generated from `myfile.txt` to the one that was recovered from the signature. Print a message to the console indicating whether the signature is valid (i.e., whether the values are the same).
