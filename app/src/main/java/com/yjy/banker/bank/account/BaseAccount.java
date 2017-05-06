package com.yjy.banker.bank.account;

public class BaseAccount extends AbstractAccount {

    public BaseAccount(AccountID ID) {
        super(ID);
    }

    @Override
    public boolean earn(int money) {
        if (isMoneyUpperFlow(money)) {
            return false;
        }
        this.balance += money;
        return true;
    }


    @Override
    public boolean pay(int money) {
        if (isMoneyUnderFlow(money)) {
            return false;
        }
        this.balance -= money;
        return true;
    }

    private boolean isMoneyUnderFlow(int money) {
        return balance < money;
    }

    private boolean isMoneyUpperFlow(int money) {
        long tempBalance = this.balance;
        return tempBalance + money > getCapacity();
    }

}
