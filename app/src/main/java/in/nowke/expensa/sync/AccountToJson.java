package in.nowke.expensa.sync;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.entity.TransactionDetail;

/**
 * Created by nav on 2/9/15.
 * A helper class to convert Account information (including transaction) to JSON string
 */
public class AccountToJson {

    private long accountId;
    private Context context;
    private AccountDBAdapter helper;
    private JSONObject accountObj;
    private AccountDetail accountDetail;
    private List<TransactionDetail> transactionDetailList;

    private String jsonString;

    private static final String USER_ID = "_uid";
    private static final String USER_NAME = "name";
    private static final String USER_CREATED = "created";
    private static final String USER_ICON_ID = "iconId";
    private static final String USER_BALANCE = "balance";
    private static final String USER_ACCOUNT_TYPE = "type";
    private static final String USER_TRANSACTIONS = "transactions";

    private static final String TRANS_ID = "_tid";
    private static final String TRANS_DESC = "desc";
    private static final String TRANS_AMOUNT = "amount";
    private static final String TRANS_TYPE = "type";
    private static final String TRANS_DATE = "date";

    public AccountToJson(Context context, long accountId) throws JSONException {
        this.context = context;
        this.accountId = accountId;
        helper = new AccountDBAdapter(context);
        accountDetail = helper.getAccountById(accountId);
        transactionDetailList = helper.getTransInfo(accountId, false);
        accountObj = new JSONObject();

        writeObject();
    }

    private void writeObject() throws JSONException {
        accountObj.put(USER_ID, (int) accountId);
        accountObj.put(USER_ICON_ID, accountDetail.user_icon_id);
        accountObj.put(USER_BALANCE, accountDetail.user_balance);
        accountObj.put(USER_NAME, accountDetail.user_name);
        accountObj.put(USER_CREATED, accountDetail.user_created);
        accountObj.put(USER_ACCOUNT_TYPE, accountDetail.user_account_type);

        JSONArray transactionObjects = new JSONArray();
        for (TransactionDetail transactionDetail: transactionDetailList) {
            JSONObject transactionObject = new JSONObject();
            transactionObject.put(TRANS_ID, transactionDetail.transId);
            transactionObject.put(TRANS_AMOUNT, transactionDetail.transAmount);
            transactionObject.put(TRANS_DESC, transactionDetail.transDesc);
            transactionObject.put(TRANS_DATE, transactionDetail.transDate);
            transactionObject.put(TRANS_TYPE, transactionDetail.transType);

            transactionObjects.put(transactionObject);
        }
        accountObj.put(USER_TRANSACTIONS, transactionObjects);
    }

    public String getJsonString() throws JSONException {
        StringWriter out = new StringWriter();
        jsonString = accountObj.toString(4);

        return jsonString;
    }
}
