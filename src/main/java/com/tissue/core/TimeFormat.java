package com.tissue.core;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class TimeFormat {

    private DateTime start;
    private DateTime end;

    public TimeFormat(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    private Period getPeriod() {
        Period p = new Period(start, end);
        return p;
    }

    public int getYears() {
        return getPeriod().getYears();
    }

    public int getMonths() {
        return getPeriod().getMonths();
    }

    public int getWeeks() {
        return getPeriod().getWeeks();
    }

    public int getDays() {
        return getPeriod().getDays();
    }

    public int getHours() {
        return getPeriod().getHours();
    }

    public int getMinutes() {
        return getPeriod().getMinutes();
    }

    public int getSeconds() {
        return getPeriod().getSeconds();
    }
 
}
