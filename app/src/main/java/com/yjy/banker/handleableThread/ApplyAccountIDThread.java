package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.BaseUser;
import com.yjy.banker.bank.bank.User;

import java.io.IOException;

public class ApplyAccountIDThread extends HandleableThread<AccountID> {
    private final String mServerAddress;

    public static class OnUpdateListener implements OnHandleListener<AccountID> {
        @Override
        public void onUpdate(@Nullable AccountID accountID, int what) {

        }
    }

    public ApplyAccountIDThread(String serverAddress,
                                @Nullable OnUpdateListener listener) {
        super("Request Account ID", listener);
        mServerAddress = serverAddress;
    }

    @Override
    public void run() {
        super.run();
        try {
            User user = new BaseUser(mServerAddress);
            AccountID accountID = user.getAccountID();
            sendData(accountID);
        } catch (IOException e) {
            Logger.e("Can not apply accountID: ", e.getMessage());
            sendData(null, MESSAGE_WHAT_IO_EXCEPTION);
        }
    }
}
