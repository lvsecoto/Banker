package com.yjy.banker.bank.Persistence;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@MediumTest
public class BankDatabaseTest {

    private IBankDatabase bankDataBase;

    public BankDatabaseTest(Class<IBankDatabase> bankDataBaseClass) throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        bankDataBase = bankDataBaseClass.getConstructor(Context.class).newInstance(appContext);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {BankDatabase.class},
                {BankDatabaseMocker.class}
        });
    }

    @Before
    public void setUp() throws Exception {
        bankDataBase.reset();
    }

    @Test
    public void insertAccount() throws Exception {
        assertEquals(new AccountID(1), bankDataBase.insertAccount());
    }

    @Test
    public void updateAndGetBalance() throws Exception {
        final int balance = 100;
        AccountID id = bankDataBase.insertAccount();

        bankDataBase.updateBalance(id, balance);
        assertEquals(balance, bankDataBase.getBalance(id));
    }

    @Test
    public void updateAndGetProfile() throws Exception {
        AccountID id = bankDataBase.insertAccount();
        Profile profile = new Profile();

        profile.setName("hello");
        profile.setDescription("world");
        profile.setPhoto(UUID.randomUUID());

        bankDataBase.updateProfile(id, profile);

        assertEquals(profile, bankDataBase.getProfile(id));
    }
}