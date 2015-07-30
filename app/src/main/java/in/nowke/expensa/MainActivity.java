package in.nowke.expensa;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.telly.mrvector.MrVector;

import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    HomeFragment homeFragment;
    private static FloatingActionButton fabAddAccount;
    private static NavigationView navigationView;
    private MenuItem mPreviousCheckedItem;
    TextView accountBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isFirstTime()) {
            setContentView(R.layout.activity_intro);
        }
        else {
            showMainWindow();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (homeFragment != null && homeFragment.finishActionMode()) {
            return;
        }
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
            }
            else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Show activity_main
     */
    private void showMainWindow() {
        setContentView(R.layout.activity_main);
        homeFragment = (HomeFragment) getFragmentManager().findFragmentById(R.id.fragmentHome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clearStatusBarColor();
        }

        // ACTION BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupAppBar(mToolbar);

        // NAV DRAWER
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // FAB
        fabAddAccount = (FloatingActionButton) findViewById(R.id.fab_add_account);
        fabAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddAccountActivity.class));
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

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
            actionBar.setTitle(getResources().getString(R.string.title_activity_main_account));
        }
    }

    public static void fabHideShow(int visibility) {
        // 1 - Show, 0 - Hide
        if (visibility == 0) {
            fabAddAccount.hide();
        }
        else {
            fabAddAccount.show();
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
    private void setStatusBarColor(int statusBarColor, boolean clearFlags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) { return; }
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        window.setNavigationBarColor(getResources().getColor(statusBarColor));
        window.setStatusBarColor(getResources().getColor(statusBarColor));
    }


    /**
     * Triggered when 'Skip' Button is pressed
     * Skips the Login (activity_intro.xml) and Redirects to Main Screen (activity_main.xml)
     * @param view
     */
    public void onSkip(View view) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("RanBefore", true);
        editor.apply();
        showMainWindow();
    }

    /**
     * Inflates Navigation Drawer
     * @param navigationView
     */
    private void setupDrawerContent(final NavigationView navigationView) {
        mPreviousCheckedItem = navigationView.getMenu().findItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (mPreviousCheckedItem != null && mPreviousCheckedItem != menuItem && menuItem.isCheckable()) {
                            mPreviousCheckedItem.setChecked(false);
                        }
                        if (menuItem.isCheckable()) {
                            mPreviousCheckedItem = menuItem;
                        }
                        mDrawerLayout.closeDrawers();
                        new Thread() {
                            @Override
                            public void run() {
                                SystemClock.sleep(300);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (menuItem.getItemId()) {
                                            case R.id.nav_home:
                                                homeFragment.setAccountListAdapter(1);
                                                fabHideShow(1);
                                                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main_account));
//                                                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

//                                                MainActivity.this.setStatusBarColor(R.color.colorPrimaryDark, false);

                                                break;
                                            case R.id.nav_archives:
                                                homeFragment.setAccountListAdapter(2);
                                                fabHideShow(0);
                                                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main_archive));
//                                                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorArchive));
//                                                MainActivity.this.setStatusBarColor(R.color.colorArchiveDark, false);

                                                break;
                                            case R.id.nav_trash:
                                                homeFragment.setAccountListAdapter(3);
                                                fabHideShow(0);
                                                getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main_deleted));
//                                                mToolbar.setBackgroundColor(getResources().getColor(R.color.colorDeleted));
//                                                MainActivity.this.setStatusBarColor(R.color.colorDeletedDark, false);

                                                break;
                                            case R.id.nav_credit:
                                                homeFragment.setAccountListAdapter(4);
                                                fabHideShow(0);
                                                getSupportActionBar().setTitle(getResources().getString(R.string.nav_credit));
                                                break;

                                            case R.id.nav_debit:
                                                homeFragment.setAccountListAdapter(5);
                                                fabHideShow(0);
                                                getSupportActionBar().setTitle(getResources().getString(R.string.nav_debit));
                                                break;
                                        }

                                    }
                                });
                            }
                        }.start();
                        return true;
                    }
                });
        ImageView avatarCircle = (ImageView) navigationView.findViewById(R.id.accountAvatar);
        Drawable drawable = MrVector.inflate(getResources(), R.drawable.account_circle);
        avatarCircle.setImageDrawable(drawable);

        accountBalance = (TextView) navigationView.findViewById(R.id.accountBalance);
        updateBalance();

    }

    /**
     * Checks that application runs first time and write flag at SharedPreferences
     *
     * @return true if 1st time
     */
    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        return !ranBefore;
    }

    private void updateBalance() {
        if (accountBalance != null) {
            AccountDBAdapter helper = new AccountDBAdapter(this);
            Double totalBalance = helper.calcTotalBalance();
            if (totalBalance >= 0) {
                accountBalance.setText(Html.fromHtml(totalBalance.toString() + " &uarr;"));
            } else {
                accountBalance.setText(Html.fromHtml(String.valueOf(Math.abs(totalBalance)) + " &darr;"));
            }
        }
    }
}

/**

 To Fix:
    * Landscape Toolbar vertical alignment
    * Refactor set status bar color

 */