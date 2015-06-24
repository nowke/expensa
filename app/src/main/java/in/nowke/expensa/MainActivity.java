package in.nowke.expensa;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.AccountListAdapter;
import in.nowke.expensa.classes.AccountDetail;
import in.nowke.expensa.classes.ActionCallback;
import in.nowke.expensa.classes.ClickListener;
import in.nowke.expensa.classes.DividerItemDecoration;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    public RecyclerView mAccountList;
    public static AccountListAdapter adapter;
    private FloatingActionButton fabAddAccount;

    private ActionMode mActionMode;
    private int selectedItem;
    private int statusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstTime()) {
            setContentView(R.layout.activity_intro);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStatusBarColor();
            }
        }
        else {
            showMainWindow();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mActionMode != null) {
            mActionMode.finish();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Show activity_main
     */
    private void showMainWindow() {
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clearStatusBarColor();
        }

        // ACTION BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupAppBar(mToolbar);

        // NAV DRAWER
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // ACCOUNT LIST RECYCLERVIEW
        mAccountList = (RecyclerView) findViewById(R.id.accountListRecycler);
        adapter = new AccountListAdapter(this, getData());
        mAccountList.setAdapter(adapter);
        mAccountList.addItemDecoration(new DividerItemDecoration(this, null));
        mAccountList.setLayoutManager(new LinearLayoutManager(this));
        mAccountList.addOnItemTouchListener(new RecyclerTouchListener(this, mAccountList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mActionMode != null) {
                    mActionMode.finish();
                    return;
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                if (mActionMode != null) {
                    return;
                }
                mActionModeCallback.setClickedView(view);
                selectedItem = position;
                mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);

                view.setSelected(true);
            }
        }));

        // FAB
        fabAddAccount = (FloatingActionButton) findViewById(R.id.fab_add_account);
        fabAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddAccountActivity.class));
            }
        });

    }

    /**
     * Sets up Toolbar
     * @param mToolbar
     */
    private void setupAppBar(Toolbar mToolbar) {

        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * Clears Status Bar Color if any from Intro Window
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void clearStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.transparent));
    }

    /**
     * Sets Status Bar Color same as Primary Color (Only Lollipop & above)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
    }


    /**
     * Triggered when 'Skip' Button is pressed
     * Skips the Login (activity_intro.xml) and Redirects to Main Screen (activity_main.xml)
     * @param view
     */
    public void onSkip(View view) {
        showMainWindow();
    }

    /**
     * Inflates Navigation Drawer
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /**
     * Checks that application runs first time and write flag at SharedPreferences
     *
     * @return true if 1st time
     */
    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }

    public List<AccountDetail> getData() {
        AccountDBAdapter helper = new AccountDBAdapter(this);
        return helper.getAccountInfo();
    }

    private ActionCallback mActionModeCallback = new ActionCallback() {

        public View mClickedView;

        public void setClickedView(View view) {
            mClickedView = view;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getWindow().getStatusBarColor();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //set your gray color
                getWindow().setStatusBarColor(0xFF555555);
            }
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_account_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_remover) {
                TextView userIdText = (TextView) mClickedView.findViewById(R.id.userId);
                int uid = Integer.parseInt(userIdText.getText().toString());
                AccountDBAdapter helper = new AccountDBAdapter(getApplicationContext());
                helper.removeAccount(uid);
                adapter.remove(selectedItem);
                mode.finish();
                return true;

            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(statusBarColor);
            }
            mActionMode = null;
            selectedItem = -1;
            mClickedView.setSelected(false);
        }
    };
}

/**

 To Fix:
    1. Landscape Toolbar vertical alignment
    2. Add Account Choose Avatar - Reset happens when grid is out of focus
    3. Refactor set status bar color

 */