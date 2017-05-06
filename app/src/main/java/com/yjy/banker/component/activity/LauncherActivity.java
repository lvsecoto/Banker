package com.yjy.banker.component.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.component.activity.baseActivity.SingleFragmentActivity;
import com.yjy.banker.utils.Login;

public class LauncherActivity extends SingleFragmentActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, LauncherActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected Fragment getFragment() {
        Login login = Login.newInstance(this);
        UserFragment.Arguments arguments;

        switch (login.getLoginMode()) {
            case Login.MODE_NULL:
                break;

            case Login.MODE_USER:
                if ((arguments = getUserConfig(login)) != null) {
                    UserActivity.startActivity(this, arguments);
                    finish();
                }
                break;
            case Login.MODE_BANK:
                BankerActivity.startActivity(this);
                finish();
                break;

            case Login.MODE_BANK_WITH_USER:
                AccountID accountID;
                if ((accountID = getUserWithBanker(login)) != null) {
                    BankerWithUserActivity.startActivity(this, accountID);
                    finish();
                }
                break;
        }

        return LauncherFragment.newInstance();
    }

    private UserFragment.Arguments getUserConfig(Login login) {
        AccountID id = login.getAccountID();
        String serverAddress = login.getServerAddress();

        if (id == null || serverAddress == null) {
            return null;
        }

        UserFragment.Arguments arguments;
        arguments = new UserFragment.Arguments(id, serverAddress);
        return arguments;
    }

    private AccountID getUserWithBanker(Login login) {
        return login.getAccountID();
    }
}
