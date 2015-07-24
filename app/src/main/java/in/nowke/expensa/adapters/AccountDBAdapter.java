package in.nowke.expensa.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.entity.TransactionDetail;
import in.nowke.expensa.classes.Message;

/**
 * Created by nav on 1/5/15.
 */
public class AccountDBAdapter {

    AccountDBHelper helper;

    public AccountDBAdapter(Context context) {
        helper = new AccountDBHelper(context);
    }

    public long addAccount(AccountDetail accountDetail) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_NAME, accountDetail.user_name);
        contentValues.put(AccountDBHelper.USER_ICON_ID, accountDetail.user_icon_id);
        contentValues.put(AccountDBHelper.USER_CREATED, accountDetail.user_created);
        contentValues.put(AccountDBHelper.USER_ACCOUNT_TYPE, AccountDBHelper.ACCOUNT_DEFAULT);
        contentValues.put(AccountDBHelper.USER_BALANCE, 0);

        long id = db.insert(AccountDBHelper.TABLE_ACCOUNT, null, contentValues);

        return id;
    }

    public long addTransaction (TransactionDetail transactionDetail) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_ID, transactionDetail.userId);
        contentValues.put(AccountDBHelper.TRANS_AMOUNT, transactionDetail.transAmount);
        contentValues.put(AccountDBHelper.TRANS_TYPE, transactionDetail.transType);
        contentValues.put(AccountDBHelper.TRANS_DESC, transactionDetail.transDesc);
        contentValues.put(AccountDBHelper.TRANS_DATE, transactionDetail.transDate);

        long id = db.insert(AccountDBHelper.TABLE_TRANS, null, contentValues);

        Double newBalance = calcBalance(transactionDetail.userId);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(AccountDBHelper.USER_BALANCE, newBalance);
        db.update(AccountDBHelper.TABLE_ACCOUNT, contentValues1, AccountDBHelper.USER_ID + "=" + transactionDetail.userId, null);

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

    public List<AccountDetail> getAccountInfo(int accountType) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {
                AccountDBHelper.USER_ID,
                AccountDBHelper.USER_NAME,
                AccountDBHelper.USER_BALANCE,
                AccountDBHelper.USER_ICON_ID,
                AccountDBHelper.USER_ACCOUNT_TYPE,
                AccountDBHelper.USER_CREATED
        };
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, AccountDBHelper.USER_ACCOUNT_TYPE + "=" + accountType, null, null, null,
                AccountDBHelper.USER_CREATED + " DESC");

        List<AccountDetail> accInfo = new ArrayList<>();

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.USER_ID);
            int index2 = cursor.getColumnIndex(AccountDBHelper.USER_NAME);
            int index3 = cursor.getColumnIndex(AccountDBHelper.USER_BALANCE);
            int index4 = cursor.getColumnIndex(AccountDBHelper.USER_ICON_ID);
            int index5 = cursor.getColumnIndex(AccountDBHelper.USER_ACCOUNT_TYPE);
            int index6 = cursor.getColumnIndex(AccountDBHelper.USER_CREATED);

            int userId = cursor.getInt(index1);
            String userName = cursor.getString(index2);
            double userBalance = cursor.getDouble(index3);
            int userIconId = cursor.getInt(index4);
            int userAccountType = cursor.getInt(index5);
            String userCreatedDate = cursor.getString(index6);

            AccountDetail accDetail = new AccountDetail();
            accDetail.user_id = userId;
            accDetail.user_name = userName;
            accDetail.user_balance = userBalance;
            accDetail.user_icon_id = userIconId;
            accDetail.user_created = userCreatedDate;
            accDetail.user_account_type = userAccountType;

            accInfo.add(accDetail);
        }

        return accInfo;
    }

    public List<TransactionDetail> getTransInfo(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.TRANS_DESC, AccountDBHelper.TRANS_AMOUNT, AccountDBHelper.TRANS_TYPE, AccountDBHelper.TRANS_DATE, AccountDBHelper.TRANS_ID};
        Cursor cursor = db.query(AccountDBHelper.TABLE_TRANS, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        List<TransactionDetail> transInfo = new ArrayList<>();

        Double userBalance = getBalanceById(userId);
        String userCreated = getCreatedDateById(userId);
        TransactionDetail userHeader = new TransactionDetail();
        userHeader.userBalance = userBalance;
        userHeader.userCreated = userCreated;

        transInfo.add(userHeader);

        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(AccountDBHelper.TRANS_DESC);
            int index2 = cursor.getColumnIndex(AccountDBHelper.TRANS_AMOUNT);
            int index3 = cursor.getColumnIndex(AccountDBHelper.TRANS_TYPE);
            int index4 = cursor.getColumnIndex(AccountDBHelper.TRANS_DATE);
            int index5 = cursor.getColumnIndex(AccountDBHelper.TRANS_ID);

            String tDesc = cursor.getString(index1);
            Double tAmount = cursor.getDouble(index2);
            int tType = cursor.getInt(index3);
            String tDate = cursor.getString(index4);
            int tId = cursor.getInt(index5);

            TransactionDetail transDetail = new TransactionDetail();
            transDetail.transType = tType;
            transDetail.transDesc = tDesc;
            transDetail.transAmount = tAmount;
            transDetail.transDate = tDate;
            transDetail.transId = tId;

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

    public Double calcTotalBalance() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_BALANCE};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, null, null, null, null, null);

        Double totalBalance = 0.0;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(AccountDBHelper.USER_BALANCE);
            Double userBalance = cursor.getDouble(index);
            totalBalance += userBalance;
        }
        return totalBalance;
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

    public int getIconById(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_ICON_ID};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        int iconId = -1;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(AccountDBHelper.USER_ICON_ID);
            iconId = cursor.getInt(index);
        }
        return iconId;
    }

    public String getCreatedDateById(int userId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String columns[] = {AccountDBHelper.USER_ID, AccountDBHelper.USER_CREATED};
        Cursor cursor = db.query(AccountDBHelper.TABLE_ACCOUNT, columns, AccountDBHelper.USER_ID + "=" + userId, null, null, null, null);

        String uCreated = null;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(AccountDBHelper.USER_CREATED);
            uCreated = cursor.getString(index);
        }
        return uCreated;
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

    public void trashAccount(int userId) {
        changeUserAccountType(userId, AccountDBHelper.ACCOUNT_TRASH);
    }

    public void archiveAccount(int userId) {
       changeUserAccountType(userId, AccountDBHelper.ACCOUNT_ARCHIVED);
    }

    public void unarchiveAccount(int userId) {
        changeUserAccountType(userId, AccountDBHelper.ACCOUNT_DEFAULT);
    }

    public void restoreAccount(int userId) {
        changeUserAccountType(userId, AccountDBHelper.ACCOUNT_DEFAULT);
    }

    public void changeUserAccountType(int userId, int accountType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_ACCOUNT_TYPE, accountType);
        String[] whereArgs = {String.valueOf(userId)};
        int count = db.update(AccountDBHelper.TABLE_ACCOUNT, contentValues, AccountDBHelper.USER_ID + " =? ", whereArgs );
    }

    public void editUser(int userId, String newUserName, int newUserIconId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AccountDBHelper.USER_NAME, newUserName);
        contentValues.put(AccountDBHelper.USER_ICON_ID, newUserIconId);
        String[] whereArgs = {String.valueOf(userId)};
        db.update(AccountDBHelper.TABLE_ACCOUNT, contentValues, AccountDBHelper.USER_ID + " =? ", whereArgs);
    }

    static class AccountDBHelper extends SQLiteOpenHelper {

        // ACCOUNT TYPES
        private static final int ACCOUNT_DEFAULT = 1;
        private static final int ACCOUNT_ARCHIVED = 2;
        private static final int ACCOUNT_TRASH = 3;

        // TRANSACTION TYPES
        private static final int TRANS_CREDIT = 0;
        private static final int TRANS_DEBIT = 1;

        // DATABASE DETAILS
        // ----------------------

        // DATABASES
        private static final String DATABASE_NAME = "AccountDb";
        private static final int DATABASE_VERSION = 6;

        // TABLES
        private static final String TABLE_ACCOUNT = "tableAccount";
        private static final String TABLE_TRANS = "tableTrans";

        // COLUMNS
        private static final String USER_ID = "_id";
        private static final String USER_NAME = "userName";
        private static final String USER_BALANCE = "userBalance";
        private static final String USER_ICON_ID = "userIconId";
        private static final String USER_CREATED = "userCreated";
        private static final String USER_ACCOUNT_TYPE = "userAccountType";

        private static final String TRANS_ID = "_tid";
        private static final String TRANS_AMOUNT = "transAmount";
        private static final String TRANS_TYPE = "transType";
        private static final String TRANS_DESC = "transDesc";
        private static final String TRANS_DATE = "transDate";

        // CREATE DB STATEMENTS
        private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + " (" +
                                                           USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                           USER_NAME + " VARCHAR(255), " +
                                                           USER_ICON_ID + " INTEGER, " +
                                                           USER_ACCOUNT_TYPE + " INTEGER, " +
                                                           USER_CREATED + " VARCHAR(30), "+
                                                           USER_BALANCE + " DOUBLE);";

        private static final String CREATE_TRANS_TABLE = "CREATE TABLE " + TABLE_TRANS + " (" +
                                                         TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                         USER_ID + " INTEGER, " +
                                                         TRANS_AMOUNT + " DOUBLE, " +
                                                         TRANS_DESC + " VARCHAR(255), " +
                                                         TRANS_TYPE + " INTEGER, " +
                                                         TRANS_DATE + " VARCHAR(30), " +
                                                         "FOREIGN KEY (" + USER_ID + ") REFERENCES " + TABLE_ACCOUNT + " (" + USER_ID + "));";

        // DROP TABLE
        private static final String DROP_TABLE_ACC = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT;
        private static final String DROP_TABLE_TRANSC = "DROP TABLE IF EXISTS " + TABLE_TRANS;

        private Context context;

        AccountDBHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_ACCOUNT_TABLE);
                db.execSQL(CREATE_TRANS_TABLE);
            }
            catch (SQLException e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
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
