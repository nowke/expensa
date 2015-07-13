package in.nowke.expensa.classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import in.nowke.expensa.R;

/**
 * Created by nav on 22/6/15.
 */
public class AvatarIcons {

    public List<Integer> mAvatars;
    public List<TextDrawable> mDrawables;
    ColorGenerator generator;
    TextDrawable drawable;
    Context context;
    private int[] svg_drawables = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
            R.drawable.avatar_7,
            R.drawable.avatar_8,
            R.drawable.avatar_9,
            R.drawable.avatar_10,
            R.drawable.avatar_11,
            R.drawable.avatar_12,
            R.drawable.avatar_13,
            R.drawable.avatar_14,
            R.drawable.avatar_15,
            R.drawable.avatar_16,
    };

    private int[] materialColors = {
            R.color.colorMaterialRed,
            R.color.colorMaterialPink,
            R.color.colorMaterialPurple,
            R.color.colorMaterialDeepPurple,
            R.color.colorMaterialIndigo,
            R.color.colorMaterialBlue,
            R.color.colorMaterialLightBlue,
            R.color.colorMaterialCyan,
            R.color.colorMaterialTeal,
            R.color.colorMaterialGreen,
            R.color.colorMaterialLightGreen,
            R.color.colorMaterialLime,
            R.color.colorMaterialYellow,
            R.color.colorMaterialAmber,
            R.color.colorMaterialOrange,
            R.color.colorMaterialDeepOrange,
            R.color.colorMaterialBrown,
            R.color.colorMaterialGrey,
            R.color.colorMaterialBlueGrey,
    };

    private int[] avatarIconBackgroundColors = {
            R.color.avatar_1,
            R.color.avatar_2,
            R.color.avatar_3,
            R.color.avatar_4,
            R.color.avatar_5,
            R.color.avatar_6,
            R.color.avatar_7,
            R.color.avatar_8,
            R.color.avatar_9,
            R.color.avatar_10,
            R.color.avatar_11,
            R.color.avatar_12,
            R.color.avatar_13,
            R.color.avatar_14,
            R.color.avatar_15,
            R.color.avatar_16,
    };

    public AvatarIcons(Context context) {
        this.context = context;
        generator = ColorGenerator.MATERIAL;
        mAvatars = new ArrayList<>();
        mDrawables = new ArrayList<>();
        for (int Drawable: svg_drawables) {
            mAvatars.add(Drawable);
        }
    }

    public int getAvatarCount() {
        return mAvatars.size();
    }

    public int getTextCount() {
        return materialColors.length;
    }

    public Integer getAvatarIcon(int pos) {
        return mAvatars.get(pos);
    }

    public TextDrawable getDrawable(int pos, String txt) {
        drawable = TextDrawable.builder()
                .beginConfig().width(64).height(64)
                .endConfig()
                .buildRect(txt, context.getResources().getColor(materialColors[pos]));
        return drawable;
    }

    public int getBackgroundColor(int pos) {
        if (pos < 16) {
            return avatarIconBackgroundColors[pos];
        }
        else {
            return materialColors[pos - 16];
        }
    }
}
