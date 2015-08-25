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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.telly.mrvector.MrVector;

import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.TransactionListAdapter;
import in.nowke.expensa.classes.AvatarIcons;
import in.nowke.expensa.classes.ClickListener;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.RecyclerTouchListener;
import in.nowke.expensa.entity.TransactionDetail;
import in.nowke.expensa.fragments.HomeFragment;

public class AccountDetailActivity extends AppCompatActivity {
    ;

    public static TransactionListAdapter adapter;

    private RecyclerView mTransactionList;

    private FloatingActionButton fabAddTransaction;
    private FloatingActionButton fabEditAccount;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView detailImage;

    private AvatarIcons avatarIcons;
    private Drawable drawable;

    String userID;
    int listPosition;
    int iconId;
    String userName;

    private static final int REQUEST_CODE_EDIT_ACCOUNT = 1;
    private static final int REQUEST_CODE_EDIT_TRANSACTION = 2;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_ACCOUNT:

                    String newUserName = data.getStringExtra("new_user_name");
                    int newIconId = data.getIntExtra("new_icon_id", -1);
                    userName = newUserName;
                    iconId = newIconId;

                    // UPDATE VIEWS
                    collapsingToolbar.setTitle(newUserName);
                    int newBackgroundColor = avatarIcons.getBackgroundColor(newIconId);
                    if (newIconId < 16) {
                        drawable = MrVector.inflate(getResources(), avatarIcons.getAvatarIcon(newIconId));
                        detailImage.setImageDrawable(drawable);
                        detailImage.setBackgroundColor(getResources().getColor(newBackgroundColor));
                    } else {
                        detailImage.setImageDrawable(null);
                        detailImage.setBackgroundColor(getResources().getColor(newBackgroundColor));
                    }
                    collapsingToolbar.setBackgroundColor(getResources().getColor(newBackgroundColor));
                    collapsingToolbar.setContentScrimColor(getResources().getColor(newBackgroundColor));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setStatusBarColor(avatarIcons.getBackgroundColorDark(newIconId));
                    }

                    if (!avatarIcons.isTextColorLight(newIconId)) {
                        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryText));
                        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

                        // This is temporary FIX ASAP using themes
                        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    } else {
                        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
                        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

                        // This is temporary FIX ASAP using themes
                        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        getSupportActionBar().setHomeAsUpIndicator(upArrow);
                    }
                    break;

                case REQUEST_CODE_EDIT_TRANSACTION:

                    String newTransTitle = data.getStringExtra("new_trans_title");
                    Double newTransAmount = data.getDoubleExtra("new_trans_amount", 0);
                    int newTransType = data.getIntExtra("new_trans_type", 0);
                    String newTransDate = data.getStringExtra("new_trans_date");
                    Double newUserBalance = data.getDoubleExtra("new_user_balance", 0);
                    int transListPos = data.getIntExtra("trans_list_pos", -1);

                    adapter.editTransaction(transListPos, newTransTitle, newTransAmount, newTransType, newTransDate, newUserBalance);
                    HomeFragment.adapter.changeAccountAmount(listPosition, newUserBalance);
                    break;

            }
        }
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AccountDBAdapter helper = new AccountDBAdapter(this);
        userName = helper.getNameById(Integer.parseInt(userID));
        iconId = helper.getIconById(Integer.parseInt(userID));

        collapsingToolbar.setTitle(userName);

        detailImage = (ImageView) findViewById(R.id.detailImage);

        // Avatar
        avatarIcons = new AvatarIcons(this);
        int backgroundColor = avatarIcons.getBackgroundColor(iconId);
        if (iconId < 16) {
            drawable = MrVector.inflate(getResources(), avatarIcons.getAvatarIcon(iconId));
            detailImage.setImageDrawable(drawable);
        } else {
            detailImage.setImageDrawable(null);
            detailImage.setBackgroundColor(getResources().getColor(backgroundColor));
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
        // Transaction List
        adapter = new TransactionListAdapter(this, getData());
        mTransactionList = (RecyclerView) findViewById(R.id.transactionListRecycler);
        mTransactionList.setAdapter(adapter);
        mTransactionList.setLayoutManager(new LinearLayoutManager(this));
        mTransactionList.addOnItemTouchListener(new RecyclerTouchListener(this, mTransactionList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {

                TextView transIdTextView = (TextView) view.findViewById(R.id.transId);
                final long transId = Long.parseLong(transIdTextView.getText().toString());
                final int userId = Integer.parseInt(userID);
//                Message.message(AccountDetailActivity.this, String.valueOf(userId));

                new MaterialDialog.Builder(AccountDetailActivity.this)
                        .title(R.string.transaction_edit_dialog_title)
                        .items(R.array.transaction_edit_dialog_list)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                               switch(which) {
                                   case 0:
                                       // Edit
                                       Intent intent = new Intent(AccountDetailActivity.this, AddTransactionActivity.class);
                                       intent.putExtra("TRANS_USER_ID", userID);
                                       intent.putExtra("USER_LIST_POSITION", listPosition);
                                       intent.putExtra("Trans_id", transId);
                                       intent.putExtra("is_edit_trans", true);
                                       intent.putExtra("trans_list_pos", position);
                                       startActivityForResult(intent, REQUEST_CODE_EDIT_TRANSACTION);
                                       break;
                                   case 1:
                                       // Delete
                                       AccountDBAdapter helper = new AccountDBAdapter(AccountDetailActivity.this);
                                       Double newBalance = helper.deleteTransaction(transId, userId);

                                       // Update Balance
                                       adapter.changeBalance(newBalance);
                                       HomeFragment.adapter.changeAccountAmount(listPosition, newBalance);

                                       // Remove item
                                       adapter.remove(position);
                                       break;
                               }
                            }
                        })
                        .show();
            }
        }));


        // FAB
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

        fabEditAccount = (FloatingActionButton) findViewById(R.id.fabEditAccount);
        fabEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddAccountActivity.class);
                intent.putExtra("User_id", Integer.parseInt(userID));
                intent.putExtra("User_icon_id", iconId);
                intent.putExtra("LIST_POSITION", listPosition);
                intent.putExtra("User_name", userName);
                intent.putExtra("is_from_detail_activity", true);
                startActivityForResult(intent, REQUEST_CODE_EDIT_ACCOUNT);
            }
        });
    }
}
