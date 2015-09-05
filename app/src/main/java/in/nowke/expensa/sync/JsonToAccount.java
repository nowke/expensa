package in.nowke.expensa.sync;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.entity.TransactionDetail;

/**
 * Created by nav on 4/9/15.
 */
public class JsonToAccount {

    private String jsonString;
    private Context context;
    private JSONObject accountObject;
    private AccountDBAdapter helper;

    private AccountDetail accountDetail;
    private List<TransactionDetail> transactionDetailList;

    private static final String USER_ID = "_uid";
    private static final String USER_NAME = "name";
    private static final String USER_CREATED = "created";
    private static final String USER_ICON_ID = "iconId";
    private static final String USER_BALANCE = "balance";
    private static final String USER_ACCOUNT_TYPE = "type";
    private static final String USER_TRANSACTIONS = "transactions";
    private static final String USER_UUID = "uuid";

    private static final String TRANS_ID = "_tid";
    private static final String TRANS_DESC = "desc";
    private static final String TRANS_AMOUNT = "amount";
    private static final String TRANS_TYPE = "type";
    private static final String TRANS_DATE = "date";

    public JsonToAccount(Context context, String jsonString) throws JSONException {
        this.context = context;
        this.jsonString = jsonString;
        this.accountObject = new JSONObject(jsonString);
        this.helper = new AccountDBAdapter(context);
        this.accountDetail = new AccountDetail();
        this.transactionDetailList = new ArrayList<>();
        parseString();
    }

    private void parseString() throws JSONException {
        accountDetail.user_id = accountObject.getInt(USER_ID);
        accountDetail.user_name = accountObject.getString(USER_NAME);
        accountDetail.user_created = accountObject.getString(USER_CREATED);
        accountDetail.user_icon_id = accountObject.getInt(USER_ICON_ID);
        accountDetail.user_balance = accountObject.getDouble(USER_BALANCE);
        accountDetail.user_account_type = accountObject.getInt(USER_ACCOUNT_TYPE);
        accountDetail.uuid = accountObject.getString(USER_UUID);

        JSONArray transactionJsonArray = accountObject.getJSONArray(USER_TRANSACTIONS);


        for (int i=0; i< transactionJsonArray.length(); i++) {
            TransactionDetail transactionDetail = new TransactionDetail();
            JSONObject transactionObject = transactionJsonArray.getJSONObject(i);

            transactionDetail.transId = transactionObject.getInt(TRANS_ID);
            transactionDetail.transDesc = transactionObject.getString(TRANS_DESC);
            transactionDetail.transAmount = transactionObject.getDouble(TRANS_AMOUNT);
            transactionDetail.transType = transactionObject.getInt(TRANS_TYPE);
            transactionDetail.transDate = transactionObject.getString(TRANS_DATE);

            transactionDetailList.add(transactionDetail);
        }
    }

    public void writeAccountToDb() {
        long newUserId = helper.addAccount(accountDetail);
        for (TransactionDetail transaction: transactionDetailList) {
            transaction.userId = (int) newUserId;
            helper.addTransaction(transaction);
        }
    }
}
