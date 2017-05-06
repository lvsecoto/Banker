package com.yjy.banker.handleableThread;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;

public class CheckServerUpdateThread extends HandleableThread<Bundle> {

    private static final String EXTRA_IS_ACCOUNT_ID_CHANGE = "EXTRA_IS_ACCOUNT_ID_CHANGE";
    private static final String EXTRA_IS_BALANCE_CHANGED = "EXTRA_IS_BALANCE_CHANGED";
    private static final String EXTRA_IS_PROFILE_CHANGED = "EXTRA_IS_PROFILE_CHANGED";

    private volatile boolean isRun = true;
    private final User.ModifiedCounters mModifiedCounters;
    private final UserFactory mUserFactory;

    public static class OnUpdateListener implements OnHandleListener<Bundle> {
        @Override
        public void onUpdate(@Nullable Bundle data, int what) {

        }
    }

    /**
     * The reference to listener is weakness, so you don't need to care about
     * the memory leaking.
     *
     * @param IOnUpdatedListener The data you get will be handle by it. Set Null to Ignore the date.
     */
    public CheckServerUpdateThread(UserFactory userFactory, @Nullable OnUpdateListener
            IOnUpdatedListener) {
        super("CheckServerUpdateThread", IOnUpdatedListener);
        mUserFactory = userFactory;
        mModifiedCounters = new User.ModifiedCounters();
    }

    @Override
    public void run() {
        super.run();

        Logger.d("loop start");
        while (isRun) {

            boolean isNeedToBroadcastIntent = false;
            boolean isAccountIDUpdate;
            boolean isBalanceUpdate;
            boolean isProfileListUpdate;

            try {

                User user = mUserFactory.getUser();
                User.ModifiedCounters modifiedCounter
                        = user.getModifiedCounters();

                if (isAccountIDUpdate = isAccountIDListUpdate(modifiedCounter)) {
                    isNeedToBroadcastIntent = true;
                    Logger.i("Account id list has been changed ");
                }

                if (isBalanceUpdate = isBalanceListUpdate(modifiedCounter)) {
                    Logger.i("Balance list has been changed ");
                    isNeedToBroadcastIntent = true;
                }

                if (isProfileListUpdate = isProfileListUpdate(modifiedCounter)) {
                    Logger.i("Profile list has been changed ");
                    isNeedToBroadcastIntent = true;
                }

                if (isNeedToBroadcastIntent) {
                    Bundle data = new Bundle();
                    data.putBoolean(CheckServerUpdateThread.EXTRA_IS_ACCOUNT_ID_CHANGE, isAccountIDUpdate);
                    data.putBoolean(CheckServerUpdateThread.EXTRA_IS_BALANCE_CHANGED, isBalanceUpdate);
                    data.putBoolean(CheckServerUpdateThread.EXTRA_IS_PROFILE_CHANGED, isProfileListUpdate);

                    sendData(data);
                }

                Thread.sleep(1000);
            } catch (IOException e) {
                Logger.w("Fail to check: " + e.getMessage());

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        Logger.d("loop stop");
    }

    public void stopThread() {
        isRun = false;
        this.interrupt();

        removeAllData();
    }

    public static boolean isAccountIDChanged(Bundle bundle) {
        return bundle.getBoolean(EXTRA_IS_ACCOUNT_ID_CHANGE, false);
    }

    public static boolean isBalanceListChanged(Bundle bundle) {
        return bundle.getBoolean(EXTRA_IS_BALANCE_CHANGED, false);
    }

    public static boolean isProfileListChanged(Bundle bundle) {
        return bundle.getBoolean(EXTRA_IS_PROFILE_CHANGED, false);
    }

    private void removeAllData() {
        Handler handler = getHandleableThreadHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private boolean isAccountIDListUpdate(User.ModifiedCounters modifiedCounter) {
        if (mModifiedCounters.accountList != modifiedCounter.accountList) {
            mModifiedCounters.accountList = modifiedCounter.accountList;
            Logger.d("Account list update.");
            return true;
        }

        return false;
    }

    private boolean isBalanceListUpdate(User.ModifiedCounters modifiedCounter) {
        if (mModifiedCounters.balanceList != modifiedCounter.balanceList) {
            mModifiedCounters.balanceList = modifiedCounter.balanceList;
            Logger.d("Balance list update.");
            return true;
        }

        return false;
    }

    private boolean isProfileListUpdate(User.ModifiedCounters modifiedCounter) {
        if (mModifiedCounters.profileList != modifiedCounter.profileList) {
            mModifiedCounters.profileList = modifiedCounter.profileList;
            Logger.d("Balance list update.");
            return true;
        }

        return false;
    }
}
