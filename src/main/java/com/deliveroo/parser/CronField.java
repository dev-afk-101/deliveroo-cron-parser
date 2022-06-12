package com.deliveroo.parser;

import java.util.*;
import java.util.stream.Collectors;

public class CronField {

    private final String incomingText;
    private CronFieldType type;
    private Set<String> values = new LinkedHashSet<>();
    List<String> months = Arrays.asList("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC");
    List<String> days = Arrays.asList("SUN","MON","TUE","WED","THU","FRI","SAT");
    public CronField(String incomingText, CronFieldType type) throws CronExpressionFieldException {
        this.type = type;
        this.incomingText = incomingText;

        if(incomingText.contains(",")) {
            parseFixedValues();
        } else if (incomingText.contains("-")) {
            List<String> data = null;
            if (CronFieldType.DAY_OF_WEEK.equals(type)) {
                data = days;
            } else if (CronFieldType.MONTH.equals(type)) {
                data = months;
            }
            parseRangeOfValues(data);
        } else if (incomingText.equals("*")) {
            parseAll();
        } else if (incomingText.contains("/")) {
            parseIntervals();
        } else {
            int e = parseNumber(incomingText);
            populateValues(e,e,1);
        }

        if (values.isEmpty()) {
            parseNumber(incomingText);
            values.add(incomingText);
        }
    }



    public Set<String> getValues() {
        return values;
    }

    public String toString() {
        return values.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    private void parseAll() throws CronExpressionFieldException {
        int interval = 1;
        populateValues(type.min, type.max, interval);
    }
    private void parseIntervals() throws CronExpressionFieldException {
            int interval = 1;
            String[] intervals = incomingText.split("/");
            if (intervals.length > 2) {
                throw new CronExpressionFieldException("Number " + incomingText + " for " + type + "has too many intervals");
            }
            if (intervals.length == 2) {
                interval = parseNumber(intervals[1]);
            }
            if (intervals[0].equals("*")){
                populateValues(type.min, type.max, interval);
            } else {
                populateValues(parseNumber(intervals[0]), type.max, interval);
            }

    }

    private void parseRangeOfValues(List<String> data) throws CronExpressionFieldException {
        String[] range = incomingText.split("-");
        if (range.length == 2) {
            try{
                int start = parseNumber(range[0]);
                int end = parseNumber(range[1]);
                populateValues(start, end, 1);
            } catch (NumberFormatException ex) {
                if (null == data) {
                    throw new CronExpressionFieldException(type+" input combination is not valid. Please use either week or months syntax");
                }
                int stIdx = data.indexOf(range[0]);
                int endIdx = data.indexOf(range[1]);
                populateWordFormValues(stIdx,endIdx,data,1);
            }
        }
    }

    private void parseFixedValues() throws CronExpressionFieldException {
        String[] fixedDates = incomingText.split(",");
        for (String item: fixedDates) {
            parseNumber(item);
        }
        Arrays.sort(fixedDates, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            }
        });
        if (fixedDates.length > 1) {
            //fixed values
            for (String date : fixedDates) {
                int e = parseNumber(date);
                populateValues(e, e, 1);
            }
        }
    }

    private void populateValues(int start, int end, int increment) throws CronExpressionFieldException {
        if (increment == 0) {
            throw new CronExpressionFieldException("Number " + incomingText + " for " + type + " interval is 0");
        }
        if (end < start) {
            throw new CronExpressionFieldException("Number " + incomingText + " for " + type + " ends before it starts");
        }
        if (start < type.min || end > type.max) {
            throw new CronExpressionFieldException("Number " + incomingText + " for " + type + " is outside valid range (" + type.min + "-" + type.max + ")");
        }

            for (int i = start; i <= end; i += increment) {
                values.add(String.valueOf(i));
            }
    }

    private void populateWordFormValues(int start, int end, List<String> data, int increment) throws CronExpressionFieldException {
        if (start == -1 || end == -1) {
            throw new CronExpressionFieldException("Input " + incomingText + " for " + type + " is outside valid range");
        }
        if (increment == 0) {
            throw new CronExpressionFieldException("Input " + incomingText + " for " + type + " interval is 0");
        }
        if (end < start) {
            throw new CronExpressionFieldException("Input " + incomingText + " for " + type + " ends before it starts");
        }

        for (int i = start; i <= end; i += increment) {
            values.add(data.get(i));
        }
    }
    private Integer parseNumber(String no) throws CronExpressionFieldException {
        try {
            return Integer.parseInt(no);
        } catch (NumberFormatException nfe) {
            throw new CronExpressionFieldException("Invalid number '" + no + "' in field " + type + ": " + nfe.getMessage());
        }
    }
}

