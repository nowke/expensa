package in.nowke.expensa.entity;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nav on 11/7/15.
 */
public class MyDateFormat {

    private int dayOfMonth;
    private int monthOfYear;
    private int year;

    private static String dateStr = "";

    public MyDateFormat(int dayOfMonth, int monthOfYear, int year) {
        this.dayOfMonth = dayOfMonth;
        this.monthOfYear = monthOfYear;
        this.year = year;

        this.setDateStr();
    }

    public String getDateStr() {
        return dateStr;
    }

    public MyDateFormat getDateFromStr(String dtstr) {
        String[] splitDate = dtstr.split("-");
        int day = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);
        return new MyDateFormat(day, month, year);
    }

    private void setDateStr() {
        String mon = DateFormatSymbols.getInstance().getShortMonths()[monthOfYear-1];
        dateStr = mon + " " + dayOfMonth  + ", " + year;
    }
}
