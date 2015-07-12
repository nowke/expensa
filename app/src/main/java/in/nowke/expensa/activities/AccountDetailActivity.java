package in.nowke.expensa.activities;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.TransactionListAdapter;
import in.nowke.expensa.entity.TransactionDetail;

public class AccountDetailActivity extends AppCompatActivity {;

    private TransactionListAdapter adapter;

    private RecyclerView mTransactionList;

    private FloatingActionButton fabAddTransaction;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        setContentView(R.layout.activity_account_detail);
        userID = getIntent().getStringExtra("USER_ID");

        initToolbar();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.pull_left_in, R.anim.push_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_left_in, R.anim.push_out_right);
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Account Detail");

//        collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
        fixApi21ToolBarBug(toolbar);

    }

    private void fixApi21ToolBarBug(Toolbar toolbar){
        if (Build.VERSION.SDK_INT!=21) return; //only on api 21
        final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        final int result = (resourceId>0) ? getResources().getDimensionPixelSize(resourceId) : 0;
        final CollapsingToolbarLayout.LayoutParams params =
                (CollapsingToolbarLayout.LayoutParams)toolbar.getLayoutParams();
        params.topMargin -= result;
        toolbar.setLayoutParams(params);
    }

    public List<TransactionDetail> getData() {
        AccountDBAdapter helper = new AccountDBAdapter(this);
        return helper.getTransInfo(Integer.parseInt(userID));
    }

    private void initViews() {


        adapter = new TransactionListAdapter(this, getData());
        mTransactionList = (RecyclerView) findViewById(R.id.transactionListRecycler);
        mTransactionList.setAdapter(adapter);
        mTransactionList.setLayoutManager(new LinearLayoutManager(this));

        fabAddTransaction = (FloatingActionButton) findViewById(R.id.fabAddTransaction);
        fabAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
                intent.putExtra("TRANS_USER_ID", userID);
                startActivity(intent);
            }
        });
    }
}
