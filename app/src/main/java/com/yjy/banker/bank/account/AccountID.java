package com.yjy.banker.bank.account;

import java.io.Serializable;

public class AccountID implements Serializable {
    private long ID = 0;

    public AccountID(long ID) {
        this.ID = ID;
    }

    public long getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "" + ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountID)) return false;

        AccountID accountID = (AccountID) o;

        return ID == accountID.ID;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(ID).hashCode();
    }
}
