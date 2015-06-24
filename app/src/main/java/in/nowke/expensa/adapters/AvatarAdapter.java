package in.nowke.expensa.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.amulyakhare.textdrawable.TextDrawable;
import com.telly.mrvector.MrVector;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nowke.expensa.R;
import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.classes.AvatarIcons;

/**
 * Created by nav on 22/6/15.
 */
public class AvatarAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    public AvatarIcons avatarIcons;

    public AvatarAdapter(Context c) {
        mContext = c;
        avatarIcons = new AvatarIcons(c);
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return avatarIcons.getAvatarCount() + avatarIcons.getTextCount();
    }

    @Override
    public Object getItem(int position) {
        if (position < 16) {
            return avatarIcons.getAvatarIcon(position);
        }
        else {
            return avatarIcons.getDrawable(position - 16, "A");
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        Drawable drawable;
        TextDrawable drawable1;
        if (position < 16) {
            drawable = MrVector.inflate(mContext.getResources(), avatarIcons.getAvatarIcon(position));
//            if (convertView == null) {
                view = inflater.inflate(R.layout.image_avatar, parent, false);
//            }
//            else {
//                view = convertView;
//            }
            final CircleImageView circleImage = (CircleImageView) view.findViewById(R.id.avatar);
            circleImage.setImageDrawable(drawable);

            if (position == AddAccountActivity.clickedPos) {
                circleImage.setBorderWidth(8);
            }

        }
        else {
            drawable1 = avatarIcons.getDrawable(position - 16, "A");
//            if (convertView == null) {
                view = inflater.inflate(R.layout.image_avatar, parent, false);
//            }
//            else {
//                view = convertView;
//            }
            final CircleImageView circleImage = (CircleImageView) view.findViewById(R.id.avatar);
            circleImage.setImageDrawable(drawable1);

            if (position == AddAccountActivity.clickedPos) {
                circleImage.setBorderWidth(8);
            }
        }

        return view;
    }

}
