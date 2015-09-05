package in.nowke.expensa.activities;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nowke.expensa.BaseActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.fragments.HomeFragment;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 0;
    private static boolean firstTime = false;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
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

                                            case R.id.nav_settings:
                                                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                                                startActivity(intent);
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
//        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
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
            if (mConnectionProgressDialog.isShowing()) {
                mConnectionProgressDialog.dismiss();
            }
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
            resolveSignInError();
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

    /**
     * SYNC
     */
//    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
//            new ResultCallback<DriveApi.DriveContentsResult>() {
//                @Override
//                public void onResult(DriveApi.DriveContentsResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        Message.message(MainActivity.this, "Error while trying to create new file contents");
//                        return;
//                    }
//
//                    final DriveContents driveContents = result.getDriveContents();
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            OutputStream outputStream = driveContents.getOutputStream();
//                            Writer writer = new OutputStreamWriter(outputStream);
//                            try {
//                                writer.write("Hello World!");
//                                writer.close();
//                            } catch (IOException e) {
//                                Log.e("Drive", e.getMessage());
//                            }
//
//
//                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                    .setTitle("New file")
//                                    .setMimeType("text/plain")
//                                    .build();
//
//                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
//                                    .createFile(mGoogleApiClient, changeSet, driveContents)
//                                    .setResultCallback(fileCallback);
//
//                            Drive.DriveApi.getAppFolder(mGoogleApiClient).
//                        }
//                    }.start();
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            Query query = new Query.Builder()
//                                    .addFilter(Filters.eq(SearchableField.TITLE, "New file"))
//                                    .build();
//                            Drive.DriveApi.getAppFolder(mGoogleApiClient)
//                                    .queryChildren(mGoogleApiClient, query)
//                                    .setResultCallback(metadataCallback);
//                        }
//                    }.start();
//
//                }
//            };

//    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback =
//            new ResultCallback<DriveApi.MetadataBufferResult>() {
//                @Override
//                public void onResult(DriveApi.MetadataBufferResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        Message.message(MainActivity.this, "Problem while retrieving results");
//                        return;
//                    }
//                    MetadataBuffer metadata = result.getMetadataBuffer();
//                    if (metadata.getCount() > 0) {
//                        DriveId driveID = metadata.get(0).getDriveId();
//                        new RetrieveDriveFileContentsAsyncTask(MainActivity.this).execute(driveID);
//                    }
//                }
//            };

//    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
//            ResultCallback<DriveFolder.DriveFileResult>() {
//                @Override
//                public void onResult(DriveFolder.DriveFileResult result) {
//                    if (!result.getStatus().isSuccess()) {
//                        Message.message(MainActivity.this, "Error while trying to create the file");
//                        return;
//                    }
//                    Message.message(MainActivity.this, "Created a file in App Folder: "
//                            + result.getDriveFile().getDriveId());
//
//                }
//            };
//
//    final private class RetrieveDriveFileContentsAsyncTask
//            extends ApiClientAsyncTask<DriveId, Boolean, String> {
//
//        public RetrieveDriveFileContentsAsyncTask(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected String doInBackgroundConnected(DriveId... params) {
//            String contents = null;
//            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, params[0]);
//            DriveApi.DriveContentsResult driveContentsResult =
//                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
//            if (!driveContentsResult.getStatus().isSuccess()) {
//                return null;
//            }
//            DriveContents driveContents = driveContentsResult.getDriveContents();
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(driveContents.getInputStream()));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//                contents = builder.toString();
//            } catch (IOException e) {
//                Log.e("Drive", "IOException while reading from the stream", e);
//            }
//
//            driveContents.discard(mGoogleApiClient);
//            return contents;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (result == null) {
//                Message.message(MainActivity.this, "Error while reading from the file");
//                return;
//            }
//            Message.message(MainActivity.this, "File contents: " + result);
//        }
//    }
}