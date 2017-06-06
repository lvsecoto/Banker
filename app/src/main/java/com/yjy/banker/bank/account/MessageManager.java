package com.yjy.banker.bank.account;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessageManager {
    private LinkedHashMap<AccountID, ArrayList<Message>> mMessageList = new LinkedHashMap<>();

    public void sendMessage(@NonNull Message message) {
        AccountID id = message.getTo();
        ArrayList<Message> messages = mMessageList.get(id);

        if (messages == null) {
            messages = new ArrayList<>();
            mMessageList.put(id, messages);
        }

        messages.add(message);
    }

    public ArrayList<Message> getMessages(AccountID toId) {
        ArrayList<Message> messages = mMessageList.get(toId);
        mMessageList.put(toId, null);
        return messages;
    }
}
