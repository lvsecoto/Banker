package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.IllegalAccountIDException;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseBankServiceTest {

    private BankService bankService;
    private AccountID superAccountID;
    private AccountID accountID;
    private AccountID anotherAccountID;

    @Before
    public void setUp() throws Exception {
        Bank bank = new BaseBank();
        bankService = bank.getBankService();
        superAccountID = bankService.applySupperAccount();
        accountID = bankService.applyAccount();
        anotherAccountID = bankService.applyAccount();
        assertEquals(3, bankService.getAccountIDListModifiedCounter());
    }

    @Test
    public void superAccountTransferMoneyToBaseAccount() throws Exception {
        assertTrue(bankService.transferMoney(superAccountID, accountID, 100));
        assertEquals(100, bankService.getBalance(accountID));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void transferBetweenTwoAccountWithTwoThread() throws Exception {
        long modifiedCounterBefore = bankService.getAccountIDListModifiedCounter();

        bankService.transferMoney(superAccountID, accountID, 100);
        assertEquals(2, bankService.getBalanceListModifiedCounter() - modifiedCounterBefore);

        bankService.transferMoney(superAccountID, accountID, 100);
        assertEquals(4, bankService.getBalanceListModifiedCounter() - modifiedCounterBefore);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        bankService.transferMoney(accountID, anotherAccountID, 1);
                    }
                } catch (IllegalAccountIDException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        bankService.transferMoney(anotherAccountID, accountID, 1);
                    }
                } catch (IllegalAccountIDException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();

        while (thread1.isAlive() || thread2.isAlive()) ;

        assertEquals(200, bankService.getBalance(accountID) + bankService.getBalance(anotherAccountID));

        assertEquals(404, bankService.getBalanceListModifiedCounter() - modifiedCounterBefore);

    }

}