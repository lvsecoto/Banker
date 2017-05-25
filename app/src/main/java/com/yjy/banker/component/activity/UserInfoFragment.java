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
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.component.activity.baseActivity.OnBackPressedListener;
import com.yjy.banker.component.activity.dialog.Dialogs;
import com.yjy.banker.component.activity.util.EditActionMode;
import com.yjy.banker.handleableThread.SetProfileThread;
import com.yjy.banker.utils.Login;
import com.yjy.banker.utils.NetWorks;
import com.yjy.banker.utils.UserFactory;

import java.io.Serializable;

public class UserInfoFragment extends Fragment implements View.OnClickListener, OnBackPressedListener {

    public static final String EXTRA_ARGUMENTS = "EXTRA_ARGUMENTS";

    private static final String RESULT_IS_LOGOUT = "RESULT_IS_LOGOUT";
    private static final String RESULT_NEW_SERVER_ADDRESS = "RESULT_NEW_SERVER_ADDRESS";

    private Activity mActivity;
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mServerAddressEditText;

    private AccountID mAccountID;
    private String mServerAddress;
    private Profile mProfile;
    private Dialogs mDialogs;
    private EditActionMode mEditActionModes;
    private Intent mResultData;
    private Login mLogin;
    private NetWorks mNetWorks;

    public static class Arguments implements Serializable {

        private final AccountID mAccountID;
        private final String mServerAddress;
        private Profile mProfile;

        public Arguments(@NonNull AccountID accountID, @NonNull String serverAddress,
                         @Nullable Profile profile) {
            mAccountID = accountID;
            mServerAddress = serverAddress;
            mProfile = profile;
            if (mProfile == null) {
                mProfile = new Profile();
            }
        }

    }

    public static UserInfoFragment newInstance(Arguments arguments) {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ARGUMENTS, arguments);
        userInfoFragment.setArguments(args);
        return userInfoFragment;
    }

    public static boolean isLogout(Intent intent) {
        return intent.getBooleanExtra(RESULT_IS_LOGOUT, false);
    }

    /**
     * @return Return the Server address user configured. Return null if user did not change it.
     */
    @Nullable
    public static String getNewServerAddress(Intent intent) {
        if (!intent.hasExtra(RESULT_NEW_SERVER_ADDRESS)) {
            return null;
        }
        return intent.getStringExtra(RESULT_NEW_SERVER_ADDRESS);
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
        mResultData = new Intent();

        Arguments arguments;
        arguments = (Arguments) getArguments().getSerializable(EXTRA_ARGUMENTS);
        assert arguments != null;
        mAccountID = arguments.mAccountID;
        mServerAddress = arguments.mServerAddress;
        mProfile = arguments.mProfile;

        mNetWorks = new NetWorks();
        mLogin = Login.newInstance(mActivity);
        mDialogs = new Dialogs(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        mNameEditText = (EditText) view.findViewById(R.id.depositor_name);
        mDescriptionEditText = (EditText) view.findViewById(R.id.depositor_description);
        mServerAddressEditText = (EditText) view.findViewById(R.id.server_address);
        view.findViewById(R.id.logout).setOnClickListener(this);

        mDescriptionEditText.setHint("id: " + mAccountID.getID());
        showInfoFromProfile(mProfile);
        showServerAddress(mServerAddress);

        mEditActionModes = createEditActionModes();
        mEditActionModes.add(mNameEditText);
        mEditActionModes.add(mDescriptionEditText);
        if (mLogin.getLoginMode() == Login.MODE_USER) {
            mEditActionModes.add(mServerAddressEditText);
        }

        getActivity().setTitle(R.string.title_user_info_fragment);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

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
        mDialogs.showConfirmDialog(R.string.alert_confirm_logout,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            mLogin.logout();
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
        inflater.inflate(R.menu.fragment_user_info, menu);
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

    @NonNull
    private EditActionMode createEditActionModes() {
        final EditActionMode editActionModes =
                new EditActionMode((AppCompatActivity) getActivity());

        editActionModes.setOnActionModeFinishedListener(new EditActionMode
                .onActionModeFinishedListener() {
            @Override
            public void onFinished(Boolean isDone) {
                if (isDone) {
                    updateProfile(mProfile);
                    updateServerAddress();
                } else {
                    showInfoFromProfile(mProfile);
                    showServerAddress(mServerAddress);
                }
            }
        });
        return editActionModes;
    }

    private void showInfoFromProfile(Profile profile) {
        mNameEditText.setText(profile.getName());
        mDescriptionEditText.setText(mProfile.getDescription());
    }

    private void showServerAddress(String serverAddress) {
        mServerAddressEditText.setText(serverAddress);
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

    private void updateServerAddress() {
        if (!isServerAddressChanged()) {
            return;
        }

        String serverAddress = mServerAddressEditText.getText().toString();

        if (mNetWorks.isBelongToNetWork(serverAddress)) {
            mLogin.setupServerAddress(serverAddress);
            mResultData.putExtra(RESULT_NEW_SERVER_ADDRESS, serverAddress);
            mServerAddress = serverAddress;
        }
    }

    private boolean isProfileChanged() {
        return !mNameEditText.getText().toString().equals(mProfile.getName()) ||
                !mDescriptionEditText.getText().toString().equals(mProfile.getDescription());
    }

    private boolean isServerAddressChanged() {
        return !mServerAddressEditText.getText().toString().equals(mServerAddress);
    }
}
