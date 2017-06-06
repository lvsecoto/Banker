package com.yjy.banker.bank.account;


import android.support.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MessageManagerTest {

    private MessageManager mMessageManager;

    @Before
    public void setUp() throws Exception {
        mMessageManager = new MessageManager();
    }

    @Test
    public void sendMessage() throws Exception {
        AccountID fromId = new AccountID(0);
        AccountID toId = new AccountID(1);
        Message message = createTestMessage(fromId, toId);

        mMessageManager.sendMessage(message);

        ArrayList<Message> expectedMessages = new ArrayList<>();
        expectedMessages.add(message);

        assertEquals(expectedMessages, mMessageManager.getMessages(toId));
    }

    @Test
    public void sendMessageToDifferentAccountId() throws Exception {
        AccountID fromId = new AccountID(0);
        Map<AccountID, List<Message>> expectedMessages = new LinkedHashMap<>();

        for (int toId = 0; toId < 10; toId++) {
            List<Message> messages = new ArrayList<>();
            expectedMessages.put(new AccountID(toId), messages);
            for (int messageNbr = 0; messageNbr < 10; messageNbr++) {
                Message message = createTestMessage(fromId, new AccountID(toId));
                mMessageManager.sendMessage(
                        message
                );
                messages.add(message);
            }
        }

        for (AccountID id : expectedMessages.keySet()) {
            assertEquals(expectedMessages.get(id), mMessageManager.getMessages(id));
        }
    }

    @Test
    public void afterGetMessagesClearTheMessagesOfAccountIDInMessageManager() throws Exception {
        AccountID mFromId = new AccountID(0);
        AccountID toId = new AccountID(1);
        for (int i = 0; i < 10; i++) {
            mMessageManager.sendMessage(
                    createTestMessage(mFromId, toId)
            );
        }

        assertNotEquals(null, mMessageManager.getMessages(toId));
        assertEquals(null, mMessageManager.getMessages(toId));
    }

    @NonNull
    private Message createTestMessage(AccountID fromId, AccountID toId) {
        String title = UUID.randomUUID().toString();
        String text = UUID.randomUUID().toString();

        Message message = new Message(fromId);
        message.setTo(toId);
        message.setType(title);
        message.setText(text);
        return message;
    }
}
