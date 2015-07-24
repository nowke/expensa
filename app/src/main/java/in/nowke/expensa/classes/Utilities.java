package in.nowke.expensa.classes;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

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

    public static class AccountSlideInAnimator extends SlideInRightAnimator {
        public AccountSlideInAnimator() {
            this.setAddDuration(400);
            this.setChangeDuration(200);
            this.setRemoveDuration(200);
            this.setMoveDuration(200);
        }
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

    private static boolean isDateToday(long time) {
        String date = getDateFromFormat(time, "dd MMM");
        String currentDate = getDateFromFormat(getCurrentTimeStamp(), "dd MMM");
        return date.equals(currentDate);
    }

    public static String getDate(long time) {
        int year = getYear(time);
        if (year < getCurrentYear()) {
            return getDateFromFormat(time, "MMM, yyyy");
        }
        else if (isDateToday(time)) {
            return getDateFromFormat(time, "h:mm A");
        }
        else {
            return getDateFromFormat(time, "MMM dd");
        }
    }
}
