package com.yjy.banker.component.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.component.activity.dialog.Dialogs;
import com.yjy.banker.component.service.BankService;
import com.yjy.banker.handleableThread.ApplyAccountIDThread;
import com.yjy.banker.handleableThread.HandleableThread;
import com.yjy.banker.utils.Login;
import com.yjy.banker.utils.NetWorks;

public class LauncherFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Dialogs mDialogs;

    private String mServerAddress;

    private TextView mBankIPAddressHint;
    private CheckBox mBeBankerWithUserCheckBox;

    private ApplyAccountIDThread.OnUpdateListener mOnUpdateListener;
    private Login mLogin;

    public static LauncherFragment newInstance() {
        return new LauncherFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogs = new Dialogs(this);
        mLogin = Login.newInstance(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_launcher, container, false);

        Button joinBankButton = (Button) view.findViewById(R.id.be_user);
        joinBankButton.setOnClickListener(this);

        Button createBankButton = (Button) view.findViewById(R.id.be_banker);
        createBankButton.setOnClickListener(this);

        EditText bankIPAddress = (EditText) view.findViewById(R.id.bank_ip_address);
        bankIPAddress.addTextChangedListener(mIPAddressTextWatcher);

        mBankIPAddressHint = (TextView) view.findViewById(R.id.bank_ip_address_hint);
        mBeBankerWithUserCheckBox = (CheckBox) view.findViewById(R.id.be_banker_with_user);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mNetWorks = new NetWorks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialogs.dismissAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.be_user:
                beUser();
                break;
            case R.id.be_banker:
                if (mBeBankerWithUserCheckBox.isChecked()) {
                    beBankerWithUser();
                } else {
                    beBanker();
                }
                break;
        }
    }

    private void beUser() {
        if (isIPAddressLegal(mServerAddress)) {
            mOnUpdateListener = new ApplyAccountIDThread.OnUpdateListener() {
                @Override
                public void onUpdate(@Nullable AccountID accountID, int what) {
                    super.onUpdate(accountID, what);
                    mDialogs.dismissPleaseWaitDialog();
                    switch (what) {
                        case HandleableThread.MESSAGE_WHAT_IO_EXCEPTION:
                            mDialogs.showMessageDialog(R.string.alert_network_connection_error);
                            break;

                        case HandleableThread.MESSAGE_WHAT_OK:
                            mLogin.loginWithUser(accountID, mServerAddress);
                            startUserActivity(accountID, mServerAddress);
                            finishThisActivity();
                            break;
                    }
                }

                private void startUserActivity(@Nullable AccountID accountID, String serverAddress) {
                    UserFragment.Arguments arguments = new UserFragment.Arguments(
                            accountID, serverAddress
                    );
                    UserActivity.startActivity(mContext, arguments);
                }

            };
            createApplyAccountIDThread(mOnUpdateListener, mServerAddress);
            mDialogs.showPleaseWaitDialog();
        }
    }

    private void beBanker() {
        BankService.startAndBindService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BankerActivity.startActivity(mContext);
                finishThisActivity();
                mContext.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });

        mLogin.loginWithBanker();
    }

    public void beBankerWithUser() {
        mOnUpdateListener = new ApplyAccountIDThread.OnUpdateListener() {
            @Override
            public void onUpdate(@Nullable AccountID accountID, int what) {
                super.onUpdate(accountID, what);
                mDialogs.dismissPleaseWaitDialog();
                switch (what) {
                    case HandleableThread.MESSAGE_WHAT_IO_EXCEPTION:
                        mDialogs.showMessageDialog(R.string.alert_network_connection_error);
                        break;

                    case HandleableThread.MESSAGE_WHAT_OK:
                        mLogin.loginWithBankerAndUser(accountID);
                        BankerWithUserActivity.startActivity(mContext, accountID);
                        finishThisActivity();
                        break;
                }
            }
        };

        BankService.startAndBindService(mContext, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mContext.unbindService(this);
                createApplyAccountIDThread(mOnUpdateListener, "127.0.0.1");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });

        mLogin.loginWithBanker();
    }

    private void createApplyAccountIDThread(ApplyAccountIDThread.OnUpdateListener onUpdateListener, String serverAddress) {
        ApplyAccountIDThread requireApplyAccountIDThread =
                new ApplyAccountIDThread(
                        serverAddress,
                        onUpdateListener
                );
        requireApplyAccountIDThread.start();
    }

    private void finishThisActivity() {
        if (isAdded()) {
            getActivity().finish();
        }
    }

    private NetWorks mNetWorks;
    private final TextWatcher mIPAddressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mServerAddress = s.toString();
            if (isIPAddressLegal(mServerAddress)) {
                mBankIPAddressHint.setText(R.string.correct);
                mBankIPAddressHint.setTextColor(0xFF00FF00);
            } else {
                mBankIPAddressHint.setText(R.string.incorrect);
                mBankIPAddressHint.setTextColor(0xFFFF0000);
            }
        }
    };

    private boolean isIPAddressLegal(String ipAddress) {
        return mNetWorks.isBelongToNetWork(ipAddress);
    }

}
