package in.nowke.expensa.classes;

import android.support.v7.view.ActionMode;
import android.view.View;

/**
 * Created by nav on 23/6/15.
 */
public interface ActionCallback extends ActionMode.Callback {
    public void setClickedView(View view, int position);
}
