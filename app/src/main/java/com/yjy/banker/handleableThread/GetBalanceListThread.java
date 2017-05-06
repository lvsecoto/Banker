package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;
import java.util.HashMap;

public class GetBalanceListThread extends HandleableThread<HashMap<AccountID, Integer>> {
    private final UserFactory mUserFactory;

    public static class OnUpdateListener implements OnHandleListener<HashMap<AccountID, Integer>> {
        @Override
        public void onUpdate(@Nullable HashMap<AccountID, Integer> data, int what) {

        }
    }

    public GetBalanceListThread(UserFactory userFactory, OnUpdateListener listener) {
        super(GetBalanceListThread.class.getName(), listener);
        mUserFactory = userFactory;
    }

    @Override
    public void run() {

        try {
            User user = mUserFactory.getUser();
            HashMap<AccountID, Integer> balanceList;
            balanceList = user.getBalanceList();
            sendData(balanceList);
            Logger.i("Balance list get: \n%s", balanceList);

        } catch (IOException e) {
            Logger.w("Fail to get BalanceList : \n%s", e.getMessage());
            sendData(null, HandleableThread.MESSAGE_WHAT_IO_EXCEPTION);
        }

    }

}
