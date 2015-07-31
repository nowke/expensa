package in.nowke.expensa.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import in.nowke.expensa.MainActivity;
import in.nowke.expensa.R;
import in.nowke.expensa.activities.AccountDetailActivity;
import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.AccountListAdapter;
import in.nowke.expensa.adapters.AccountListSectionedAdapter;
import in.nowke.expensa.classes.Utilities;
import in.nowke.expensa.entity.AccountDetail;
import in.nowke.expensa.classes.ActionCallback;
import in.nowke.expensa.classes.ClickListener;
import in.nowke.expensa.classes.DividerItemDecoration;
import in.nowke.expensa.classes.RecyclerTouchListener;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by nav on 26/6/15.
 */
public class HomeFragment extends Fragment {

    public static RecyclerView mAccountList;
    public static AccountListAdapter adapter;

    private LinearLayout emptyView;
    private TextView emptyViewText;
    private ImageView emptyViewImage;

    private static ActionMode mActionMode;
    private int selectedItem;

    private int statusBarColor;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        emptyView = (LinearLayout) rootView.findViewById(R.id.empty_row_view);
        emptyViewText = (TextView) rootView.findViewById(R.id.empty_row_text);
        emptyViewImage = (ImageView) rootView.findViewById(R.id.empty_row_image);

        // ACCOUNT LIST RECYCLERVIEW
        mAccountList = (RecyclerView) rootView.findViewById(R.id.accountListRecycler);
        adapter = new AccountListAdapter(getActivity(), getData(1), emptyView);
        mAccountList.setAdapter(adapter);
        mAccountList.setLayoutManager(new LinearLayoutManager(getActivity()));;
        mAccountList.setItemAnimator(new Utilities.AccountSlideInAnimator());
        mAccountList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mAccountList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (finishActionMode()) {
                    // Finished Action Mode
                    return;
                }
                if (!view.isClickable()) { return; }
                TextView userIdText = (TextView) view.findViewById(R.id.userId);
                Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
                intent.putExtra("USER_ID", userIdText.getText().toString());
                intent.putExtra("LIST_POSITION", position);

                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
                if (mActionMode != null || !view.isClickable()) {
                    return;
                }

                mActionModeCallback.setClickedView(view, position);
                selectedItem = position;
                AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
                mActionMode = parentActivity.startSupportActionMode(mActionModeCallback);

                view.setSelected(true);
            }
        }));

        return rootView;
    }

    public static boolean finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            return true;
        }
        return false;
    }


    public List<AccountDetail> getData(int accountType) {
        AccountDBAdapter helper = new AccountDBAdapter(getActivity());

        switch (accountType) {
            case 1:
                emptyViewText.setText(getResources().getString(R.string.empty_text_home));
                emptyViewImage.setImageResource(R.drawable.ic_account_box_white_big);
                break;
            case 2:
                emptyViewText.setText(getResources().getString(R.string.empty_text_archive));
                emptyViewImage.setImageResource(R.drawable.ic_archive_white_big);
                break;
            case 3:
                emptyViewText.setText(getResources().getString(R.string.empty_text_trash));
                emptyViewImage.setImageResource(R.drawable.ic_delete_white_big);
                break;
            case 4:
                emptyViewText.setText(getResources().getString(R.string.empty_text_credit));
                emptyViewImage.setImageResource(R.drawable.ic_account_box_white_big);
                break;
            case 5:
                emptyViewText.setText(getResources().getString(R.string.empty_text_debit));
                emptyViewImage.setImageResource(R.drawable.ic_account_box_white_big);
                break;
        }

        return helper.getAccountInfo(accountType);
    }

    public ActionMode.Callback getActionModeCallback() {
        return mActionModeCallback;
    }

    public void setAccountListAdapter(int accountType) {
        if (accountType <= 3) {
            adapter = new AccountListAdapter(getActivity(), getData(accountType), emptyView);
            mAccountList.swapAdapter(adapter, false);
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
        else {
            List<List<AccountDetail>> splittedAccounts = Utilities.splitAccounts(getData(accountType));
            List<AccountDetail> mainAccountData = splittedAccounts.get(0);
            List<AccountDetail> archiveAccountData = splittedAccounts.get(1);
            List<AccountDetail> trashAccountData = splittedAccounts.get(2);

            AccountListSectionedAdapter mAdapter;
            mAdapter = new AccountListSectionedAdapter(getActivity(), mainAccountData, archiveAccountData, trashAccountData, emptyView);
            mAccountList.swapAdapter(mAdapter, false);
            emptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    public static void scrollListToTop() {
        mAccountList.scrollToPosition(0);
    }

    private ActionCallback mActionModeCallback = new ActionCallback() {

        public View mClickedView;
        public int mClickedPosition;

        public void setClickedView(View view, int position) {
            mClickedView = view;
            mClickedPosition = position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //set your gray color
                getActivity().getWindow().setStatusBarColor(0xFF555555);
            }

            MenuInflater inflater = mode.getMenuInflater();
            TextView uAccType = (TextView) mClickedView.findViewById(R.id.userAccountType);
            int accType = Integer.parseInt(uAccType.getText().toString());
            switch (accType) {
                case 1:
                    inflater.inflate(R.menu.menu_account_select_home, menu);
                    break;
                case 2:
                    inflater.inflate(R.menu.menu_account_select_archive, menu);
                    break;
                case 3:
                    inflater.inflate(R.menu.menu_account_select_trash, menu);
                    break;
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            TextView userIdText = (TextView) mClickedView.findViewById(R.id.userId);
            TextView userNameText = (TextView) mClickedView.findViewById(R.id.userName);
            int uid = Integer.parseInt(userIdText.getText().toString());
            AccountDBAdapter helper = new AccountDBAdapter(getActivity());

            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.main_content);

            if (id == R.id.action_editor) {
                int userIconId = helper.getIconById(uid);
                Intent editAccountIntent = new Intent(getActivity(), AddAccountActivity.class);
                editAccountIntent.putExtra("User_id", uid);
                editAccountIntent.putExtra("User_icon_id", userIconId);
                editAccountIntent.putExtra("LIST_POSITION", mClickedPosition);
                editAccountIntent.putExtra("User_name", userNameText.getText().toString());
                startActivity(editAccountIntent);
                return true;
            }

            if (id == R.id.action_remover) {
                helper.trashAccount(uid);
                adapter.remove(selectedItem);
                mode.finish();
                Snackbar.make(coordinatorLayout, "Deleted", Snackbar.LENGTH_SHORT).show();
                return true;
            }

            if (id == R.id.action_acrhive) {
                helper.archiveAccount(uid);
                adapter.remove(selectedItem);
                mode.finish();
                return true;
            }

            if (id == R.id.action_move_inbox) {
                helper.unarchiveAccount(uid);
                adapter.remove(selectedItem);
                mode.finish();
                return true;
            }

            if (id == R.id.action_restore) {
                helper.restoreAccount(uid);
                adapter.remove(selectedItem);
                mode.finish();
                return true;
            }

            if (id == R.id.action_delete) {
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
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getActivity().getWindow().setStatusBarColor(statusBarColor);
            }
            mActionMode = null;
            selectedItem = -1;
            mClickedView.setSelected(false);
        }
    };
}
