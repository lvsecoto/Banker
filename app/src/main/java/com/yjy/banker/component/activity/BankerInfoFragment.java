package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.component.activity.baseActivity.OnBackPressedListener;
import com.yjy.banker.component.activity.dialog.Dialogs;
import com.yjy.banker.handleableThread.HandleableThread;
import com.yjy.banker.handleableThread.OnHandleListener;
import com.yjy.banker.handleableThread.SetProfileThread;
import com.yjy.banker.utils.Login;
import com.yjy.banker.utils.NetWorks;
import com.yjy.banker.utils.UserFactory;

import java.io.Serializable;

public class BankerInfoFragment extends Fragment implements View.OnClickListener, OnBackPressedListener {

    public static final String EXTRA_ARGUMENTS = "EXTRA_ARGUMENTS";

    private static final String RESULT_IS_LOGOUT = "RESULT_IS_LOGOUT";

    private Activity mActivity;

    private TextView mServerAddressText;

    private Profile mProfile;
    private AccountID mAccountID;

    private Dialogs mDialogs;

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private Intent mResultData;
    private String mServerAddress;

    public static class Arguments implements Serializable {

        private final AccountID mAccountID;
        private final Profile mProfile;

        public Arguments(@NonNull AccountID accountID, @NonNull Profile profile) {
            mAccountID = accountID;
            mProfile = profile;
        }
    }

    public static BankerInfoFragment newInstance(Arguments arguments) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ARGUMENTS, arguments);

        BankerInfoFragment fragment = new BankerInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static boolean isLogout(Intent intent) {
        return intent.getBooleanExtra(RESULT_IS_LOGOUT, false);
    }

    private static class GetBankServerAddressThread extends HandleableThread<String> {

        private static abstract class OnUpdateListener implements OnHandleListener<String> {
        }

        GetBankServerAddressThread(@Nullable OnUpdateListener IOnUpdatedListener) {
            super(GetBankServerAddressThread.class.getName(), IOnUpdatedListener);
        }

        @Override
        public void run() {
            super.run();
            String addressName = NetWorks.getLocalHostAddress();
            sendData(addressName);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogs = new Dialogs(this);

        Arguments arguments = (Arguments) getArguments().getSerializable(EXTRA_ARGUMENTS);

        assert arguments != null;
        mServerAddress = "127.0.0.1";
        mAccountID = arguments.mAccountID;
        mProfile = arguments.mProfile;

        mResultData = new Intent();
    }

    private final GetBankServerAddressThread.OnUpdateListener mOnGetBankServerAddressUpdateListener
            = new GetBankServerAddressThread.OnUpdateListener() {
        @Override
        public void onUpdate(@Nullable String data, int what) {
            if (data != null) {
                mServerAddressText.setText(data);
            } else {
                mServerAddressText.setText(R.string.invalid);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banker_info, container, false);

        mNameEditText = (EditText) view.findViewById(R.id.depositor_name);
        mDescriptionEditText = (EditText) view.findViewById(R.id.depositor_description);
        mServerAddressText = (TextView) view.findViewById(R.id.server_address);
        view.findViewById(R.id.logout).setOnClickListener(this);

        mDescriptionEditText.setHint("id: " + mAccountID.getID());
        mNameEditText.setText(mProfile.getName());
        mDescriptionEditText.setText(mProfile.getDescription());

        new GetBankServerAddressThread(
                mOnGetBankServerAddressUpdateListener
        ).start();

        mServerAddressText.setEnabled(false);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialogs.dismissAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        final Login login = Login.newInstance(mActivity);
        mDialogs.showConfirmDialog(R.string.alert_confirm_logout,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            login.logout();
                            mResultData.putExtra(RESULT_IS_LOGOUT, true);
                            finishActivity(mResultData);
                        }
                    }
                });
    }

    private void finishActivity(Intent data) {
        mActivity.setResult(Activity.RESULT_OK, data);
        mActivity.finish();
    }

    @Override
    public void onBackPressed() {
        saveConfigure();
        mActivity.setResult(Activity.RESULT_OK, mResultData);
    }

    private void saveConfigure() {
        if (isProfileChanged()) {

            mProfile.setName(mNameEditText.getText().toString());
            mProfile.setDescription(mDescriptionEditText.getText().toString());

            UserFactory userFactory = new UserFactory(mActivity, mAccountID, mServerAddress);
            new SetProfileThread(userFactory, mProfile, null)
                    .start();
        }

    }

    private boolean isProfileChanged() {
        return !mNameEditText.getText().toString().equals(mProfile.getName()) ||
                !mDescriptionEditText.getText().toString().equals(mProfile.getDescription());
    }
}
