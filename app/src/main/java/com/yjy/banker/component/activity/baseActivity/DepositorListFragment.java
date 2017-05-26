package com.yjy.banker.component.activity.baseActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.Account;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.handleableThread.CheckServerUpdateThread;
import com.yjy.banker.handleableThread.GetBalanceListThread;
import com.yjy.banker.handleableThread.GetProfileListThread;
import com.yjy.banker.utils.UserFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class DepositorListFragment extends ListFragment {
    public static final String EXTRA_ARGUMENTS = "EXTRA_ARGUMENTS";

    private Activity mActivity;

    private DepositorAdapter mAdapter;

    private AccountID mAccountID;

    private HashMap<AccountID, Integer> mBalanceList = null;
    private HashMap<AccountID, Profile> mProfileList = null;

    private UserFactory mUserFactory;
    private String mServerAddress;
    private ArrayList<AccountID> mAccountIDList = new ArrayList<>();

    public static class Arguments implements Serializable {
        final AccountID mAccountID;
        final String mServerString;

        public Arguments(AccountID accountID, String serverString) {
            mAccountID = accountID;
            mServerString = serverString;
        }
    }

    private final GetBalanceListThread.OnUpdateListener mOnBalanceListUpdatedListener =
            new GetBalanceListThread.OnUpdateListener() {
                public void onUpdate(@Nullable HashMap<AccountID, Integer> data, int what) {
                    mBalanceList = data;

                    mAccountIDList.clear();
                    addToAccountIDList(mBalanceList);
                    putToTopOfList(mAccountIDList, mAccountID);

                    mAdapter.notifyDataSetChanged();
                    Logger.i("Adapter update.");
                }
            };

    private final GetProfileListThread.OnUpdateListener mOnProfileListUpdateListener =
            new GetProfileListThread.OnUpdateListener() {
                public void onUpdate(@Nullable HashMap<AccountID, Profile> data, int what) {
                    mProfileList = data;
                    mAdapter.notifyDataSetChanged();
                    onProfileListUpdate(mProfileList);
                    Logger.i("Profile update.");
                }
            };

    protected void onProfileListUpdate(HashMap<AccountID, Profile> profileList) {

    }

    private CheckServerUpdateThread mCheckServerUpdateThread = null;
    private final CheckServerUpdateThread.OnUpdateListener mOnServerUpDateListener =
            new CheckServerUpdateThread
                    .OnUpdateListener() {
                @Override
                public void onUpdate(@Nullable Bundle data, int what) {
                    super.onUpdate(data, what);


                    if (CheckServerUpdateThread.isBalanceListChanged(data)) {
                        updateListViewWithBalanceList();
                    }

                    if (CheckServerUpdateThread.isProfileListChanged(data)) {
                        updateListViewWithProfileList();
                    }
                }
            };

    protected abstract Arguments getArgument();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Arguments arguments = getArgument();

        mAccountID = arguments.mAccountID;
        mServerAddress = arguments.mServerString;
        mUserFactory = new UserFactory(mActivity, mAccountID, mServerAddress);

        mAdapter = new DepositorAdapter(mAccountIDList);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCheckServerUpdateThread = new CheckServerUpdateThread(mUserFactory,
                mOnServerUpDateListener);
        mCheckServerUpdateThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCheckServerUpdateThread != null) {
            mCheckServerUpdateThread.stopThread();
        }
    }

    protected DepositorAdapter getAdapter() {
        return mAdapter;
    }

    protected void setServerAddress(String serverAddress) {
        mServerAddress = serverAddress;
        mUserFactory = new UserFactory(mActivity, mAccountID, mServerAddress);
    }

    @Nullable
    protected HashMap<AccountID, Integer> getBalanceList() {
        return mBalanceList;
    }

    @Nullable
    protected HashMap<AccountID, Profile> getProfileList() {
        return mProfileList;
    }

    protected AccountID getAccountID() {
        return mAccountID;
    }

    protected String getServerAddress() {
        return mServerAddress;
    }

    private void updateListViewWithBalanceList() {
        GetBalanceListThread updateBalanceThread =
                new GetBalanceListThread(mUserFactory, mOnBalanceListUpdatedListener);
        updateBalanceThread.start();
    }

    private void updateListViewWithProfileList() {
        GetProfileListThread updateProfileThread =
                new GetProfileListThread(mUserFactory, mOnProfileListUpdateListener);
        updateProfileThread.start();
    }

    private void addToAccountIDList(@Nullable HashMap<AccountID, Integer> balanceList) {
        if (balanceList != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                mAccountIDList.addAll(balanceList.keySet());
            } else {
                for (AccountID id : balanceList.keySet()) {
                    mAccountIDList.add(id);
                }
            }
        }
    }

    private void putToTopOfList(ArrayList<AccountID> balanceList, AccountID accountID) {
        if (balanceList.remove(accountID)) {
            balanceList.add(0, accountID);
        }
    }

    protected class DepositorAdapter extends ArrayAdapter<AccountID> {

        private class ViewHolder {
            TextView nameTextView;
            TextView descriptionTextView;
            TextView balanceTextView;
            View container;
        }

        private DepositorAdapter(List<AccountID> accountIDList) {
            super(mActivity, 0, accountIDList);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            AccountID accountID = getItem(position);

            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(
                        R.layout.list_item_depositor, parent, false);
                holder = getViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (accountID != null) {
                setBackGroundDrawable(holder, accountID);
                showProfile(holder, accountID);
                showBalance(holder, accountID);
            }
            return convertView;
        }

        @NonNull
        private ViewHolder getViewHolder(@NonNull View convertView) {
            DepositorAdapter.ViewHolder holder;
            holder = new ViewHolder();
            holder.container = convertView;
            holder.nameTextView = (TextView) convertView.findViewById(R.id.depositor_name);
            holder.descriptionTextView = (TextView) convertView.findViewById(R.id.depositor_description);
            holder.balanceTextView = (TextView) convertView.findViewById(R.id.depositor_balance);
            return holder;
        }

        private void setBackGroundDrawable(ViewHolder holder, AccountID accountID) {
            if (accountID.equals(mAccountID)) {
                holder.container.setBackgroundResource(R.drawable.list_item_depositor_owner);
            } else if (accountID.getID() == 0) {
                holder.container.setBackgroundResource(R.drawable.list_item_depositor_banker);
            } else {
                holder.container.setBackgroundResource(R.drawable.list_item_depositor);
            }
        }

        private void showBalance(ViewHolder holder, AccountID accountID) {
            if (mBalanceList == null) {
                return;
            }

            Integer balance = mBalanceList.get(accountID);
            holder.balanceTextView.setText(getBalanceString(balance));

            if (accountID.getID() == 0) {
                holder.balanceTextView.setBackgroundColor(
                        ContextCompat.getColor(mActivity, R.color.colorAccent)
                );
            }
        }

        private String getBalanceString(Integer balance) {
            if (balance == Account.INFINITE) {
                return getString(R.string.depositor_infinite_balance);
            }
            return getString(R.string.depositor_balance, balance);
        }

        private void showProfile(ViewHolder holder, AccountID accountID) {
            if (mProfileList == null) {
                return;
            }

            Profile profile = mProfileList.get(accountID);

            if (profile == null) {
                holder.nameTextView.setText("");
                holder.descriptionTextView.setText("");
                return;
            }

            holder.nameTextView.setText(profile.getName());
            holder.descriptionTextView.setText(profile.getDescription());
        }

    }
}
