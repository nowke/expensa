package in.nowke.expensa.classes;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import in.nowke.expensa.entity.AccountDetail;
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

    public static List<List<AccountDetail>> splitAccounts(List<AccountDetail> data) {
        List<List<AccountDetail>> splittedAccounts = new ArrayList<>();
        List<AccountDetail> mainAccounts = new ArrayList<>();
        List<AccountDetail> archiveAccounts = new ArrayList<>();
        List<AccountDetail> trashAccounts = new ArrayList<>();

        for (AccountDetail account : data) {
            switch (account.user_account_type) {
                case 1:
                    mainAccounts.add(account);
                    break;
                case 2:
                    archiveAccounts.add(account);
                    break;
                case 3:
                    trashAccounts.add(account);
            }
        }
        splittedAccounts.add(mainAccounts);
        splittedAccounts.add(archiveAccounts);
        splittedAccounts.add(trashAccounts);

        return splittedAccounts;
    }

    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    public static int getCurrentYear() {
        String curYear = getDateFromFormat(getCurrentTimeStamp(), "yyyy");
        return Integer.parseInt(curYear);
    }

    public static int getCurrentDay() {
        String curDay = getDateFromFormat(getCurrentTimeStamp(), "dd");
        return Integer.parseInt(curDay);
    }

    public static int getCurrentMonth() {
        String curMonth = getDateFromFormat(getCurrentTimeStamp(), "M");
        return Integer.parseInt(curMonth);
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

    public static void hideKeyboard(Context context, EditText editText) {
        // Hides the Keyboard input from 'editText'
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
