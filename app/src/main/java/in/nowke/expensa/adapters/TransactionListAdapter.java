package in.nowke.expensa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.entity.TransactionDetail;

/**
 * Created by nav on 11/7/15.
 */
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MainViewHolder> {

    private LayoutInflater inflater;
    private Context context;

    private  static final int ITEM_TYPE_HEADER = 1;
    private  static final int ITEM_NORMAL = 2;

    List<TransactionDetail> data = Collections.emptyList();

    public TransactionListAdapter(Context context, List<TransactionDetail> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       switch (viewType) {
           case ITEM_NORMAL:
               View view = inflater.inflate(R.layout.transaction_row, parent, false);
               TransactionViewHolder holder = new TransactionViewHolder(view);
               return holder;
           case ITEM_TYPE_HEADER:
               View viewHeader = inflater.inflate(R.layout.transaction_row_header, parent, false);
               UserHeaderViewHolder headerViewHolder = new UserHeaderViewHolder(viewHeader);
               return headerViewHolder;
           default:
               return null;
       }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        }
        else {
            return ITEM_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM_NORMAL:
                TransactionDetail current = data.get(position);
                TransactionViewHolder itemNormalHolder = (TransactionViewHolder) holder;

                itemNormalHolder.transTitle.setText(current.transDesc);
                itemNormalHolder.transId.setText(String.valueOf(current.transId));
                itemNormalHolder.transDate.setText(current.transDate);

                if (current.transType == 0) {
                    itemNormalHolder.transAmount.setTextColor(context.getResources().getColor(R.color.colorMaterialTeal));
                    itemNormalHolder.transAmount.setText(Html.fromHtml(current.transAmount.toString() + " &uarr;"));
                }
                else {
                    itemNormalHolder.transAmount.setTextColor(context.getResources().getColor(R.color.colorMaterialRed));
                    itemNormalHolder.transAmount.setText(Html.fromHtml(String.valueOf(Math.abs(current.transAmount)) + " &darr;"));
                }

                break;

            case ITEM_TYPE_HEADER:
                TransactionDetail userHeader = data.get(position);
                UserHeaderViewHolder headerViewHolder = (UserHeaderViewHolder) holder;

                String uDate = Utilities.getDate(Long.parseLong(userHeader.userCreated));

                if (userHeader.userBalance >= 0) {
                    headerViewHolder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialTeal));
                    headerViewHolder.userBalance.setText(Html.fromHtml(String.valueOf(userHeader.userBalance + " &uarr;")));
                }
                else {
                    headerViewHolder.userBalance.setTextColor(context.getResources().getColor(R.color.colorMaterialRed));
                    headerViewHolder.userBalance.setText(Html.fromHtml(String.valueOf(Math.abs(userHeader.userBalance)) + " &darr;"));
                }

                headerViewHolder.userCreated.setText("Created: " + uDate);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void add(TransactionDetail item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void updateBalance(Double amount) {
        TransactionDetail headerData = data.get(0);
        headerData.userBalance += amount;
        notifyItemChanged(0);
    }

    public void changeBalance(Double amount) {
        TransactionDetail headerData = data.get(0);
        headerData.userBalance = amount;
        notifyItemChanged(0);
    }

    public void editTransaction(int position, String newTitle, Double newAmount, int newType, String newDate, Double newTotalBalance) {
        TransactionDetail currentData = data.get(position);
        currentData.transDesc = newTitle;
        currentData.transAmount = newAmount;
        currentData.transType = newType;
        currentData.transDate = newDate;
        changeBalance(newTotalBalance);
        notifyItemChanged(position);
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        public MainViewHolder(View itemView) {
            super(itemView);
        }
    }

    class UserHeaderViewHolder extends MainViewHolder {

        TextView userCreated;
        TextView userBalance;

        public UserHeaderViewHolder(View itemView) {
            super(itemView);

            userCreated = (TextView) itemView.findViewById(R.id.userCreatedHeader);
            userBalance = (TextView) itemView.findViewById(R.id.userBalanceHeader);
        }
    }

    class TransactionViewHolder extends MainViewHolder {

        TextView transTitle;
        TextView transDate;
        TextView transAmount;
        TextView transId;

        public TransactionViewHolder(View itemView) {
            super(itemView);

            transTitle = (TextView) itemView.findViewById(R.id.transTitle);
            transDate = (TextView) itemView.findViewById(R.id.transDate);
            transAmount = (TextView) itemView.findViewById(R.id.transAmount);
            transId = (TextView) itemView.findViewById(R.id.transId);

            itemView.setClickable(true);

        }
    }

}
