package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.Account;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.service.RequestTransferMoney;
import com.yjy.banker.component.activity.dialog.Dialogs;
import com.yjy.banker.component.activity.dialog.MoneyPickerDialog;
import com.yjy.banker.handleableThread.CheckServerUpdateThread;
import com.yjy.banker.handleableThread.GetBalanceListThread;
import com.yjy.banker.handleableThread.TansferMoneyThread;
import com.yjy.banker.utils.UserFactory;

import java.io.Serializable;
import java.util.HashMap;

public class DepositorsFragment extends Fragment implements View.OnClickListener {
    public static final String EXTRA_ARGUMENTS = "EXTRA_ARGUMENTS";
    private static final int REQUEST_TRANSFER_MONEY = 1;
    private Toolbar mToolbar;

    static class Arguments implements Serializable {
        private final AccountID mAccountID;
        private final String mServerAddress;
        private final AccountID mTargetAccountId;
        private final HashMap<AccountID, Integer> mBalanceList;
        private final HashMap<AccountID, Profile> mProfileList;

        Arguments(
                AccountID accountID,
                String serverAddress,
                AccountID targetAccountId,
                HashMap<AccountID, Integer> balanceList,
                HashMap<AccountID, Profile> profileList) {
            mAccountID = accountID;
            mServerAddress = serverAddress;
            mTargetAccountId = targetAccountId;
            mBalanceList = balanceList;
            mProfileList = profileList;
        }
    }

    private Activity mContext;

    private final Dialogs mDialogs = new Dialogs(this);
    private MoneyPickerDialog mMoneyPickerDialog;

    private TextView mBalanceTextView;
    private TextView mYourBalanceTextView;

    private AccountID mTargetAccountId;
    private AccountID mAccountID;

    private final GetBalanceListThread.OnUpdateListener mOnBalanceUpdatedListener =
            new GetBalanceListThread.OnUpdateListener() {
                @Override
                public void onUpdate(@Nullable HashMap<AccountID, Integer> data, int what) {
                    if (data == null) {
                        return;
                    }
                    mBalanceList = data;
                    showInfoOnView();
                }
            };

    private final CheckServerUpdateThread.OnUpdateListener mCheckServerUpdateListener =
            new CheckServerUpdateThread.OnUpdateListener() {
                @Override
                public void onUpdate(@Nullable Bundle data, int what) {
                    super.onUpdate(data, what);
                    if (CheckServerUpdateThread.isBalanceListChanged(data)) {
                        updateBalanceFromServer();
                    }
                }
            };

    private final TansferMoneyThread.OnUpdateListener
            onTransferMoneyThreadUpdateListener =
            new TansferMoneyThread.OnUpdateListener() {
                @Override
                public void onUpdate(@Nullable Integer result, int what) {
                    super.onUpdate(result, what);
                    mDialogs.dismissPleaseWaitDialog();

                    if (result == null) {
                        mDialogs.showMessageDialog("Net Error!");
                        return;
                    }

                    switch (result) {
                        case RequestTransferMoney.RESULT_FAIL:
                            mDialogs.showMessageDialog("Transfer fail. Do you have enough money?");
                            break;
                        case RequestTransferMoney.RESULT_ILLEGAL_ACCOUNT_ID:
                            mDialogs.showMessageDialog("Can't not find account ID");
                            break;
                        case RequestTransferMoney.RESULT_SUCCEED:
                            updateBalanceFromServer();
                            break;
                    }
                }
            };

    private UserFactory mUserFactory;
    private HashMap<AccountID, Integer> mBalanceList;
    private HashMap<AccountID, Profile> mProfileList;
    private CheckServerUpdateThread mCheckServerUpdateThread;


    public static DepositorsFragment newInstance(Arguments arguments) {
        DepositorsFragment depositorsFragment = new DepositorsFragment();
        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putSerializable(EXTRA_ARGUMENTS, arguments);
        depositorsFragment.setArguments(argumentsBundle);

        return depositorsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Arguments arguments = (Arguments) getArguments().getSerializable(EXTRA_ARGUMENTS);

        assert arguments != null;
        String serverAddress = arguments.mServerAddress;
        mAccountID = arguments.mAccountID;
        mTargetAccountId = arguments.mTargetAccountId;
        mBalanceList = arguments.mBalanceList;
        mProfileList = arguments.mProfileList;

        mUserFactory = new UserFactory(mContext, mAccountID, serverAddress);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_depositors, container, false);
        view.findViewById(R.id.transfer).setOnClickListener(this);

        mBalanceTextView = (TextView) view.findViewById(R.id.depositor_balance);
        mYourBalanceTextView = (TextView) view.findViewById(R.id.your_balance);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(
                getResources().getColor(R.color.light_100));

        showInfoOnView();
        showTitle();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCheckServerUpdateThread = new CheckServerUpdateThread(mUserFactory, mCheckServerUpdateListener);
        mCheckServerUpdateThread.start();

    }

    private void showTitle() {
        mToolbar.setTitle(getTitle());
    }

    private String getTitle() {
        Profile profile;
        String name;
        if (mProfileList == null
                || (profile = mProfileList.get(mTargetAccountId)) == null
                || (name = profile.getName()) == null
                ) {
            name = getString(R.string.anonymous);
        }

        return getString(R.string.title_depositors_fragment, name);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCheckServerUpdateThread != null) {
            mCheckServerUpdateThread.stopThread();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDialogs.dismissAll();
        dismissMoneyPickerDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transfer:
                startTransferMoney();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TRANSFER_MONEY:
                if (resultCode != Activity.RESULT_OK) {
                    break;
                }
                finishTransferMoney(data);
                break;
        }
    }

    private void updateBalanceFromServer() {
        new GetBalanceListThread(mUserFactory, mOnBalanceUpdatedListener)
                .start();
    }

    private void startTransferMoney() {
        showMoneyPickerDialog("Transfer money");
    }

    private void finishTransferMoney(Intent intent) {
        Integer money = MoneyPickerDialog.getResultMoney(intent);
        if (money == null) {
            mDialogs.showMessageDialog("Please input a correct number");
            return;
        }

        new TansferMoneyThread(mUserFactory,
                mTargetAccountId,
                money, onTransferMoneyThreadUpdateListener)
                .start();
        mDialogs.showPleaseWaitDialog();
    }

    private void showMoneyPickerDialog(String title) {
        dismissMoneyPickerDialog();
        mMoneyPickerDialog = MoneyPickerDialog.newInstance(
                title, this, REQUEST_TRANSFER_MONEY
        );
        mMoneyPickerDialog.show(mContext);
    }

    private void dismissMoneyPickerDialog() {
        if (mMoneyPickerDialog != null) {
            mMoneyPickerDialog.dismiss();
        }
    }

    private void showInfoOnView() {
        showDepositorBalance();
    }

    private void showDepositorBalance() {
        if (mBalanceList == null) {
            return;
        }

        mBalanceTextView.setText(
                getBalanceString(mBalanceList.get(mTargetAccountId))
        );
        mYourBalanceTextView.setText(
                getBalanceString(mBalanceList.get(mAccountID))
        );
    }

    private String getBalanceString(int balance) {
        if (balance == Account.INFINITE) {
            return getString(R.string.depositor_infinite_balance);
        }
        return getString(R.string.depositor_balance, balance);
    }
}
