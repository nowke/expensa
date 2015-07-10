package in.nowke.expensa.classes;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nav on 26/6/15.
 */
public class Utilities {

    public static String getDateFromFormat(long time, String dateFormat) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format(dateFormat, cal).toString();
        return date;
    }

    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public static int getCurrentYear() {
        String curYear = getDateFromFormat(getCurrentTimeStamp(), "yyyy");
        return Integer.parseInt(curYear);
    }

    public static int getYear(long time) {
        return Integer.parseInt(getDateFromFormat(time, "yyyy"));
    }

    public static String getDate(long time) {
        int year = getYear(time);
        if (year < getCurrentYear()) {
            return getDateFromFormat(time, "MMM, yyyy");
        }
        else {
            return getDateFromFormat(time, "MMM dd");
        }
    }
}
