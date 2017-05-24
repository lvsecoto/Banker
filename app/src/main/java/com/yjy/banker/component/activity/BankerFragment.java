package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.component.activity.baseActivity.DepositorListFragment;

public class BankerFragment extends DepositorListFragment {

    private static final int REQUEST_BANKER_INFO = 1;
    private Activity mActivity;

    public static BankerFragment newInstance() {
        return new BankerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected Arguments getArgument() {
        return new Arguments(
                new AccountID(1), "127.0.0.1"
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_banker, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AccountID accountID = getAdapter().getItem(position);
        DepositorsFragment.Arguments arguments = new DepositorsFragment.Arguments(
                getAccountID(),
                getServerAddress(),
                accountID,
                getBalanceList(),
                getProfileList()
        );

        DepositorsActivity.startActivity(getContext(), arguments);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.banker_info:
                Profile profile;
                if (getProfileList() != null) {
                    profile = getProfileList().get(getAccountID());
                } else {
                    profile = new Profile();
                }
                BankerInfoActivity.startActivity(this, new BankerInfoFragment.Arguments(
                                getAccountID(),
                                profile)
                        , REQUEST_BANKER_INFO);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BANKER_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    if (BankerInfoActivity.isLogout(data)) {
                        mActivity.finish();
                        LauncherActivity.startActivity(mActivity);
                    }
                }
                break;
        }
    }

    @Override
    protected void showInfoAtSubTitle(String serverAddress) {
        super.showInfoAtSubTitle("");
    }
}

