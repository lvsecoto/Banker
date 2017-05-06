package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.R;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;

public class BankerInfoActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        BankerInfoFragment.Arguments arguments = (BankerInfoFragment.Arguments)
                getIntent().getSerializableExtra(BankerInfoFragment.EXTRA_ARGUMENTS);
        return BankerInfoFragment.newInstance(arguments);
    }

    public static void startActivity(Fragment fragment, BankerInfoFragment.Arguments arguments, int requestCode) {
        Activity activity = fragment.getActivity();
        Intent intent = new Intent(activity, BankerInfoActivity.class);
        intent.putExtra(BankerInfoFragment.EXTRA_ARGUMENTS, arguments);
        fragment.startActivityForResult(intent, requestCode);

        activity.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
    }

    public static boolean isLogout(Intent intent) {
        return BankerInfoFragment.isLogout(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_right);
    }
}
