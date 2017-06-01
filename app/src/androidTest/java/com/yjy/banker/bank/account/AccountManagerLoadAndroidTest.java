package com.yjy.banker.bank.account;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import com.yjy.banker.bank.Persistence.BankDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class AccountManagerLoadAndroidTest {

    private Context mAppContext;

    @Before
    public void setUp() throws Exception {
        mAppContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void recoverDateByLoad() throws Exception {
        LinkedHashMap<AccountID, Integer> expectedBalanceList = initDatabase();
        BankDatabase bankDatabase = new BankDatabase(mAppContext);
        AccountManager baseAccountManager = AccountManager.loadFrom(bankDatabase);
        if (baseAccountManager == null) {
            fail();
        }

        assertEquals(
                expectedBalanceList,
                baseAccountManager.getBalanceManager().getBalanceList()
        );

    }

    private LinkedHashMap<AccountID, Integer> initDatabase() {
        BankDatabase bankDatabase = new BankDatabase(mAppContext);

        AccountManager baseAccountManager = AccountManager.createFrom(bankDatabase);

        AccountID superId = baseAccountManager.applySupperAccount();
        AccountID id = baseAccountManager.applyAccount();
        AccountID anotherId = baseAccountManager.applyAccount();
        baseAccountManager.getBalanceManager().transferMoney(superId, id, 100);
        baseAccountManager.getBalanceManager().transferMoney(superId, anotherId, 200);

        bankDatabase.close();
        return baseAccountManager.getBalanceManager().getBalanceList();
    }

    @Test
    public void failToLoadFromAnEmptyDatabase() throws Exception {
        BankDatabase bankDatabase = new BankDatabase(mAppContext);
        bankDatabase.reset();

        AccountManager baseAccountManager =
                AccountManager.loadFrom(bankDatabase);

        assertEquals(null, baseAccountManager);
    }

    @Test
    public void autoChangeTheFirstAccountToSuperAccountWhenLoad() throws Exception {
        BankDatabase bankDatabase = new BankDatabase(mAppContext);
        AccountID id = bankDatabase.insertAccount();
        bankDatabase.updateBalance(id, 300);

        AccountManager baseAccountManager =
                AccountManager.loadFrom(bankDatabase);

        if (baseAccountManager == null) {
            fail();
        }

        int superAccountBalance =
                baseAccountManager.getBalanceManager()
                        .getBalanceList().get(new AccountID(1));

        assertEquals(AbstractAccount.INFINITE, superAccountBalance);
    }
}
