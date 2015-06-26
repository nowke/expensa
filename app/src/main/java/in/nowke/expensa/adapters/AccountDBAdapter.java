package in.nowke.expensa.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.nowke.expensa.classes.AccountDetail;
import in.nowke.expensa.classes.AccountDetailInfo;
import in.nowke.expensa.classes.Message;

/**
 * Created by nav on 1/5/15.
 */
public class AccountDBAdapter {

    AccountDBHelper helper;

    public AccountDBAdapter(Context context) {
        helper = new AccountDBHelper(context);
    }

    public long addAccount(String accName, int avatarIconId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_NAME, accName);
        contentValues.put(AccountDBHelper.USER_ICON_ID, avatarIconId);
        contentValues.put(AccountDBHelper.USER_BALANCE, 0);

        long id = db.insert(AccountDBHelper.TABLE_ACCOUNT, null, contentValues);

        return id;
    }

    public long addTransaction(Double transAmount, int transType, int userId, String transDesc, Double personBalance) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_ID, userId);
        contentValues.put(AccountDBHelper.TRANS_AMOUNT, transAmount);
        contentValues.put(AccountDBHelper.TRANS_TYPE, transType);
        contentValues.put(AccountDBHelper.TRANS_DESC, transDesc);

        long id = db.insert(AccountDBHelper.TABLE_TRANS, null, contentValues);

        Double newBalance = calcBalance(userId);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(AccountDBHelper.USER_BALANCE, newBalance);
        db.update(AccountDBHelper.TABLE_ACCOUNT, contentValues1, AccountDBHelper.USER_ID + "=" + userId, null);

        return id;
    }


    public String getAllData() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_NAME, AccountDBHelper.USER_BALANCE};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.USER_ID);
            int index2 = cursor.getColumnIndex(AccountDBHelper.USER_NAME);
            int index3 = cursor.getColumnIndex(AccountDBHelper.USER_BALANCE);

            int cid = cursor.getInt(index1);
            String uname = cursor.getString(index2);
            double ubalance = cursor.getDouble(index3);

            buffer.append(cid + ". " + uname + "-" + ubalance + "\n");
        }

        return buffer.toString();
    }

    public String getTransData() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.TRANS_ID, AccountDBHelper.USER_ID, AccountDBHelper.TRANS_AMOUNT, AccountDBHelper.TRANS_TYPE, AccountDBHelper.TRANS_DESC};
        Cursor cursor = db.query(AccountDBHelper.TABLE_TRANS, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.TRANS_ID);
            int index2 = cursor.getColumnIndex(AccountDBHelper.USER_ID);
            int index3 = cursor.getColumnIndex(AccountDBHelper.TRANS_AMOUNT);
            int index4 = cursor.getColumnIndex(AccountDBHelper.TRANS_TYPE);
            int index5 = cursor.getColumnIndex(AccountDBHelper.TRANS_DESC);

            int tid = cursor.getInt(index1);
            int uid = cursor.getInt(index2);
            Double tAmt = cursor.getDouble(index3);
            int tType = cursor.getInt(index4);
            String transDesc = cursor.getString(index5);

            buffer.append(tid + "," + uid + ":" + tAmt + ", " + tType + "= " + transDesc + "\n" );
        }
        return buffer.toString();
    }

    public List<AccountDetail> getAccountInfo() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_NAME, AccountDBHelper.USER_BALANCE, AccountDBHelper.USER_ICON_ID};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, null, null, null, null, null);

        List<AccountDetail> accInfo = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.USER_ID);
            int index2 = cursor.getColumnIndex(AccountDBHelper.USER_NAME);
            int index3 = cursor.getColumnIndex(AccountDBHelper.USER_BALANCE);
            int index4 = cursor.getColumnIndex(AccountDBHelper.USER_ICON_ID);

            int cid = cursor.getInt(index1);
            String uname = cursor.getString(index2);
            double ubalance = cursor.getDouble(index3);
            int icId = cursor.getInt(index4);

            AccountDetail accDetail = new AccountDetail();
            accDetail.user_id = cid;
            accDetail.user_name = uname;
            accDetail.user_balance = ubalance;
            accDetail.user_icon_id = icId;

            accInfo.add(accDetail);
        }

        return accInfo;
    }

    public List<AccountDetailInfo> getTransInfo(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.TRANS_DESC, AccountDBHelper.TRANS_AMOUNT, AccountDBHelper.TRANS_TYPE};
        Cursor cursor = db.query(AccountDBHelper.TABLE_TRANS, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        List<AccountDetailInfo> transInfo = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.TRANS_DESC);
            int index2 = cursor.getColumnIndex(AccountDBHelper.TRANS_AMOUNT);
            int index3 = cursor.getColumnIndex(AccountDBHelper.TRANS_TYPE);

            String tDesc = cursor.getString(index1);
            Double tAmount = cursor.getDouble(index2);
            int tType = cursor.getInt(index3);

            AccountDetailInfo transDetail = new AccountDetailInfo();
            transDetail.transType = tType;
            transDetail.transDesc = tDesc;
            transDetail.transAmount = tAmount;

            transInfo.add(transDetail);
        }

        return transInfo;
    }

    public Double calcBalance(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.TRANS_AMOUNT, AccountDBHelper.TRANS_TYPE};
        Cursor cursor = db.query(AccountDBHelper.TABLE_TRANS, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);
        Double balance = 0.0;

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.TRANS_AMOUNT);
            int index2 = cursor.getColumnIndex(AccountDBHelper.TRANS_TYPE);

            int tType = cursor.getInt(index2);
            Double tAmount = cursor.getDouble(index1);
            if (tType == 0) {
                balance += tAmount;
            }
            else {
                balance -= tAmount;
            }
        }
        return balance;
    }

    public String getNameById(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_NAME};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        String uName = null;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(AccountDBHelper.USER_NAME);
            uName = cursor.getString(index);
        }
        return uName;
    }

    public Double getBalanceById(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_BALANCE};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        Double uName = null;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(AccountDBHelper.USER_BALANCE);
            uName = cursor.getDouble(index);
        }
        return uName;
    }

    public void removeAccount(int userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count1 = db.delete(AccountDBHelper.TABLE_ACCOUNT, AccountDBHelper.USER_ID + "=" + userId, null);
        int count2 = db.delete(AccountDBHelper.TABLE_TRANS, AccountDBHelper.USER_ID + "=" + userId, null);
    }

    static class AccountDBHelper extends SQLiteOpenHelper {

        // DATABASE DETAILS
        // ----------------------

        // DATABASES
        private static final String DATABASE_NAME = "AccountDb";
        private static final int DATABASE_VERSION = 4;

        // TABLES
        private static final String TABLE_ACCOUNT = "tableAccount";
        private static final String TABLE_TRANS = "tableTrans";

        // COLUMNS
        private static final String USER_ID = "_id";
        private static final String USER_NAME = "userName";
        private static final String USER_BALANCE = "userBalance";
        private static final String USER_ICON_ID = "userIconId";

        private static final String TRANS_ID = "_tid";
        private static final String TRANS_AMOUNT = "transAmount";
        private static final String TRANS_TYPE = "transType";
        private static final String TRANS_DESC = "transDesc";

        // CREATE DB STATEMENTS
        private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + " (" +
                                                           USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                           USER_NAME + " VARCHAR(255), " +
                                                           USER_ICON_ID + " INTEGER, " +
                                                           USER_BALANCE + " DOUBLE);";

        private static final String CREATE_TRANS_TABLE = "CREATE TABLE " + TABLE_TRANS + " (" +
                                                         TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                         USER_ID + " INTEGER, " +
                                                         TRANS_AMOUNT + " DOUBLE, " +
                                                         TRANS_DESC + " VARCHAR(255), " +
                                                         TRANS_TYPE + " INTEGER, " +
                                                         "FOREIGN KEY (" + USER_ID + ") REFERENCES " + TABLE_ACCOUNT + " (" + USER_ID + "));";

        // DROP TABLE
        private static final String DROP_TABLE_ACC = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT;
        private static final String DROP_TABLE_TRANSC = "DROP TABLE IF EXISTS " + TABLE_TRANS;

        private Context context;

        AccountDBHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
//            Message.message(context, "Constructor called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_ACCOUNT_TABLE);
                db.execSQL(CREATE_TRANS_TABLE);
//                Message.message(context, "onCreate called");
            }
            catch (SQLException e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
//                Message.message(context, "onUpgrade called");
                db.execSQL(DROP_TABLE_ACC);
                db.execSQL(DROP_TABLE_TRANSC);
                onCreate(db);
            }
            catch (SQLException e) {
                Message.message(context, "" + e);
            }
        }
    }
}