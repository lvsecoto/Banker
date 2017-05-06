package com.yjy.banker.bank.account;

public abstract class AbstractAccount implements Account {
    protected int balance;
    protected final AccountID ID;

    public AbstractAccount(AccountID ID) {
        this.ID = ID;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public AccountID getID() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAccount)) return false;

        AbstractAccount abstractAccount = (AbstractAccount) o;

        return ID.equals(abstractAccount.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

}
