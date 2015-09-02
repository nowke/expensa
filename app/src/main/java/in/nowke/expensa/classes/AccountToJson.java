package in.nowke.expensa.classes;

import android.content.Context;

import in.nowke.expensa.adapters.AccountDBAdapter;

/**
 * Created by nav on 2/9/15.
 * A helper class to convert Account information (including transaction) to JSON string
 */
public class AccountToJson {

    private long accountId;
    private Context context;
    private AccountDBAdapter helper;

    public AccountToJson(Context context, long accountId) {
        this.context = context;
        this.accountId = accountId;
        helper = new AccountDBAdapter(context);

    }
}
