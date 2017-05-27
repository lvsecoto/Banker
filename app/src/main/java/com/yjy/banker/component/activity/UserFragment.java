package com.yjy.banker.component.activity;

import android.app.Activity;
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

import java.util.HashMap;

public class UserFragment extends DepositorListFragment {

    private static final int REQUEST_USER_INFO = 1;

    public static UserFragment newInstance(Arguments arguments) {
        UserFragment userFragment = new UserFragment();
        Bundle argumentBundle = new Bundle();
        argumentBundle.putSerializable(EXTRA_ARGUMENTS, arguments);
        userFragment.setArguments(argumentBundle);
        return userFragment;
    }

    @Override
    protected Arguments getArgument() {
        return (Arguments) getArguments().getSerializable(EXTRA_ARGUMENTS);
    }

    @Override
    public void onResume() {
        super.onResume();
        // invoke after the fragment show by activity.
        showTitle(getProfileList());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // invoke after the fragment show by viewpager.
        showTitle(getProfileList());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Profile profile = getProfile();
                UserInfoFragment.Arguments arguments = new UserInfoFragment.Arguments(
                        getAccountID(),
                        getServerAddress(),
                        profile
                );
                UserInfoActivity.startActivityForResult(this, arguments, REQUEST_USER_INFO);
            default:
                return false;
        }
    }

    @Nullable
    private Profile getProfile() {
        HashMap<AccountID, Profile> profileList = getProfileList();
        AccountID accountID = getAccountID();
        Profile profile = null;
        if (profileList != null) {
            profile = profileList.get(accountID);
        }
        return profile;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Activity activity = getActivity();
        switch (requestCode) {
            case REQUEST_USER_INFO:
                if (resultCode != Activity.RESULT_OK) {
                    break;
                }

                if (UserInfoActivity.isLogout(data)) {
                    activity.finish();
                    LauncherActivity.startActivity(activity);
                }

                String newServerAddress = UserInfoActivity.getNewServerAddress(data);
                if (newServerAddress != null) {
                    setServerAddress(newServerAddress);
                }
                break;
        }
    }

    @Override
    protected void onProfileListUpdate(HashMap<AccountID, Profile> profileList) {
        super.onProfileListUpdate(profileList);
        showTitle(profileList);
    }

    private void showTitle(HashMap<AccountID, Profile> profileList) {
        if (!isHidden()) {
            getActivity().setTitle(getTitle(profileList));
        }
    }

    private String getTitle(HashMap<AccountID, Profile> profileList) {

        if (profileList == null) {
            return null;
        }

        Profile bankerProfile = profileList.get(new AccountID(1));

        if (bankerProfile == null) {
            return null;
        }

        String name = bankerProfile.getName();
        if (name == null) {
            name = getString(R.string.anonymous);
        }

        return getString(
                R.string.title_user_fragment,
                name
        );
    }
}
