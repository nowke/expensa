package in.nowke.expensa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
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
import in.nowke.expensa.classes.AvatarIcons;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.entity.AccountDetail;

/**
 * Created by nav on 30/7/15.
 */
public class AccountListSectionedAdapter extends RecyclerView.Adapter<AccountListSectionedAdapter.MainViewHolder> {

    private Context context;
    private View emptyView;
    private LayoutInflater inflater;

    private List<AccountDetail> mainAccountData = Collections.emptyList();
    private List<AccountDetail> archiveAccountData = Collections.emptyList();
    private List<AccountDetail> trashAccountData = Collections.emptyList();

    private AvatarIcons avatarIcons;
    private Drawable drawable;
    private TextDrawable textDrawable;

    private int mainAccounts;
    private int archiveAccounts;
    private int trashAccounts;

    private static final int ACCOUNT_TYPE_MAIN = 1;
    private static final int ARCHIVE_HEADER = 2;
    private static final int ACCOUNT_TYPE_ARCHIVE = 3;
    private static final int TRASH_HEADER = 4;
    private static final int ACCOUNT_TYPE_TRASH = 5;

    public AccountListSectionedAdapter(Context context,
                                       List<AccountDetail> mainAccountData,
                                       List<AccountDetail> archiveAccountData,
                                       List<AccountDetail> trashAccountData,
                                       View emptyView) {
        this.context = context;
        this.mainAccountData = mainAccountData;
        this.archiveAccountData = archiveAccountData;
        this.trashAccountData = trashAccountData;
        this.emptyView = emptyView;
        inflater = LayoutInflater.from(context);
        avatarIcons = new AvatarIcons(context);

        this.mainAccounts = mainAccountData.size();
        this.archiveAccounts = archiveAccountData.size();
        this.trashAccounts = trashAccountData.size();
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ACCOUNT_TYPE_TRASH:
            case ACCOUNT_TYPE_MAIN:
            case ACCOUNT_TYPE_ARCHIVE:
                View view = inflater.inflate(R.layout.account_row, parent, false);
                emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                AccountViewHolder accountViewHolder = new AccountViewHolder(view);
                return accountViewHolder;
            default:
                View rowView = inflater.inflate(R.layout.account_row_title, parent, false);
                emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                AccountHeaderViewHolder accountHeaderViewHolder = new AccountHeaderViewHolder(rowView);
                return accountHeaderViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ACCOUNT_TYPE_MAIN:
            case ACCOUNT_TYPE_ARCHIVE:
            case ACCOUNT_TYPE_TRASH:
                AccountDetail current = getItem(position, viewType);
                AccountViewHolder accountViewHolder = (AccountViewHolder) holder;
                String uDate = Utilities.getDate(Long.parseLong(current.user_created));

                accountViewHolder.userName.setText(current.user_name);
                accountViewHolder.userId.setText(String.valueOf(current.user_id));
                accountViewHolder.userAccountType.setText(String.valueOf(current.user_account_type));
                accountViewHolder.userDate.setText(uDate);

                if (current.user_icon_id < 16) {
                    drawable = MrVector.inflate(context.getResources(), avatarIcons.getAvatarIcon(current.user_icon_id));
                    accountViewHolder.userIcon.setImageDrawable(drawable);
                } else {
                    textDrawable = avatarIcons.getDrawable(current.user_icon_id - 16, String.valueOf(current.user_name.charAt(0)));
                    accountViewHolder.userIcon.setImageDrawable(textDrawable);
                }

                if (current.user_balance >= 0) {
                    accountViewHolder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialTeal));
                    accountViewHolder.userBalance.setText(Html.fromHtml(current.user_balance.toString() + " &uarr;"));
                }
                else {
                    accountViewHolder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialRed));
                    accountViewHolder.userBalance.setText(Html.fromHtml(String.valueOf(Math.abs(current.user_balance)) + " &darr;"));
                }
                break;

            case ARCHIVE_HEADER:
                AccountHeaderViewHolder accountHeaderViewHolderArchive = (AccountHeaderViewHolder) holder;
                accountHeaderViewHolderArchive.accountHeader.setText("Archives");
                break;
            case TRASH_HEADER:
                AccountHeaderViewHolder accountHeaderViewHolderTrash = (AccountHeaderViewHolder) holder;
                accountHeaderViewHolderTrash.accountHeader.setText("Trash");
                break;
        }
        
    }

    private AccountDetail getItem(int position, int viewType) {
        switch (viewType) {
            case ACCOUNT_TYPE_MAIN:
                return mainAccountData.get(position);
            case ACCOUNT_TYPE_ARCHIVE:
                if (mainAccounts == 0) {
                    return archiveAccountData.get(position -1);
                }
                else {
                    return archiveAccountData.get(position - mainAccounts - 1);
                }
            case ACCOUNT_TYPE_TRASH:
                if (archiveAccounts == 0 && mainAccounts == 0) {
                    return trashAccountData.get(position - 1);
                }
                else if (archiveAccounts == 0) {
                    return trashAccountData.get(position - mainAccounts - 1);
                }
                else if (mainAccounts == 0) {
                    return trashAccountData.get(position - archiveAccounts - 2);
                }
                else {
                    return trashAccountData.get(position - mainAccounts - archiveAccounts - 2);
                }
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mainAccounts > 0 && position < mainAccounts) {
            return ACCOUNT_TYPE_MAIN;
        }
        else if (archiveAccounts > 0 && ((mainAccounts > 0 && position == mainAccounts) || (mainAccounts == 0 && position == 0) )) {
            return ARCHIVE_HEADER;
        }

        else if (archiveAccounts > 0 && ((mainAccounts > 0 && position > mainAccounts && position <= mainAccounts + archiveAccounts)
                || (mainAccounts == 0 && position > 0 && position <= archiveAccounts)) ) {
            return ACCOUNT_TYPE_ARCHIVE;
        }

        else if (trashAccounts > 0 && (( mainAccounts > 0  &&
                ((archiveAccounts > 0 && position == mainAccounts + archiveAccounts + 1) || (archiveAccounts == 0 && position == mainAccounts)))
                || ( mainAccounts == 0 && ((archiveAccounts > 0 && position == archiveAccounts + 1)
                || (archiveAccounts == 0 && position == 0))))) {
            return TRASH_HEADER;
        }
        else {
            return ACCOUNT_TYPE_TRASH;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (archiveAccounts == 0) { count += mainAccounts; }
        else { count += mainAccounts + archiveAccounts + 1; }

        if (trashAccounts > 0) { count += trashAccounts + 1; }
        return count;
    }

    class MainViewHolder extends  RecyclerView.ViewHolder {

        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }

    class AccountViewHolder extends MainViewHolder {

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

    class AccountHeaderViewHolder extends MainViewHolder {

        TextView accountHeader;

        public AccountHeaderViewHolder(View itemView) {
            super(itemView);

            accountHeader = (TextView) itemView.findViewById(R.id.accountHeader);

            itemView.setClickable(false);
        }
    }
}
