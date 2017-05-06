package com.yjy.banker.bank.account;

public interface Account {
    int INFINITE = Integer.MAX_VALUE;

    int getBalance();

    AccountID getID();

    boolean earn(int money);

    boolean pay(int money);

    int getCapacity();
}
