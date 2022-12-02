package com.danielx31.ehataw;

import org.joda.time.LocalDate;

import java.util.Date;

public class DateManager {

    private LocalDate localDate;

    public DateManager(Date date) {
        localDate = new LocalDate(date);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public Date getTomorrowDate() {
        localDate.plusDays(1);
        return localDate.toDate();
    }

    @Override
    public String toString() {
        return localDate.toString();
    }



}
