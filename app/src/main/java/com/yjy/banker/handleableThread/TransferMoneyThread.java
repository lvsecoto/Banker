package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;

public class TransferMoneyThread extends HandleableThread<TransferMoneyThread.Result> {
    private final UserFactory mUserFactory;
    private final AccountID mTargetAccountId;
    private final int mMoney;

    public class Result {
        /**
         * <ul>
         * <li>RequestTransferMoney.RESULT_SUCCEED</li>
         * <li>RequestTransferMoney.RESULT_FAIL</li>
         * <li>RequestTransferMoney.RESULT_ILLEGAL_ACCOUNT_ID</li>
         * </ul>
         */
        public int mResult;
        public int mMoney;
        public AccountID mTargetAccountId;

        public Result(int result, int money, AccountID targetAccountId) {
            mResult = result;
            mMoney = money;
            mTargetAccountId = targetAccountId;
        }
    }

    public static abstract class OnUpdateListener implements OnHandleListener<Result> {

        /**
         * @param result Null if a net error occurred.
         */
        @Override
        public void onUpdate(@Nullable Result result, int what) {

        }

    }

    /**
     * A thread for require server for transferring mMoney.
     *
     * @param userFactory     For the user info.
     * @param targetAccountId Money you want to transfer to.
     * @param money           How much mMoney you want to transfer
     * @param listener        What to do when transferring is finished. Passing Null to Ignore.
     */
    public TransferMoneyThread(
            UserFactory userFactory,
            AccountID targetAccountId,
            Integer money,
            OnUpdateListener listener) {
        super(TransferMoneyThread.class.getName(), listener);
        mUserFactory = userFactory;
        mTargetAccountId = targetAccountId;
        mMoney = money;
    }

    @Override
    public void run() {
        try {
            User user = mUserFactory.getUser();
            int result = user.transferMoney(mTargetAccountId, mMoney);
            sendData(new Result(result, mMoney, mTargetAccountId));
        } catch (IOException e) {
            Logger.e("run: fail to transfer mMoney", e);
            sendData(null);
        }
    }
}
