package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.Message;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;

public class SendMessageThread extends HandleableThread<Void> {
    private UserFactory mUserFactory;
    private Message mMessage;

    public static abstract class onUpdateListener implements OnHandleListener<Void> {
    }

    /**
     * The reference to listener is weakness, so you don't need to care about
     * the memory leaking.
     *
     * @param OnHandleListener The data you get will be handle by it. Set Null to Ignore the date.
     */
    public SendMessageThread(UserFactory userFactory, Message message, @Nullable onUpdateListener
            OnHandleListener) {
        super(SendMessageThread.class.getName(), OnHandleListener);

        mUserFactory = userFactory;
        mMessage = message;
    }

    @Override
    public void run() {
        super.run();

        try {
            User user = mUserFactory.getUser();
            user.sendMessage(mMessage);
        } catch (IOException e) {
            Logger.e("Failed to send message", e);
        }
    }
}
