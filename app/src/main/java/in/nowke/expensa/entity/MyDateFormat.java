package in.nowke.expensa.entity;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public MyDateFormat(int dayOfMonth, String monthInStr, int year) {
        this.dayOfMonth = dayOfMonth;
        this.year = year;

        try {
            Date tempdate = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthInStr);
            Calendar tempcal = Calendar.getInstance();
            tempcal.setTime(tempdate);
            this.monthOfYear = tempcal.get(Calendar.MONTH) + 1;
            this.setDateStr();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDateStr() {
        return dateStr;
    }

    public static MyDateFormat getDateFromStr(String dtstr) {
        String[] splitDate = dtstr.split(" ");
        String month = splitDate[0];
        int day = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);
        return new MyDateFormat(day, month, year);
    }

    private void setDateStr() {
        String mon = DateFormatSymbols.getInstance().getShortMonths()[monthOfYear-1];
        dateStr = mon + " " + dayOfMonth  + " " + year;
    }

    public int getDay() {
        return dayOfMonth;
    }

    public String getMonth() {
        return DateFormatSymbols.getInstance().getShortMonths()[monthOfYear-1];
    }

    public int getYear() {
        return year;
    }
}
