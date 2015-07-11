package in.nowke.expensa.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.entity.TransactionDetail;

/**
 * Created by nav on 11/7/15.
 */
public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder> {

    private LayoutInflater inflater;
    private Context context;


    List<TransactionDetail> data = Collections.emptyList();

    public TransactionListAdapter(Context context, List<TransactionDetail> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.transaction_row, parent, false);
        TransactionViewHolder holder = new TransactionViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        TransactionDetail current = data.get(position);

        holder.transTitle.setText(current.transDesc);
        holder.transId.setText(String.valueOf(current.transId));
        holder.transAmount.setText(String.valueOf(current.transAmount));
        holder.transDate.setText(current.transDate);

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


    class TransactionViewHolder extends RecyclerView.ViewHolder {

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
