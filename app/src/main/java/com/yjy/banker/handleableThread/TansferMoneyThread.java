package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;

public class TansferMoneyThread extends HandleableThread<Integer> {
    private final UserFactory mUserFactory;
    private final AccountID mTargetAccountId;
    private final int money;

    public static class OnUpdateListener implements OnHandleListener<Integer> {

        /**
         * @param result Null if a net error occurred. Or:
         *               <ul>
         *               <li>RequestTransferMoney.RESULT_SUCCEED</li>
         *               <li>RequestTransferMoney.RESULT_FAIL</li>
         *               <li>RequestTransferMoney.RESULT_ILLEGAL_ACCOUNT_ID</li>
         *               </ul>
         */
        @Override
        public void onUpdate(@Nullable Integer result, int what) {

        }
    }

    /**
     * A thread for require server for transferring money.
     *
     * @param userFactory     For the user info.
     * @param targetAccountId Money you want to transfer to.
     * @param money           How much money you want to transfer
     * @param listener        What to do when transferring is finished. Passing Null to Ignore.
     */
    public TansferMoneyThread(
            UserFactory userFactory,
            AccountID targetAccountId,
            Integer money,
            OnUpdateListener listener) {
        super(TansferMoneyThread.class.getName(), listener);
        mUserFactory = userFactory;
        mTargetAccountId = targetAccountId;
        this.money = money;
    }

    @Override
    public void run() {
        try {
            User user = mUserFactory.getUser();
            int result = user.transferMoney(mTargetAccountId, money);
            sendData(result);
        } catch (IOException e) {
            Logger.e("run: fail to transfer money", e);
            sendData(null);
        }
    }
}
