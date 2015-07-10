package in.nowke.expensa;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    HomeFragment homeFragment;
    private FloatingActionButton fabAddAccount;

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
        if (homeFragment.finishActionMode()) {
            return;
        }
        super.onBackPressed();
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // FAB
        fabAddAccount = (FloatingActionButton) findViewById(R.id.fab_add_account);
        fabAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddAccountActivity.class));
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
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                homeFragment.setAccountListAdapter(1);
//                                homeFragment.setFabVisibility(1);
                                fabAddAccount.setVisibility(View.VISIBLE);
                                break;
                            case R.id.nav_archives:
                                homeFragment.setAccountListAdapter(2);
//                                homeFragment.setFabVisibility(0);
                                fabAddAccount.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.nav_trash:
                                homeFragment.setAccountListAdapter(3);
//                                homeFragment.setFabVisibility(0);
                                fabAddAccount.setVisibility(View.INVISIBLE);
                                break;
                        }
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
}

/**

 To Fix:
    1. Landscape Toolbar vertical alignment
    2. Add Account Choose Avatar - Reset happens when grid is out of focus
    3. Refactor set status bar color

 */