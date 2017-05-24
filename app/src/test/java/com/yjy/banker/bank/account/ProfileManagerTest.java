package com.yjy.banker.bank.account;

import com.yjy.banker.bank.Persistence.BankDatabaseMocker;
import com.yjy.banker.bank.Persistence.IBankDatabase;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProfileManagerTest {

    private ProfileManager mProfileManager;
    private IBankDatabase mBankDatabase;

    @Before
    public void setUp() throws Exception {
        mProfileManager = new ProfileManager();
        mBankDatabase = new BankDatabaseMocker();
    }

    @Test
    public void setProfile() throws Exception {
        AccountID accountID = mBankDatabase.insertAccount();
        Profile profile = new Profile();
        profile.setName("hello");
        profile.setDescription("world");
        profile.setPhoto(UUID.randomUUID());

        mProfileManager.add(accountID);
        mProfileManager.setProfile(accountID, profile);

        assertEquals(1, mProfileManager.getProfileList().size());
        assertEquals(profile, mProfileManager.getProfileList().get(accountID));
    }

    @Test
    public void failToSetProfileToANonExistentAccountID() throws Exception {
        assertFalse(mProfileManager.setProfile(new AccountID(100), new Profile()));
    }

    @Test
    public void listModifiedCounterChangeAfterAdd() throws Exception {
        final AccountID accountID = mBankDatabase.insertAccount();
        assertModifiedCounter(new Runnable() {
            public void run() {
                mProfileManager.add(accountID);
            }
        }, 1);

        assertModifiedCounter(new Runnable() {
            @Override
            public void run() {
                mProfileManager.remove(accountID);
            }
        }, 1);
    }

    private void assertModifiedCounter(Runnable runnable, int expectedChange) {
        long modifiedCounter = mProfileManager.getProfileListModifiedCounter();
        runnable.run();
        assertEquals(expectedChange, mProfileManager.getProfileListModifiedCounter() - modifiedCounter);
    }

}