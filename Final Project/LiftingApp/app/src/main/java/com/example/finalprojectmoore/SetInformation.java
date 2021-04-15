package com.example.finalprojectmoore;

import java.sql.Timestamp;

public class SetInformation {
    int weight;
    int reps;
    String sqlTimestamp;
    int oneRepMax;
    long dateMillis;

    public SetInformation(int weight, int reps, String sqlTimestamp) {
        this.weight = weight;
        this.reps = reps;
        this.sqlTimestamp = sqlTimestamp;
        this.oneRepMax = oneRepMaxBrzycki(this.weight, this.reps);
        this.dateMillis = sqlTimeToMillis();
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public int getOneRepMax() {
        return oneRepMax;
    }

    // Calculate one rep max with Brzycki formula
    private int oneRepMaxBrzycki(int weight, int reps) {
        double oneRepMax = weight / (1.0278 - (0.0278 * reps));
        return (int) Math.round(oneRepMax);
    }

    private long sqlTimeToMillis() {
        Timestamp sqlValue = Timestamp.valueOf(this.sqlTimestamp);
        return sqlValue.getTime();
    }
}
