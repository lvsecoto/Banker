package com.yjy.banker.handleableThread;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.account.Message;
import com.yjy.banker.bank.bank.User;
import com.yjy.banker.utils.UserFactory;

import java.io.IOException;
import java.util.ArrayList;

public class GetMessageThread extends HandleableThread<ArrayList<Message>> {

    private UserFactory mUserFactory;

    private boolean isStop = false;

    public static abstract class onUpdateListener
            implements OnHandleListener<ArrayList<Message>> {
    }

    public GetMessageThread(
            UserFactory userFactory,
            @Nullable OnHandleListener<ArrayList<Message>> OnHandleListener) {
        super(GetMessageThread.class.getName(), OnHandleListener);
        mUserFactory = userFactory;
    }

    @Override
    public void run() {
        super.run();

        while (!isStop) {
            try {
                Thread.sleep(500);
                User user = mUserFactory.getUser();

                ArrayList<Message> messages = user.getMessages();
                if (messages != null) {
                    sendData(messages);
                }

            } catch (IOException e) {
                Logger.e("Failed to get message", e);
            } catch (InterruptedException e) {
                break;
            }
        }

        Logger.d("Check message thread stop");
    }

    public void stopThread() {
        isStop = true;
        this.interrupt();
    }
}
