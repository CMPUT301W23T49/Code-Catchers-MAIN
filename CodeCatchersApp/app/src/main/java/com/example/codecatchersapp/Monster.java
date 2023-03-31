package com.example.codecatchersapp;

import java.security.NoSuchAlgorithmException;

/**
 * A class representing a Monster with a unique hash, name, and score.
 */
public class Monster {
    private String monsterHash;
    private String monsterName;
    private String monsterScore;

    private Double Latitude;
    private Double Longitude;

    /**
     * Constructs a Monster object with a given hash, name generated by MonsterNameGenerator, and score generated by Score class.
     * @param monsterHash the unique hash of the Monster.
     */
    public Monster(String monsterHash) {
        this.monsterHash = monsterHash;
        this.monsterName = new MonsterNameGenerator().generateName(monsterHash);
        try {
            Score score = new Score(this.monsterHash);
            this.monsterScore = score.getScore();
        } catch (NoSuchAlgorithmException e) {
            this.monsterScore = "0";
        }
    }

    public Monster(String monsterHash, Double latitude, Double longitude) {
        this.monsterHash = monsterHash;
        Latitude = latitude;
        Longitude = longitude;
    }

    /**
     * Returns the hash of the Monster.
     * @return the hash of the Monster.
     */
    public String getMonsterHash() {
        return monsterHash;
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
}
