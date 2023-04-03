/**
 * a class
 * @author CMPUT301W23T49
 * @version 1.0
 * @since [Monday April 3]
 */
package com.example.codecatchersapp;

import com.google.firebase.firestore.GeoPoint;

import java.security.NoSuchAlgorithmException;

/**
 * A class representing a Monster with a unique hash, name, and score.
 */
public class Monster {
    private String monsterSHAHash;

    private String monsterBinaryHash;
    private String monsterName;
    private String monsterScore;

    private GeoPoint geoloc;

    /**
     * Constructs a Monster object with a given hash, name generated by MonsterNameGenerator, and score generated by Score class.
     * @param monsterSHAHash the unique hash of the Monster.
     */
    public Monster(String monsterSHAHash) {
        this.monsterSHAHash = monsterSHAHash;
        this.monsterBinaryHash = HexBinaryConverter.hexToBinary(monsterSHAHash);
        this.monsterName = new MonsterNameGenerator().generateNameFromBinary(monsterBinaryHash);
        try {
            Score score = new Score(this.monsterSHAHash);
            this.monsterScore = score.getScore();
        } catch (NoSuchAlgorithmException e) {
            this.monsterScore = "0";
        }
        this.geoloc = null;
    }

    public Monster(String monsterSHAHash, GeoPoint geoloc) {
        this.monsterSHAHash = monsterSHAHash;
        this.monsterBinaryHash = HexBinaryConverter.hexToBinary(monsterSHAHash);
        this.monsterName = new MonsterNameGenerator().generateNameFromBinary(monsterBinaryHash);
        try {
            Score score = new Score(this.monsterSHAHash);
            this.monsterScore = score.getScore();
        } catch (NoSuchAlgorithmException e) {
            this.monsterScore = "0";
        }
        this.geoloc = geoloc;
    }

    /**
     * Returns the hash of the Monster.
     * @return the hash of the Monster.
     */
    public String getMonsterSHAHash() {
        return monsterSHAHash;
    }

    public String getMonsterBinaryHash() {
        return monsterBinaryHash;
    }

    /**
     * Returns the name of the Monster.
     * @return the name of the Monster.
     */
    public String getMonsterName() {
        return monsterName;
    }

    /**
     * Returns the score of the Monster.
     * @return the score of the Monster.
     */
    public String getMonsterScore() {
        return monsterScore;
    }

    public GeoPoint getGeoPoint(){
        return geoloc;
    }

    public void setGeoPoint(GeoPoint geoloc) {this.geoloc = geoloc;}
}
