package in.nowke.expensa.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.nowke.expensa.BaseActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.entity.TransactionDetail;
import in.nowke.expensa.entity.MyDateFormat;
import in.nowke.expensa.fragments.HomeFragment;

public class AddTransactionActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    TextView dateTextView;
    Toolbar mToolbar;

    TextView dateTextDate;
    TextView dateTextMonth;
    TextView dateTextYear;

    TextInputLayout transTitle;
    TextInputLayout transAmount;
    Button saveTrans;
    RadioGroup transType;

    MyDateFormat date;
    MyDateFormat transactionDate;

    String userId;
    long transId;
    int listPosition;
    int transListPos;

    private boolean isEdit = false;
    TransactionDetail transactionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
        setContentView(R.layout.activity_add_transaction);
        clearStatusBarColor();

        // FROM INTENT
        userId = getIntent().getStringExtra("TRANS_USER_ID");
        listPosition = getIntent().getIntExtra("USER_LIST_POSITION", -1);
        if (getIntent().hasExtra("is_edit_trans")) {
            isEdit = true;
            transId = getIntent().getLongExtra("Trans_id", -1);
            transListPos = getIntent().getIntExtra("trans_list_pos", -1);
        }

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        transTitle = (TextInputLayout) findViewById(R.id.textinputTransTitle);
        transAmount = (TextInputLayout) findViewById(R.id.textinputTransAmount);
        transType = (RadioGroup) findViewById(R.id.radioTransType);
        saveTrans = (Button) findViewById(R.id.saveTransaction);
        dateTextDate = (TextView) findViewById(R.id.dateTextDate);
        dateTextMonth = (TextView) findViewById(R.id.dateTextMonth);
        dateTextYear = (TextView) findViewById(R.id.dateTextYear);

        // ACTION BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupAppBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        if (isEdit) {
            // Get transaction Detail from userId
            AccountDBAdapter helper = new AccountDBAdapter(this);
             transactionDetail = helper.getTransactionById(transId);

            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_edit_transaction));
            transTitle.getEditText().setText(transactionDetail.transDesc);
            transAmount.getEditText().setText(transactionDetail.transAmount.toString());
            saveTrans.setText("Update");
            saveTrans.setEnabled(true);
            int tType = transactionDetail.transType;
            if (tType == 1) {
                // Debit
                transType.check(R.id.radioTransDebit);
            }
        }

        // TEXT WATCHERS
        transTitle.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                watchForButtonEnableDisable();
            }
        });
        transAmount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                watchForButtonEnableDisable();
            }
        });
        transAmount.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Utilities.hideKeyboard(getApplicationContext(), transAmount.getEditText());
                    return true;
                }
                return false;
            }
        });

        // DATE
        date = new MyDateFormat(Utilities.getCurrentDay(), Utilities.getCurrentMonth(), Utilities.getCurrentYear());
        dateTextView.setText(isEdit ? transactionDetail.transDate : date.getDateStr());
        if (isEdit) {
            transactionDate = MyDateFormat.getDateFromStr(transactionDetail.transDate);
        }
        dateTextDate.setText(isEdit ? String.format("%02d", transactionDate.getDay()) : String.format("%02d", date.getDay()));
        dateTextMonth.setText(isEdit ? transactionDate.getMonth() : date.getMonth());
        dateTextYear.setText(isEdit ? String.valueOf(transactionDate.getYear()) : String.valueOf(date.getYear()));
    }

    private void watchForButtonEnableDisable() {
        String tTitle = transTitle.getEditText().getText().toString();
        String tAmount = transAmount.getEditText().getText().toString();
        if (tTitle.equals("") || tAmount.equals("") || tAmount.equals(".") || date == null) {
            saveTrans.setEnabled(false);
        }
        else {
            saveTrans.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
    }

    private void addTransaction(String transTitleStr, Double transAmountVal, int transTypeVal, String dateStr) {

        AccountDBAdapter helper;
        helper = new AccountDBAdapter(this);

        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.transType = transTypeVal;
        transactionDetail.transAmount = transAmountVal;
        transactionDetail.transDesc = transTitleStr;
        transactionDetail.transDate = dateStr;
        transactionDetail.userId = Integer.parseInt(userId);

        long id = helper.addTransaction(transactionDetail);
        transactionDetail.transId = id;
        AccountDetailActivity.adapter.add(transactionDetail, AccountDetailActivity.adapter.getItemCount());
        if (transTypeVal == 0) {
            AccountDetailActivity.adapter.updateBalance(transAmountVal);
            HomeFragment.adapter.updateAccountAmount(listPosition, transAmountVal);
        }
        else {
            AccountDetailActivity.adapter.updateBalance(-transAmountVal);
            HomeFragment.adapter.updateAccountAmount(listPosition, -transAmountVal);
        }
        finish();

        super.addAccount(transactionDetail.userId);

        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);


    }


    private void updateTransaction(String transTitleStr, Double aDouble, int transTypeVal, String transDateStr) {
        AccountDBAdapter helper = new AccountDBAdapter(this);
        Double newBalance = helper.editTransaction(transId, Integer.parseInt(userId),transTitleStr, aDouble, transTypeVal, transDateStr);

        Intent returnIntent =  new Intent();
        returnIntent.putExtra("new_trans_title", transTitleStr);
        returnIntent.putExtra("new_trans_amount", aDouble);
        returnIntent.putExtra("new_trans_type", transTypeVal);
        returnIntent.putExtra("new_trans_date", transDateStr);
        returnIntent.putExtra("new_user_balance", newBalance);
        returnIntent.putExtra("trans_list_pos", transListPos);
        setResult(RESULT_OK, returnIntent);

        finish();

        super.addAccount(Integer.parseInt(userId));
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);


    }

    public void pickDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd;
        if (isEdit) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    transactionDate.getYear(),
                    transactionDate.getMonthInt(),
                    transactionDate.getDay()
            );
        }
        else {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.show(getFragmentManager(), "DatePicker Dialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = new MyDateFormat(dayOfMonth, monthOfYear + 1, year);
        dateTextView.setText(date.getDateStr());
        dateTextDate.setText(String.format("%02d", date.getDay()));
        dateTextMonth.setText(date.getMonth());
        dateTextYear.setText(String.valueOf(date.getYear()));
    }

    /**
     * Sets up Toolbar
     *
     * @param mToolbar
     */
    private void setupAppBar(Toolbar mToolbar) {

        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public void saveTransaction(View view) {
        String transTitleStr = transTitle.getEditText().getText().toString();
        String transAmountVal = transAmount.getEditText().getText().toString();
        String transDateStr = dateTextView.getText().toString();
        int transTypeVal = 0;
        switch (transType.getCheckedRadioButtonId()) {
            case R.id.radioTransCredit:
                transTypeVal = 0;
                break;
            case R.id.radioTransDebit:
                transTypeVal = 1;
                break;
        }
        if (date != null && !transTitleStr.isEmpty() && !transAmountVal.isEmpty() && !transAmountVal.equals(".")) {
            if (isEdit) {
                updateTransaction(transTitleStr, Double.valueOf(transAmountVal), transTypeVal, transDateStr);
            }
            else {
                addTransaction(transTitleStr, Double.valueOf(transAmountVal), transTypeVal, transDateStr);
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void clearStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { return;
        }
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
}
