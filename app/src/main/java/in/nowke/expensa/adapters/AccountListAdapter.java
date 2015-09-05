package in.nowke.expensa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.classes.AvatarIcons;
import in.nowke.expensa.classes.Utilities;

/**
 * Created by nav on 17/6/15.
 */
public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private AvatarIcons avatarIcons;

    private Drawable drawable;
    private TextDrawable textDrawable;
    private View emptyView;
    List<AccountDetail> data = Collections.emptyList();

    public AccountListAdapter(Context context, List<AccountDetail> data, View emptyView) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.emptyView = emptyView;
        avatarIcons = new AvatarIcons(context);
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.account_row, parent, false);
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        AccountViewHolder holder = new AccountViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {

        AccountDetail current = data.get(position);
        String uDate = Utilities.getDate(Long.parseLong(current.user_created));

        holder.userName.setText(current.user_name);
        holder.userId.setText(String.valueOf(current.user_id));
        holder.userAccountType.setText(String.valueOf(current.user_account_type));
        holder.userDate.setText(uDate);

        if (current.user_icon_id < 16) {
            drawable = MrVector.inflate(context.getResources(), avatarIcons.getAvatarIcon(current.user_icon_id));
            holder.userIcon.setImageDrawable(drawable);
        } else {
            textDrawable = avatarIcons.getDrawable(current.user_icon_id - 16, String.valueOf(current.user_name.charAt(0)));
            holder.userIcon.setImageDrawable(textDrawable);
        }

        if (current.user_balance >= 0) {
            holder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialTeal));
            holder.userBalance.setText(Html.fromHtml(current.user_balance.toString() + " &uarr;"));
        }
        else {
            holder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialRed));
            holder.userBalance.setText(Html.fromHtml(String.valueOf(Math.abs(current.user_balance)) + " &darr;"));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void add(AccountDetail item) {
        data.add(0, item);
        notifyItemInserted(0);
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void updateAccountAmount(int position, Double amount) {
        AccountDetail currentAccount = data.get(position);
        currentAccount.user_balance += amount;
        notifyItemChanged(position);
    }

    public void changeAccountAmount(int position, Double amount) {
        AccountDetail currentAccount = data.get(position);
        currentAccount.user_balance = amount;
        notifyItemChanged(position);
    }

    public void updateAccountNameAndIcon(int position, String accountName, int iconId) {
        AccountDetail currentAccount = data.get(position);
        currentAccount.user_name = accountName;
        currentAccount.user_icon_id = iconId;
        notifyItemChanged(position);
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userIcon;
        TextView userName;
        TextView userBalance;
        TextView userId;
        TextView userDate;
        TextView userAccountType;

        public AccountViewHolder(View itemView) {
            super(itemView);

            userIcon = (CircleImageView) itemView.findViewById(R.id.userIcon);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userBalance = (TextView) itemView.findViewById(R.id.userBalance);
            userId = (TextView) itemView.findViewById(R.id.userId);
            userDate = (TextView) itemView.findViewById(R.id.userLastTransDate);
            userAccountType = (TextView) itemView.findViewById(R.id.userAccountType);

            itemView.setClickable(true);
        }
    }

}
