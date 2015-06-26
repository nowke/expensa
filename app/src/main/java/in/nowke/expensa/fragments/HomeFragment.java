package in.nowke.expensa.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import java.util.List;

import in.nowke.expensa.R;
import in.nowke.expensa.activities.AddAccountActivity;
import in.nowke.expensa.adapters.AccountDBAdapter;
import in.nowke.expensa.adapters.AccountListAdapter;
import in.nowke.expensa.classes.AccountDetail;
import in.nowke.expensa.classes.ActionCallback;
import in.nowke.expensa.classes.ClickListener;
import in.nowke.expensa.classes.DividerItemDecoration;
import in.nowke.expensa.classes.Message;
import in.nowke.expensa.classes.RecyclerTouchListener;

/**
 * Created by nav on 26/6/15.
 */
public class HomeFragment extends Fragment {

    public RecyclerView mAccountList;
    public static AccountListAdapter adapter;
    private FloatingActionButton fabAddAccount;

    private ActionMode mActionMode;
    private int selectedItem;

    private int statusBarColor;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // ACCOUNT LIST RECYCLERVIEW
        mAccountList = (RecyclerView) rootView.findViewById(R.id.accountListRecycler);
        adapter = new AccountListAdapter(getActivity(), getData());
        mAccountList.setAdapter(adapter);
        mAccountList.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        mAccountList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAccountList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mAccountList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (finishActionMode()) {
                    // Finished Action Mode
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
                AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
                mActionMode = parentActivity.startSupportActionMode(mActionModeCallback);

                view.setSelected(true);
            }
        }));

        // FAB
        fabAddAccount = (FloatingActionButton) rootView.findViewById(R.id.fab_add_account);
        fabAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAccountActivity.class));
            }
        });

        return rootView;
    }

    public boolean finishActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            return true;
        }
        return false;
    }


    public List<AccountDetail> getData() {
        AccountDBAdapter helper = new AccountDBAdapter(getActivity());
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
                statusBarColor = getActivity().getWindow().getStatusBarColor();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //set your gray color
                getActivity().getWindow().setStatusBarColor(0xFF555555);
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
                AccountDBAdapter helper = new AccountDBAdapter(getActivity());
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
