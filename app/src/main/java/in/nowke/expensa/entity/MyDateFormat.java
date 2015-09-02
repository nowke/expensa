package in.nowke.expensa.entity;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import in.nowke.expensa.classes.Utilities;

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

    public static MyDateFormat getDateFromStr(String dtstr) {
        String[] splitDate = dtstr.split("-");
        int month = Integer.parseInt(splitDate[1]);
        int day = Integer.parseInt(splitDate[2]);
        int year = Integer.parseInt(splitDate[0]);
        return new MyDateFormat(day, month, year);
    }

    private void setDateStr() {
        dateStr = String.format("%s-%s-%s", year, String.format("%02d", monthOfYear), String.format("%02d", dayOfMonth));
    }

    public int getDay() {
        return dayOfMonth;
    }

    public String getMonth() {
        return DateFormatSymbols.getInstance().getShortMonths()[monthOfYear-1];
    }

    public int getMonthInt() {
        return monthOfYear-1;
    }

    public int getYear() {
        return year;
    }

    public static String getReadableDateStr(String dateString) {
        MyDateFormat newDate = getDateFromStr(dateString);
        String readableStr;
        if (newDate.getYear() < Utilities.getCurrentYear()) {
            readableStr = newDate.getMonth() + " " + newDate.getDay() + ", " + newDate.getYear();
        }
        else {
            readableStr = newDate.getMonth() + " " + newDate.getDay();
        }
        return readableStr;
    }
}
