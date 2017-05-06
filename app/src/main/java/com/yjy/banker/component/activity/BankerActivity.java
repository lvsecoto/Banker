package com.yjy.banker.component.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;
import com.yjy.banker.component.service.BankService;

public class BankerActivity extends SingleFragmentActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BankerActivity.class);
        context.startActivity(intent);

        intent = new Intent(context, BankService.class);
        context.startService(intent);
    }

    @Override
    protected Fragment getFragment() {
        return BankerFragment.newInstance();
    }
}
