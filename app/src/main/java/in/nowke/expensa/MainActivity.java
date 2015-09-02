package in.nowke.expensa;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.telly.mrvector.MrVector;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static boolean firstTime = false;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mUserPic;
    private ProgressDialog mConnectionProgressDialog;

    Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    HomeFragment homeFragment;
    private static FloatingActionButton fabAddAccount;
    private static NavigationView navigationView;
    private MenuItem mPreviousCheckedItem;
    TextView accountBalance;

    private TextView googleUserNameText;
    private TextView googleUserEmailText;

    private CircleImageView avatarCircle;
    private Drawable account_circle_drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        if (isFirstTime()) {
            firstTime = true;
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
                HomeFragment.finishActionMode();
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
        firstTimeFinished();
    }

    private void firstTimeFinished() {
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
        avatarCircle = (CircleImageView) navigationView.findViewById(R.id.accountAvatar);
        account_circle_drawable = ContextCompat.getDrawable(this, R.drawable.avatar_default);
        avatarCircle.setImageDrawable(account_circle_drawable);

        accountBalance = (TextView) navigationView.findViewById(R.id.accountBalance);
        googleUserNameText = (TextView) navigationView.findViewById(R.id.googleUserName);
        googleUserEmailText = (TextView) navigationView.findViewById(R.id.googleUserEmail);

        updateBalance();

        // Get user name & email from storage
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String googleUserName = preferences.getString("GoogleUserName", "");
        String googleUserEmail = preferences.getString("GoogleUserEmail", "");

        googleUserNameText.setText(googleUserName);
        googleUserEmailText.setText(googleUserEmail);
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

    // SIGN IN

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
//        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        if (firstTime) {
            firstTime = false;
            firstTimeFinished();
        }
        getProfileInformation();
        if (mConnectionProgressDialog.isShowing() ) {
            mConnectionProgressDialog.dismiss();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            else {
                mConnectionProgressDialog.show();
            }
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void signInGplus(View view) {
        if (!mGoogleApiClient.isConnecting()) {

            mSignInClicked = true;
//            if (mConnectionResult == null) {

//            }
//            else {
                resolveSignInError();
//            }
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;

                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();

                googleUserNameText.setText(personName);
                googleUserEmailText.setText(email);

                // Store for temporary access
                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("GoogleUserName", personName);
                editor.putString("GoogleUserEmail", email);
                editor.apply();

                // Load Picture
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + 64;
                new LoadProfileImage(avatarCircle).execute(personPhotoUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public LoadProfileImage(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
            else {
                bmImage.setImageDrawable(account_circle_drawable);
            }
        }
    }
}