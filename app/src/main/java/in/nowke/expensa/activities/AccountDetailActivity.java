package in.nowke.expensa.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.telly.mrvector.MrVector;

import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.TransactionListAdapter;
import in.nowke.expensa.classes.AvatarIcons;
import in.nowke.expensa.entity.TransactionDetail;

public class AccountDetailActivity extends AppCompatActivity {;

    public static TransactionListAdapter adapter;

    private RecyclerView mTransactionList;

    private FloatingActionButton fabAddTransaction;

    String userID;
    int listPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_detail);
        userID = getIntent().getStringExtra("USER_ID");
        listPosition = getIntent().getIntExtra("LIST_POSITION", -1);

        initToolbar();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AccountDBAdapter helper = new AccountDBAdapter(this);
        String userName = helper.getNameById(Integer.parseInt(userID));
        int iconId = helper.getIconById(Integer.parseInt(userID));

        collapsingToolbar.setTitle(userName);

        ImageView imageView = (ImageView) findViewById(R.id.detailImage);

        // Avatar
        AvatarIcons avatarIcons = new AvatarIcons(this);
        int backgroundColor = avatarIcons.getBackgroundColor(iconId);
        if (iconId < 16) {
            Drawable drawable = MrVector.inflate(getResources(), avatarIcons.getAvatarIcon(iconId));
            imageView.setImageDrawable(drawable);

        }
        else {
            imageView.setBackgroundColor(getResources().getColor(backgroundColor));
        }

        collapsingToolbar.setBackgroundColor(getResources().getColor(backgroundColor));
        collapsingToolbar.setContentScrimColor(getResources().getColor(backgroundColor));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(avatarIcons.getBackgroundColorDark(iconId));
        }

        if (!avatarIcons.isTextColorLight(iconId)) {
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryText));
            collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

            // This is temporary FIX ASAP using themes
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int statusBarColor) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(statusBarColor));
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
                intent.putExtra("USER_LIST_POSITION", listPosition);
                startActivity(intent);
            }
        });
    }
}
