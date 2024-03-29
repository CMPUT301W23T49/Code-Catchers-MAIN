/**
 * a class
 * @author CMPUT301W23T49
 * @version 1.0
 * @since [Monday April 3]
 */
package com.example.codecatchersapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 A class responsible for generating SHA-256 hashes and writing them to a Firestore database.
 @author [Josie Matalski]
 @version 1.0
 @since [Sunday March 4 2021]
 */
public class HashGenerator {                                                            // https://www.baeldung.com/sha-256-hashing-java
    public static String generateAndWriteHash(String input) {                           // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
