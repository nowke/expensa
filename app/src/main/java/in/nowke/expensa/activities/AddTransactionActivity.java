package in.nowke.expensa.activities;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.entity.TransactionDetail;
import in.nowke.expensa.entity.MyDateFormat;
import in.nowke.expensa.fragments.HomeFragment;

public class AddTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView dateTextView;
    Toolbar mToolbar;

    EditText transTitle;
    EditText transAmount;

    RadioGroup transType;

    MyDateFormat date;

    String userId;
    int listPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
        setContentView(R.layout.activity_add_transaction);

        userId = getIntent().getStringExtra("TRANS_USER_ID");
        listPosition = getIntent().getIntExtra("USER_LIST_POSITION", -1);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        transTitle = (EditText) findViewById(R.id.transTitle);
        transAmount = (EditText) findViewById(R.id.transAmount);
        transType = (RadioGroup) findViewById(R.id.radioTransType);

        // ACTION BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupAppBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_transaction) {
            String transTitleStr = transTitle.getText().toString();
            String transAmountVal = transAmount.getText().toString();
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
                addTransaction(transTitleStr, Double.valueOf(transAmountVal), transTypeVal, date.getDateStr());
            }
            return true;
        }

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
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
    }

    public void pickDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "DatePicker Dialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = new MyDateFormat(dayOfMonth, monthOfYear + 1, year);
        dateTextView.setText(date.getDateStr());
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
}
