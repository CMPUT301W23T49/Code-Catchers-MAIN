package com.example.codecatchersapp;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
/**
 The HexBinaryConverter class contains methods for converting hexadecimal and binary strings, as well as generating
 SHA-256 hashes and extracting the first six bits of a hash.
 @author [Josie Matalski]
 @version 1.0
 @since [Saturday March 4 2021]
 */
/**
 * The HexBinaryConverter class provides methods for converting between hexadecimal and binary strings,
 * generating a random SHA-256 hash, and getting the first six bits of the hash value.
 */
public class HexBinaryConverter {
    /**
     * Converts an array of bytes to a hexadecimal string.
     * @param bytes the array of bytes to be converted
     * @return the hexadecimal string representation of the input bytes
     */
    // Takes an array of bytes as input and converts it to a hexadecimal string.
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    /**
     * Converts a hexadecimal string to a binary string.
     * @param hex the hexadecimal string to be converted
     * @return the binary string representation of the input hexadecimal string
     */
    // Takes a hexadecimal string as input and converts it to a binary string.
    public static String hexToBinary(String hex) {
        BigInteger bigInt = new BigInteger(hex, 16);
        String bin = bigInt.toString(2);
        int padding = hex.length() * 4 - bin.length();
        if (padding > 0) {
            bin = String.format("%0" + padding + "d", 0) + bin;
        }
        return bin;
    }
    /**
     * Generates a random string, calculates its SHA-256 hash value, and returns the hash value as an array of bytes.
     * @param input the input string
     * @return the SHA-256 hash value of the input string
     */
    // Generates a random string
    // and calculates its SHA-256 hash value using the MessageDigest class
    public static byte[] getSHA256Digest(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Random rand = new Random();
            String hashInput = Integer.toString(rand.nextInt());
            byte[] hash = digest.digest(hashInput.getBytes("UTF-8"));
            String hexHash = bytesToHex(hash);
            String binaryHash = hexToBinary(hexHash);
            String shawNumber = getFirstSixBits(hexHash);
            System.out.println("Generated SHA-256 hash: " + hexHash);
            System.out.println("Binary representation: " + binaryHash);
            System.out.println("First six bits: " + shawNumber);
            return hash;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Gets the first six bits of a hexadecimal string representation of a hash value.
     * @param hex the hexadecimal string representation of a hash value
     * @return the first six bits of the hash value
     */
    // Get first six bits of the hash value
    // Takes the hash value generated by the getSHA256Digest method
    // Converts it to a binary string using the hexToBinary method
    // returns the first six bits of the binary string
    public static String getFirstSixBits(String hex) {
        String binaryString = hexToBinary(hex.substring(0, 2)).substring(2)
                + hexToBinary(hex.substring(2, 4))
                + hexToBinary(hex.substring(4, 6));
        return binaryString.substring(0, 6);
    }
}
