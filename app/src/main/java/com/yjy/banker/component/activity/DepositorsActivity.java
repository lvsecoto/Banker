package com.yjy.banker.component.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;

public class DepositorsActivity extends SingleFragmentActivity {

    public static void startActivity(Context context,
                                     DepositorsFragment.Arguments arguments) {
        Intent intent = new Intent(context, DepositorsActivity.class);
        intent.putExtra(DepositorsFragment.EXTRA_ARGUMENTS, arguments);
        context.startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Fragment getFragment() {
        DepositorsFragment.Arguments arguments =
                (DepositorsFragment.Arguments) getIntent().getExtras().getSerializable(
                        DepositorsFragment.EXTRA_ARGUMENTS);
        return DepositorsFragment.newInstance(arguments);
    }
}
