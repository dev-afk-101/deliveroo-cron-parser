package com.deliveroo.parser;

import java.util.Arrays;
public class Main {


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected [minute] [hour] [day of month] [day of week] [command] but got :" + Arrays.toString(args));
            return;
        }

        try {
            CronExpressionFields expr = new CronExpressionFields(args[0]);
            System.out.println(expr);

        } catch (CronExpressionFieldException invalidCronExpression) {
            System.err.println(invalidCronExpression.getMessage());
        }
    }
}

