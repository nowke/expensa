package in.nowke.expensa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.telly.mrvector.MrVector;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nowke.expensa.R;
import in.nowke.expensa.classes.AccountDetail;
import in.nowke.expensa.classes.AvatarIcons;

/**
 * Created by nav on 17/6/15.
 */
public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private AvatarIcons avatarIcons;

    private Drawable drawable;
    private TextDrawable textDrawable;

    List<AccountDetail> data = Collections.emptyList();

    public AccountListAdapter(Context context, List<AccountDetail> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        avatarIcons = new AvatarIcons(context);;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.account_row, parent, false);
        AccountViewHolder holder = new AccountViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        AccountDetail current = data.get(position);

        holder.userName.setText(current.user_name);
        holder.userBalance.setText(current.user_balance.toString());
        holder.userId.setText(String.valueOf(current.user_id));

        if (current.user_icon_id < 16) {
            drawable = MrVector.inflate(context.getResources(), avatarIcons.getAvatarIcon(current.user_icon_id));
            holder.userIcon.setImageDrawable(drawable);
        }
        else {
            textDrawable = avatarIcons.getDrawable(current.user_icon_id - 16, String.valueOf(current.user_name.charAt(0)));

            holder.userIcon.setImageDrawable(textDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(AccountDetail item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIcon;
        TextView userName;
        TextView userBalance;
        TextView userId;

        public AccountViewHolder(View itemView) {
            super(itemView);

            userIcon = (CircleImageView) itemView.findViewById(R.id.userIcon);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userBalance = (TextView) itemView.findViewById(R.id.userBalance);
            userId = (TextView) itemView.findViewById(R.id.userId);

            itemView.setClickable(true);

        }
    }
}
