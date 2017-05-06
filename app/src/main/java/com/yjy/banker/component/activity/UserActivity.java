package com.yjy.banker.component.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;

public class UserActivity extends SingleFragmentActivity {

    public static void startActivity(Context context, UserFragment.Arguments arguments) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserFragment.EXTRA_ARGUMENTS, arguments);
        context.startActivity(intent);
    }

    @Override
    protected Fragment getFragment() {
        UserFragment.Arguments arguments = (UserFragment.Arguments)
                getIntent().getExtras().getSerializable(UserFragment.EXTRA_ARGUMENTS);
        return UserFragment.newInstance(arguments);
    }

}
