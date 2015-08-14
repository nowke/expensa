package in.nowke.expensa.activities;

import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nowke.expensa.MainActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.AvatarAdapter;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.fragments.HomeFragment;

public class AddAccountActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TextInputLayout mAddAccountName;
    CircleImageView circleImageView;
    private Button saveUpdateButton;
    GridView mAvatarChooser;
    public static int clickedPos;

    private static String LOG_TAG = "AddAccount";
    private int editUserId;
    private int editUserListPos;
    private int editUserIconId;
    private String editUserName;
    private boolean editFromDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
        setContentView(R.layout.activity_add_account);
        editUserId = -1;
        editUserListPos = -1;
        editFromDetail = false;
        if (getIntent().hasExtra("User_id")) {
            editUserId = getIntent().getIntExtra("User_id", -1);
            editUserListPos = getIntent().getIntExtra("LIST_POSITION", -1);
            editUserName = getIntent().getStringExtra("User_name");
            editUserIconId = getIntent().getIntExtra("User_icon_id", -1);
        }
        if (getIntent().hasExtra("is_from_detail_activity")) {
            editFromDetail = true;
        }

        clickedPos = -1;
        saveUpdateButton = (Button) findViewById(R.id.saveOrUpdate);

        // ACTION BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupAppBar(mToolbar);
        if (editUserId != -1) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_edit_account));
        }

        // TEXT INPUT LAYOUT
        mAddAccountName = (TextInputLayout) findViewById(R.id.textinputAddAccountName);
        mAddAccountName.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mAddAccountName.getEditText().getText().toString().length() == 0) {
                    mAddAccountName.setErrorEnabled(true);
                    mAddAccountName.setError("Title can't be blank");
                }
                else {
                    mAddAccountName.setErrorEnabled(false);
                }
            }
        });
        mAddAccountName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveUpdateButton.setEnabled(!s.toString().equals(""));
            }
        });
        mAddAccountName.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Utilities.hideKeyboard(getApplicationContext(), mAddAccountName.getEditText());
                    return true;
                }
                return false;
            }
        });


        // AVATAR CHOOSER GRID
        mAvatarChooser = (GridView) findViewById(R.id.choose_avatar_image);
        mAvatarChooser.setAdapter(new AvatarAdapter(this));
        mAvatarChooser.setSelection(0);
        mAvatarChooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    if (parent.getChildAt(i) != null) {
                        CircleImageView mCircle = (CircleImageView) parent.getChildAt(i).findViewById(R.id.avatar);
                        mCircle.setBorderWidth(0);
                    }
                }
                CircleImageView current = (CircleImageView) view.findViewById(R.id.avatar);
                current.setBorderWidth(8);
                clickedPos = position;
            }
        });

        if (editUserId != -1) {
            mAddAccountName.getEditText().setText(editUserName);
            saveUpdateButton.setText("Update");
            mAvatarChooser.performItemClick(mAvatarChooser.getAdapter().getView(editUserIconId, null, null), editUserIconId, mAvatarChooser.getAdapter().getItemId(editUserIconId));
        }
        else {
            mAvatarChooser.performItemClick(mAvatarChooser.getAdapter().getView(0, null, null), 0, mAvatarChooser.getAdapter().getItemId(0));
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

    public void saveOrUpdate(View view) {
        String accountName = mAddAccountName.getEditText().getText().toString();
        if (editUserId == -1) {
            addAccount(accountName, clickedPos);
        }
        else {
            updateAccount(accountName, clickedPos, editUserListPos);
        }
    }

    private void updateAccount(final String newAccountName, final int clickedPos, final int listPos) {
        if (newAccountName.equals(editUserName) && clickedPos == editUserIconId || newAccountName.isEmpty()) { return; }
        AccountDBAdapter helper = new AccountDBAdapter(this);
        helper.editUser(editUserId, newAccountName, clickedPos);

        if (editFromDetail) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("new_user_name", newAccountName);
            returnIntent.putExtra("new_icon_id", clickedPos);
            setResult(RESULT_OK, returnIntent);
        }

        finish();

        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);

        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                AddAccountActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HomeFragment.adapter.updateAccountNameAndIcon(listPos, newAccountName, clickedPos);
                        HomeFragment.finishActionMode();
                    }
                });
            }
        }.start();
    }

    private void addAccount(String accountName, int iconPosition) {

        if (!accountName.isEmpty() && iconPosition != -1) {
            AccountDBAdapter helper;
            helper = new AccountDBAdapter(this);
            String timeStamp = String.valueOf(Utilities.getCurrentTimeStamp());

            final AccountDetail accountDetail = new AccountDetail();
            accountDetail.user_icon_id = iconPosition;
            accountDetail.user_name = accountName;
            accountDetail.user_balance = 0.0;
            accountDetail.user_created = timeStamp;
            accountDetail.user_account_type = 1;

            long id = helper.addAccount(accountDetail);
            accountDetail.user_id = id;

            finish();

            overridePendingTransition(R.anim.top_in, R.anim.bottom_out);

            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(200);
                    AddAccountActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HomeFragment.adapter.add(accountDetail);
                            HomeFragment.scrollListToTop();
                        }
                    });
                }
            }.start();
            
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);
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
