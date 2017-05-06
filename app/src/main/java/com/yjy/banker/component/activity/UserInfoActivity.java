package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.R;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;

public class UserInfoActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        UserInfoFragment.Arguments mArguments;
        mArguments = (UserInfoFragment.Arguments)
                getIntent().getSerializableExtra(UserInfoFragment.EXTRA_ARGUMENTS);
        return UserInfoFragment.newInstance(mArguments);
    }

    public static void startActivityForResult(Fragment fragment, UserInfoFragment.Arguments arguments,
                                              int requestCode) {
        Activity activity = fragment.getActivity();
        Intent intent = new Intent(activity, UserInfoActivity.class);
        intent.putExtra(UserInfoFragment.EXTRA_ARGUMENTS, arguments);
        fragment.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }

    public static boolean isLogout(Intent intent) {
        return UserInfoFragment.isLogout(intent);
    }

    public static String getNewServerAddress(Intent intent) {
        return UserInfoFragment.getNewServerAddress(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_right);
    }
}
