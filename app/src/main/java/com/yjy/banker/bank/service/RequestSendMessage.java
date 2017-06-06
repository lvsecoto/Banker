package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.Message;
import com.yjy.banker.bank.account.MessageManager;

public class RequestSendMessage extends AbstractRequest {
    private Message mMessage;

    public RequestSendMessage(Message message) {
        mMessage = message;
    }

    @Override
    public void execute() {
        MessageManager messageManager = getBankService().getMessageManager();
        messageManager.sendMessage(mMessage);
    }
}
