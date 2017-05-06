package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;

public class SetProfileThread extends HandleableThread<Boolean> {

    private final Profile mProfile;
    private final UserFactory mUserFactory;

    /**
     *
     */
    public static class OnUpdateListener implements OnHandleListener<Boolean> {

        /**
         * @param isSuccessful Set to false if there are is not enough money to transfer.
         *                     Set to null if transfer money fail, may because there is
         *                     an I/O error.
         */
        @Override
        public void onUpdate(@Nullable Boolean isSuccessful, int what) {

        }
    }

    public SetProfileThread(
            UserFactory userFactory,
            Profile profile,
            @Nullable OnUpdateListener listener) {
        super(SetProfileThread.class.getName(), listener);
        mUserFactory = userFactory;
        mProfile = profile;
    }

    @Override
    public void run() {
        try {
            User user = mUserFactory.getUser();
            boolean isSuccessful = user.setProfile(mProfile);
            Logger.d("Profile successfully? %s", isSuccessful);
            sendData(isSuccessful);
        } catch (IOException e) {
            Logger.e("Fail to set profile", e);
            sendData(null);
        }
    }
}
