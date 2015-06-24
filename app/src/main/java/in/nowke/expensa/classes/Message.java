package in.nowke.expensa.classes;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by nav on 1/5/15.
 */
public class Message {
    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
