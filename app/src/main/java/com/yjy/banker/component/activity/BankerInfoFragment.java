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
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.component.activity.baseActivity.OnBackPressedListener;
import com.yjy.banker.component.activity.dialog.CloseBankConfirmDialog;
import com.yjy.banker.component.activity.dialog.Dialogs;
import com.yjy.banker.component.activity.util.EditActionMode;
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

    private static final int REQUEST_CLOSE_BANK = 1;

    private Activity mActivity;

    private TextView mServerAddressText;

    private Profile mProfile;
    private AccountID mAccountID;

    private Dialogs mDialogs;
    private EditActionMode mEditActionModes;

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
        setHasOptionsMenu(true);
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
        view.findViewById(R.id.close_bank).setOnClickListener(this);

        mDescriptionEditText.setHint("id: " + mAccountID.getID());
        showInfoFromProfile(mProfile);
        showServerAddress();

        mEditActionModes = createEditActionModes();
        mEditActionModes.add(mNameEditText);
        mEditActionModes.add(mDescriptionEditText);

        getActivity().setTitle(R.string.title_banker_info_fragment);

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
            case R.id.close_bank:
                String bankerName = getBankerName();
                CloseBankConfirmDialog closeBankConfirmDialog =
                        CloseBankConfirmDialog.newInstance(
                                bankerName,
                                this,
                                REQUEST_CLOSE_BANK);

                closeBankConfirmDialog.show(getContext());
                break;
        }
    }

    private String getBankerName() {
        String bankerName = mProfile.getName();
        if (bankerName == null) {
            bankerName = getString(R.string.anonymous);
        }
        return bankerName;
    }

    private void closeBank() {
        final Login login = Login.newInstance(mActivity);
        mDialogs.showConfirmDialog(R.string.alert_confirm_close_bank,
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
        mActivity.setResult(Activity.RESULT_OK, mResultData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_banker_info, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                mEditActionModes.start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CLOSE_BANK:
                if (resultCode != Activity.RESULT_OK) {
                    break;
                }

                if (CloseBankConfirmDialog.isCodeCorrect(data)) {
                    closeBank();
                }

                break;
        }
    }

    @NonNull
    private EditActionMode createEditActionModes() {
        final EditActionMode editActionModes =
                new EditActionMode((AppCompatActivity) getActivity());

        editActionModes.setOnActionModeFinishedListener(new EditActionMode.onActionModeFinishedListener() {
            @Override
            public void onFinished(Boolean isDone) {
                if (isDone) {
                    updateProfile(BankerInfoFragment.this.mProfile);
                } else {
                    showInfoFromProfile(BankerInfoFragment.this.mProfile);
                }
            }
        });
        return editActionModes;
    }

    private void showInfoFromProfile(Profile profile) {
        mNameEditText.setText(profile.getName());
        mDescriptionEditText.setText(mProfile.getDescription());
    }

    private void showServerAddress() {
        new GetBankServerAddressThread(
                mOnGetBankServerAddressUpdateListener
        ).start();
    }

    private void updateProfile(Profile profile) {
        if (!isProfileChanged()) {
            return;
        }

        profile.setName(mNameEditText.getText().toString());
        profile.setDescription(mDescriptionEditText.getText().toString());

        UserFactory userFactory = new UserFactory(mActivity, mAccountID, mServerAddress);
        new SetProfileThread(userFactory, profile, null)
                .start();
    }

    private boolean isProfileChanged() {
        return !mNameEditText.getText().toString().equals(mProfile.getName()) ||
                !mDescriptionEditText.getText().toString().equals(mProfile.getDescription());
    }
}
