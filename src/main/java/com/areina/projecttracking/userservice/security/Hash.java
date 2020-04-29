package com.areina.projecttracking.userservice.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.UnknownFormatConversionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;



public class Hash {

	public static String generatePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

		int iterations = 1000;
		char[] chars = password.toCharArray();
		byte[] salt = getSalt();
		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		String merged = toHex(hash) + "-" + salt.toString();
		return merged;

	}

	private static byte[] getSalt() throws NoSuchAlgorithmException {

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	private static String toHex(byte[] array) throws NoSuchAlgorithmException, UnknownFormatConversionException {

		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString();
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0", +paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

}
