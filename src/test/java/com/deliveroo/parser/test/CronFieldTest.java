package com.deliveroo.parser.test;

import com.deliveroo.parser.CronExpressionFieldException;
import com.deliveroo.parser.CronField;
import com.deliveroo.parser.CronFieldType;
import org.junit.Test;

import static org.junit.Assert.*;
public class CronFieldTest {

    /**
     * Test valid range expressions (E.g. 1-5, 0-3, etc. )
     */
    @Test
    public void correct_range() throws CronExpressionFieldException {
        CronField f = new CronField("1-5", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2 3 4 5", f.toString());
        f = new CronField("1-1", CronFieldType.DAY_OF_MONTH);
        assertEquals("1", f.toString());
        f = new CronField("1-2", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2", f.toString());
        f = new CronField("1-15", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15", f.toString());


        f = new CronField("0-1", CronFieldType.HOURS);
        assertEquals("0 1", f.toString());
        f = new CronField("0-3", CronFieldType.HOURS);
        assertEquals("0 1 2 3", f.toString());
        f = new CronField("0", CronFieldType.HOURS);
        assertEquals("0", f.toString());
        f = new CronField("23", CronFieldType.HOURS);
        assertEquals("23", f.toString());
    }

    /**
     * Test invalid range expressions (E.g. 1-100 for month, 0-3 for year, etc. )
     */
    @Test
    public void incorrect_range() {
        tryParse("0-5", CronFieldType.DAY_OF_MONTH, "outside valid range");
        tryParse("0-5-6", CronFieldType.DAY_OF_MONTH, "Invalid number");
        tryParse("1-32", CronFieldType.DAY_OF_MONTH, "outside valid range");
        tryParse("1-0", CronFieldType.DAY_OF_MONTH, "ends before it starts");
    }


    /**
     * Test correct fixed value cron field ( e.g. 1,3,4 or 15,16 )
     */
    @Test
    public void correct_fixed_values() throws CronExpressionFieldException {
        CronField f = new CronField("1", CronFieldType.DAY_OF_MONTH);
        assertEquals("1", f.toString());
        f = new CronField("1,2,3,4,5", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2 3 4 5", f.toString());
        f = new CronField("1,1,1", CronFieldType.DAY_OF_MONTH);
        assertEquals("1", f.toString());
        f = new CronField("1,2", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2", f.toString());
        f = new CronField("2,1,3,5,6,7,4", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 2 3 4 5 6 7", f.toString());


        f = new CronField("0,1", CronFieldType.HOURS);
        assertEquals("0 1", f.toString());
        f = new CronField("0,1,2,3", CronFieldType.HOURS);
        assertEquals("0 1 2 3", f.toString());
        f = new CronField("0", CronFieldType.HOURS);
        assertEquals("0", f.toString());
        f = new CronField("0,0", CronFieldType.HOURS);
        assertEquals("0", f.toString());
        f = new CronField("23,0", CronFieldType.HOURS);
        assertEquals("0 23", f.toString());
    }

    /**
     * Test incorrect fixed value cron field ( e.g. 1,3,100 for MONTH or 15,16 for DAY_OF_WEEK )
     */
    @Test
    public void incorrect_fixed_values() {
        tryParse("0,5", CronFieldType.DAY_OF_MONTH, "outside valid range");
        tryParse("1,32", CronFieldType.DAY_OF_MONTH, "outside valid range");
    }

    /**
     * Test correct interval expressions ( e.g. * , *\/15 , *\/20 )
     */
    @Test
    public void correct_intervals() throws CronExpressionFieldException {
        CronField f = new CronField("*/10", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 11 21 31", f.toString());
        f = new CronField("*/20", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 21", f.toString());
        f = new CronField("*/30", CronFieldType.DAY_OF_MONTH);
        assertEquals("1 31", f.toString());
        f = new CronField("*/40", CronFieldType.DAY_OF_MONTH);
        assertEquals("1", f.toString());


        f = new CronField("*/10", CronFieldType.HOURS);
        assertEquals("0 10 20", f.toString());
        f = new CronField("*/15", CronFieldType.HOURS);
        assertEquals("0 15", f.toString());
        f = new CronField("*/20", CronFieldType.HOURS);
        assertEquals("0 20", f.toString());
        f = new CronField("*/23", CronFieldType.HOURS);
        assertEquals("0 23", f.toString());
        f = new CronField("*/24", CronFieldType.HOURS);
        assertEquals("0", f.toString());

        f = new CronField("*", CronFieldType.DAY_OF_WEEK);
        assertEquals("1 2 3 4 5 6 7", f.toString());

        f = new CronField("*", CronFieldType.MONTH);
        assertEquals("1 2 3 4 5 6 7 8 9 10 11 12", f.toString());
    }

    @Test
    public void incorrect_intervals()  {
        tryParse("*/0", CronFieldType.DAY_OF_MONTH, "interval is 0");
        tryParse("*/10/10", CronFieldType.DAY_OF_MONTH, "has too many intervals");
        tryParse("*/A", CronFieldType.DAY_OF_MONTH, "Invalid number 'A'");
        tryParse("A/A", CronFieldType.DAY_OF_MONTH, "Invalid number 'A'");
        tryParse("0/0", CronFieldType.DAY_OF_MONTH, "Number 0/0 for DAY_OF_MONTH interval is 0");
        tryParse("0/15", CronFieldType.DAY_OF_MONTH, "Number 0/15 for DAY_OF_MONTH is outside valid range (1-31)");
    }

    private void tryParse(String incomingText, CronFieldType fieldType, String msg) {
        try {
            new CronField(incomingText, fieldType);
            fail(incomingText + " should not be a valid " + fieldType);
        } catch (CronExpressionFieldException e) {
            assertTrue(e.getMessage().contains(msg));
            assertTrue(e.getMessage().contains(fieldType.toString()));
        }
    }


}
