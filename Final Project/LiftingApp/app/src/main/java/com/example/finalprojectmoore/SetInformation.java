package com.example.finalprojectmoore;

import android.text.format.DateUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Date;

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

    // Return the converted sqlTime
    public long getDateMillis() {
        return dateMillis;
    }

    // Return the rep max for the set
    public int getOneRepMax() {
        return oneRepMax;
    }

    // Convert the sql time to milliseconds for the graph
    private long sqlTimeToMillis() {
        Timestamp sqlValue = Timestamp.valueOf(this.sqlTimestamp);
        return sqlValue.getTime();
    }
}
