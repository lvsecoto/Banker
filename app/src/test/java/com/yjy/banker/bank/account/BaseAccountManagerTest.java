package com.yjy.banker.bank.account;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class BaseAccountManagerTest {

    private AccountManager accountManager;
    private AccountID superAccountID;

    @Before
    public void setUp() throws Exception {
        accountManager = new BaseAccountManager();
        superAccountID = accountManager.applySupperAccount();
    }

    @Test
    public void newAccountHasNoBalance() throws Exception {
        AccountID accountID1 = accountManager.applyAccount();
        int balance = accountManager.getBalance(accountID1);
        assertEquals(0, balance);
    }

    @Test
    public void onlyOneIDForApplyForSupperAccount() throws Exception {
        AccountID firstTimeID = accountManager.applySupperAccount();
        AccountID secondTimeID = accountManager.applySupperAccount();

        assertEquals(firstTimeID, secondTimeID);
    }

    @Test
    public void useNoneExistentAccountIDCauseIllegalAccountIDException() throws Exception {
        AccountID noneExistentAccountID = new AccountID(
                accountManager.applySupperAccount().getID() + 1
        );

        try {
            accountManager.getBalance(noneExistentAccountID);
            fail("Here should throws an exception of IllegalAccountIDException");
        } catch (IllegalAccountIDException e) {
            assertEquals("There is no BaseAccount with this AccountID.", e.getMessage());
        }

    }

    @Test
    public void accountCanEarnMoneyFromSuperAccount() throws Exception {
        AccountID accountID = createAccountWithBalance(50);
        assertTrue(accountManager.transferMoney(superAccountID, accountID, 100));
        assertEquals(150, accountManager.getBalance(accountID));
    }

    @Test
    public void transferMoneyFromOneAccountToAnotherAccount() throws Exception {
        AccountID toAccountID = createAccountWithBalance(100);
        AccountID fromAccountID = createAccountWithBalance(100);

        assertTrue(accountManager.transferMoney(fromAccountID, toAccountID, 50));
        assertEquals(50, accountManager.getBalance(fromAccountID));
        assertEquals(150, accountManager.getBalance(toAccountID));
    }

    @Test
    public void cantTransferIfAccountHasNotEnoughMoney() throws Exception {
        AccountID fromAccountID = createAccountWithBalance(100);
        AccountID toAccountID = createAccountWithBalance(100);
        assertEquals(100, accountManager.getBalance(fromAccountID));
        assertEquals(100, accountManager.getBalance(toAccountID));

        assertFalse(accountManager.transferMoney(fromAccountID, toAccountID, 200));

        assertEquals(100, accountManager.getBalance(fromAccountID));
        assertEquals(100, accountManager.getBalance(toAccountID));
    }

    @Test
    public void cantTransferIfAccountHasTooMuchMoney() throws Exception {
        AccountID fromAccountID = accountManager.applyAccount();
        AccountID toAccountID = accountManager.applyAccount();
        int toAccountCapacity = accountManager.getCapacity(toAccountID);

        accountManager.transferMoney(superAccountID, fromAccountID, 100);
        accountManager.transferMoney(superAccountID, toAccountID, toAccountCapacity);
        assertFalse(accountManager.transferMoney(fromAccountID, toAccountID, 100));

        assertEquals(100, accountManager.getBalance(fromAccountID));
        assertEquals(toAccountCapacity, accountManager.getBalance(toAccountID));

    }

    @Test
    public void checkIfAccountExistsWithTheAccountID() throws Exception {
        AccountID existentAccountID = accountManager.applyAccount();
        AccountID noneExistentAccountID = new AccountID(
                existentAccountID.getID() + 10
        );
        assertTrue(accountManager.isExistent(existentAccountID));
        assertFalse(accountManager.isExistent(noneExistentAccountID));
    }

    @Test
    public void deleteAccountFromAccountOnlyIfItsNoBalance() throws Exception {
        AccountID noBalanceID = createAccountWithBalance(0);
        assertTrue(accountManager.deleteAccount(noBalanceID));
        assertFalse(accountManager.isExistent(noBalanceID));
    }

    @Test
    public void canNotDeleteAccountIfItsHasBalance() throws Exception {
        AccountID someBalanceID = createAccountWithBalance(100);
        assertFalse(accountManager.deleteAccount(someBalanceID));
        assertTrue(accountManager.isExistent(someBalanceID));
    }

    private AccountID createAccountWithBalance(int money) throws IllegalAccountIDException {
        AccountID fromAccountID = accountManager.applyAccount();
        accountManager.transferMoney(superAccountID, fromAccountID, money);
        return fromAccountID;
    }

    @Test
    public void getAccountIDList() throws Exception {
        AccountID newAccountID = accountManager.applyAccount();
        Set<AccountID> accountIDList = new HashSet<>();

        accountIDList.add(superAccountID);
        accountIDList.add(newAccountID);

        assertEquals(accountIDList, accountManager.getAccountIDList());

    }

    @Test
    public void updateBalancesListAfterApplyAndTransferAndDelete() throws Exception {
        Map<AccountID, Integer> balanceList;
        Map<AccountID, Integer> expectedBalanceList;

        AccountID accountID1;
        AccountID accountID2;
        AccountID accountID3;

        //apply
        accountID1 = accountManager.applyAccount();
        accountID2 = accountManager.applyAccount();
        accountID3 = accountManager.applyAccount();

        balanceList = accountManager.getBalanceList();

        expectedBalanceList = new HashMap<>();
        expectedBalanceList.put(superAccountID, accountManager.getCapacity(superAccountID));
        expectedBalanceList.put(accountID1, 0);
        expectedBalanceList.put(accountID2, 0);
        expectedBalanceList.put(accountID3, 0);

        assertEquals(expectedBalanceList, balanceList);

        //transfer
        accountManager.transferMoney(superAccountID, accountID1, 100);
        accountManager.transferMoney(superAccountID, accountID2, 200);
        accountManager.transferMoney(superAccountID, accountID3, 0);

        balanceList = accountManager.getBalanceList();

        expectedBalanceList = new HashMap<>();
        expectedBalanceList.put(superAccountID, accountManager.getCapacity(superAccountID));
        expectedBalanceList.put(accountID1, 100);
        expectedBalanceList.put(accountID2, 200);
        expectedBalanceList.put(accountID3, 0);

        assertEquals(expectedBalanceList, balanceList);

        //delete
        accountManager.deleteAccount(accountID3);
        balanceList = accountManager.getBalanceList();

        expectedBalanceList = new HashMap<>();
        expectedBalanceList.put(superAccountID, accountManager.getCapacity(superAccountID));
        expectedBalanceList.put(accountID1, 100);
        expectedBalanceList.put(accountID2, 200);

        assertEquals(expectedBalanceList, balanceList);
    }

    @Test
    public void updateModifiedCounterAfterAccountAdded() throws Exception {
        long accountIDListCounterBefore =
                accountManager.getAccountIDListModifiedCounter();
        long balanceListCounterBefore =
                accountManager.getBalanceListModifiedCounter();
        accountManager.applyAccount();
        assertEquals(1, accountManager.getAccountIDListModifiedCounter() - accountIDListCounterBefore);
        assertEquals(1, accountManager.getBalanceListModifiedCounter() - balanceListCounterBefore);
    }

    @Test
    public void updateModifiedCounterAfterAccountDeleted() throws Exception {
        long accountIDListCounterBefore =
                accountManager.getAccountIDListModifiedCounter();
        long balanceListCounterBefore =
                accountManager.getBalanceListModifiedCounter();
        AccountID accountID = accountManager.applyAccount();
        accountManager.deleteAccount(accountID);
        assertEquals(2, accountManager.getAccountIDListModifiedCounter() - accountIDListCounterBefore);
        assertEquals(2, accountManager.getBalanceListModifiedCounter() - balanceListCounterBefore);
    }

    @Test
    public void updateModifiedCounterAfterTransfer() throws Exception {
        AccountID fromAccountID = accountManager.applyAccount();
        assertEquals(2, accountManager.getBalanceListModifiedCounter());

        AccountID toAccountID = accountManager.applyAccount();
        assertEquals(3, accountManager.getBalanceListModifiedCounter());

        assertFalse(accountManager.transferMoney(fromAccountID, toAccountID, 100));
        assertEquals(3, accountManager.getBalanceListModifiedCounter());

        assertTrue(accountManager.transferMoney(superAccountID, fromAccountID, 100));
        assertEquals(5, accountManager.getBalanceListModifiedCounter());
    }

    @Test
    public void addProfileToManager() throws Exception {
        AccountID accountID = accountManager.applyAccount();
        Profile profile = new Profile();
        accountManager.setProfile(accountID, profile);

        Map<AccountID, Profile> expectedProfileList = new HashMap<>();
        expectedProfileList.put(accountID, profile);

        assertEquals(expectedProfileList, accountManager.getProfileList());
    }

    @Test
    public void failToAddProfileIfTheAccountIDDoesNotContainInManager() throws Exception {
        AccountID accountID = new AccountID(100);
        Profile profile = new Profile();
        assertFalse(accountManager.setProfile(accountID, profile));

        Map<AccountID, Profile> expectedProfileList = new HashMap<>();

        assertEquals(expectedProfileList, accountManager.getProfileList());
    }

    @Test
    public void deleteAccountAlsoDeleteProfile() throws Exception {
        AccountID accountID = accountManager.applyAccount();
        Profile profile = new Profile();
        accountManager.setProfile(accountID, profile);

        Map<AccountID, Profile> expectedProfileList = new HashMap<>();
        expectedProfileList.put(accountID, profile);

        assertEquals(expectedProfileList, accountManager.getProfileList());

        accountManager.deleteAccount(accountID);
        expectedProfileList.remove(accountID);

        assertEquals(expectedProfileList, accountManager.getProfileList());
    }

    @Test
    public void updateModifiedCounterAfterAccountInfoChanged() throws Exception {
        AccountID accountID = accountManager.applyAccount();

        long initModifiedCounter = accountManager.getProfileListModifiedCounter();

        Profile profile = new Profile();
        accountManager.setProfile(accountID, profile);
        assertEquals(1, accountManager.getProfileListModifiedCounter() - initModifiedCounter);

        accountManager.deleteAccount(accountID);
        assertEquals(2, accountManager.getProfileListModifiedCounter() - initModifiedCounter);
    }
}