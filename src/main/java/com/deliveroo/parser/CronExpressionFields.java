package com.deliveroo.parser;

import static java.lang.String.format;

public class CronExpressionFields {

    private CronField minutes;
    private CronField hours;
    private CronField dayOfMonth;
    private CronField month;
    private CronField dayOfWeek;

    private String command;

    public CronExpressionFields(String arg) throws CronExpressionFieldException {
        String[] cronMembers = arg.split("\\s+");
        if (cronMembers.length != 6) {
            throw new CronExpressionFieldException("Expected [minute] [hour] [day of month] [day of week] [command] but got :" + arg);
        }

        minutes = new CronField(cronMembers[0], CronFieldType.MINUTES);
        hours = new CronField(cronMembers[1], CronFieldType.HOURS);
        dayOfMonth = new CronField(cronMembers[2], CronFieldType.DAY_OF_MONTH);
        month = new CronField(cronMembers[3], CronFieldType.MONTH);
        dayOfWeek = new CronField(cronMembers[4], CronFieldType.DAY_OF_WEEK);
        command = cronMembers[5];

    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(format("%-14s%s\n", "minute", minutes.toString()));
        b.append(format("%-14s%s\n", "hour", hours.toString()));
        b.append(format("%-14s%s\n", "day of month", dayOfMonth.toString()));
        b.append(format("%-14s%s\n", "month", month.toString()));
        b.append(format("%-14s%s\n", "day of week", dayOfWeek.toString()));
        b.append(format("%-14s%s\n", "command", command));
        return b.toString();
    }

}

