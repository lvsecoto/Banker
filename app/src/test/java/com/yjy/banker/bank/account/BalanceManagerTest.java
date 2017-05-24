package com.yjy.banker.bank.account;

import com.yjy.banker.bank.Persistence.BankDatabaseMocker;
import com.yjy.banker.bank.Persistence.IBankDatabase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BalanceManagerTest {

    private BalanceManager mBalanceManager;
    private IBankDatabase mBankDatabase;
    private AccountID mSuperId;

    @Before
    public void setUp() throws Exception {
        mBalanceManager = new BalanceManager();
        mBankDatabase = new BankDatabaseMocker();

        createSuperAccount();
    }

    private void createSuperAccount() {
        mBankDatabase.reset();
        mSuperId = mBankDatabase.insertAccount();
        mBalanceManager.add(new SuperAccount(mSuperId));
    }

    @Test
    public void transferMoneyFromOneToAnother() throws Exception {
        int testBalance = 100;

        AccountID id = mBankDatabase.insertAccount();
        mBalanceManager.add(new BaseAccount(id));

        if (mBalanceManager.transferMoney(mSuperId, id, testBalance)) {
            assertEquals(testBalance, getBalance(id));
        } else {
            fail();
        }
    }

    @Test
    public void cantTransferIfAccountHasNotEnoughMoney() throws Exception {
        BalanceManager balanceManager = mBalanceManager;

        AccountID fromId = mBankDatabase.insertAccount();
        AccountID toId = mBankDatabase.insertAccount();

        balanceManager.add(new BaseAccount(fromId));
        balanceManager.add(new BaseAccount(toId));

        balanceManager.transferMoney(mSuperId, fromId, 100);
        balanceManager.transferMoney(mSuperId, toId, 100);

        assertEquals(100, getBalance(fromId));
        assertEquals(100, getBalance(toId));

        assertFalse(balanceManager.transferMoney(fromId, toId, 200));

        assertEquals(100, getBalance(fromId));
        assertEquals(100, getBalance(toId));
    }

    private int getBalance(AccountID id) {
        return mBalanceManager.getBalanceList().get(id);
    }

    @Test
    public void updateModifiedCounterAfterAccountDelete() throws Exception {
        final AccountID id = mBankDatabase.insertAccount();
        mBalanceManager.add(new BaseAccount(id));

        assertModifiedCounterChange(new Runnable() {
            @Override
            public void run() {
                mBalanceManager.remove(id);
            }
        }, 1);
    }

    @Test
    public void updateModifiedCounterAfterAccountAdd() throws Exception {
        final AccountID id = mBankDatabase.insertAccount();
        assertModifiedCounterChange(new Runnable() {
            @Override
            public void run() {
                mBalanceManager.add(new BaseAccount(id));
            }
        }, 1);
    }

    @Test
    public void updateModifiedCounterAfterTransfer() throws Exception {
        final AccountID fromId = mBankDatabase.insertAccount();
        final AccountID toId = mBankDatabase.insertAccount();
        mBalanceManager.add(new BaseAccount(fromId));
        mBalanceManager.add(new BaseAccount(toId));
        mBalanceManager.transferMoney(mSuperId, fromId, 100);

        assertModifiedCounterChange(new Runnable() {
            @Override
            public void run() {
                assertFalse(mBalanceManager.transferMoney(toId, fromId, 100));
            }
        }, 0);

        assertModifiedCounterChange(new Runnable() {
            @Override
            public void run() {
                assertTrue(mBalanceManager.transferMoney(fromId, toId, 100));
            }
        }, 1);
    }

    private void assertModifiedCounterChange(Runnable runnable, int expected) {
        long balanceListModifiedCounter;
        balanceListModifiedCounter = mBalanceManager.getBalanceListModifiedCounter();
        runnable.run();
        assertEquals(expected, mBalanceManager.getBalanceListModifiedCounter() - balanceListModifiedCounter);
    }

}