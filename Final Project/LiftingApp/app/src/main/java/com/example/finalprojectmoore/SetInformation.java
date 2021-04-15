package com.example.finalprojectmoore;

import java.sql.Timestamp;

public class SetInformation {
    int exercise_id;
    String sqlTimestamp;
    int oneRepMax;
    long dateMillis;

    public SetInformation(int exercise_id, String sqlTimestamp, int oneRepMax) {
        this.exercise_id = exercise_id;
        this.sqlTimestamp = sqlTimestamp;
        this.oneRepMax = oneRepMax;
        this.dateMillis = sqlTimeToMillis();
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public int getOneRepMax() {
        return oneRepMax;
    }

    private long sqlTimeToMillis() {
        Timestamp sqlValue = Timestamp.valueOf(this.sqlTimestamp);
        return sqlValue.getTime();
    }
}
