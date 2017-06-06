package com.yjy.banker.bank.account;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private UUID mUUID = UUID.randomUUID();
    private AccountID mFrom;
    private AccountID mTo;
    private String mType;
    private String text;

    public Message(AccountID from) {
        mFrom = from;
    }

    public AccountID getFrom() {
        return mFrom;
    }

    public AccountID getTo() {
        return mTo;
    }

    public void setTo(AccountID to) {
        mTo = to;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        return mUUID.equals(message.mUUID);
    }

    @Override
    public int hashCode() {
        return mUUID.hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "mFrom=" + mFrom +
                ", mTo=" + mTo +
                ", mType='" + mType + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
