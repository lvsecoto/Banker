package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;
import java.util.HashMap;

public class GetProfileListThread extends HandleableThread<HashMap<AccountID, Profile>> {

    private final UserFactory mUserFactory;

    public static class OnUpdateListener
            implements OnHandleListener<HashMap<AccountID, Profile>> {
        @Override
        public void onUpdate(@Nullable HashMap<AccountID, Profile> data, int what) {

        }
    }

    public GetProfileListThread(UserFactory userFactory, OnUpdateListener onProfileListUpdateListener) {
        super(GetProfileListThread.class.getName(), onProfileListUpdateListener);
        mUserFactory = userFactory;
    }

    @Override
    public void run() {
        HashMap<AccountID, Profile> profileList;

        try {
            User user = mUserFactory.getUser();
            profileList = user.getProfileList();
            Logger.d("Received profileList: %s", profileList);
            sendData(profileList);
        } catch (IOException e) {
            Logger.e("Fail to get profileList: %s", e.getMessage());
            sendData(null, HandleableThread.MESSAGE_WHAT_IO_EXCEPTION);
        }

    }
}
