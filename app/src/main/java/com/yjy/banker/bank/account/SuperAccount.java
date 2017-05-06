package com.yjy.banker.bank.account;

public class SuperAccount extends AbstractAccount {
    public SuperAccount(AccountID ID) {
        super(ID);
        super.balance = getCapacity();
    }

    @Override
    public boolean earn(int money) {
        return true;
    }

    @Override
    public boolean pay(int money) {
        return true;
    }
}
