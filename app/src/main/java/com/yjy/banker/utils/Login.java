package com.yjy.banker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import com.yjy.banker.bank.Persistence.BankDatabase;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.AccountManager;
import com.yjy.banker.bank.account.BaseAccountManager;
import com.yjy.banker.bank.bank.Bank;
import com.yjy.banker.bank.bank.BaseBank;
import com.yjy.banker.component.service.BankService;

public class Login {

    private static final String PREF_MODE = "mode";
    private static final String PREF_SERVER_ADDRESS = "server_address";
    private static final String PREF_ACCOUNT_ID = "account_id";

    private static final String PREF_BANK_CREATE_TIME = "PREF_BANK_CREATE_TIME";
    private static final String PREF_BANK_DATABASE_CREATE_TIME = "PREF_BANK_DATABASE_CREATE_TIME";

    public static final int MODE_NULL = 0;
    public static final int MODE_USER = 1;
    public static final int MODE_BANK = 2;
    public static final int MODE_BANK_WITH_USER = 3;

    @SuppressLint("StaticFieldLeak")
    private static Login mLogin = null;
    private final SharedPreferences mSharePreference;
    private final Context mContext;
    private final Intent mBankServiceIntent;

    private Login(Context context) {
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context.getApplicationContext();
        mBankServiceIntent = new Intent(mContext, BankService.class);
    }

    public static Login newInstance(Context context) {
        if (mLogin == null) {
            mLogin = new Login(context);
        }
        return mLogin;
    }

    /**
     * @return Return Null if there is no server address has been set.
     */
    @Nullable
    public String getServerAddress() {
        return mSharePreference.getString(PREF_SERVER_ADDRESS, null);
    }

    private void setServerAddress(@Nullable String serverAddress) {
        mSharePreference.edit()
                .putString(PREF_SERVER_ADDRESS, serverAddress)
                .apply();
    }

    /**
     * @return Return Null if there is no account ID has been set.
     */
    @Nullable
    public AccountID getAccountID() {
        if (mSharePreference.contains(PREF_ACCOUNT_ID)) {
            long id = mSharePreference.getLong(PREF_ACCOUNT_ID, 0);
            return new AccountID(id);
        }
        return null;
    }

    private void setAccountID(@Nullable AccountID accountID) {
        if (accountID == null) {
            mSharePreference.edit()
                    .remove(PREF_ACCOUNT_ID)
                    .apply();
            return;
        }

        long id = accountID.getID();
        mSharePreference.edit()
                .putLong(PREF_ACCOUNT_ID, id)
                .apply();
    }

    /**
     * Get the login mode
     */
    public int getLoginMode() {
        return mSharePreference.getInt(PREF_MODE, MODE_NULL);
    }

    private void setLoginMode(int mode) {
        mSharePreference.edit()
                .putInt(PREF_MODE, mode)
                .apply();
    }

    /**
     * Login with User mode and save the passing parameter accountID and serverAddress to preference.
     */
    public void loginWithUser(AccountID accountID, String serverAddress) {
        setLoginMode(MODE_USER);
        setAccountID(accountID);
        setServerAddress(serverAddress);
    }

    /**
     * Login with bank mode and start bankService.
     */
    public void loginWithBanker() {
        setLoginMode(MODE_BANK);
        setAccountID(null);
        setServerAddress(null);
        startBankService();
    }

    /**
     * Login with bank and user mode, and save the passing accountID parameter and start BankService.
     */
    public void loginWithBankerAndUser(AccountID accountID) {
        setLoginMode(MODE_BANK_WITH_USER);
        setAccountID(accountID);
        setServerAddress(null);
        startBankService();
    }

    public void logout() {
        if (getLoginMode() == MODE_BANK || getLoginMode() == MODE_BANK_WITH_USER) {
            stopBankService();
        }
        setLoginMode(MODE_NULL);
        setAccountID(null);
        setServerAddress(null);
    }

    private void startBankService() {
        mContext.startService(mBankServiceIntent);
    }

    private void stopBankService() {
        mContext.stopService(mBankServiceIntent);
    }

    /**
     * Set the server address and write it down into Shared Preference, according to current login
     * mode.
     * <ul>
     * <li><b>User mode</b>: Set to the parameter of serverAddress</li>
     * <li><b>Bank mode</b>: Set to null (delete it)</li>
     * <li><b>Bank with user mode</b>: Set to localhost :"127.0.0.1"</li>
     * </ul>
     */
    public void setupServerAddress(String serverAddress) {
        switch (getLoginMode()) {
            case MODE_USER:
                setServerAddress(serverAddress);
                break;
            case MODE_BANK:
                setServerAddress(null);
                break;
            case MODE_BANK_WITH_USER:
                setServerAddress("127.0.0.1");
                break;
        }
    }

    public Bank getBank() {
        AccountManager accountManager;

        if (isBankDataBaseIsOld()) {
            accountManager = BaseAccountManager.createFrom(
                    new BankDatabase(mContext)
            );
            updateBankDataBaseCreateDate();
        } else {
            accountManager = BaseAccountManager.loadFrom(
                    new BankDatabase(mContext)
            );
        }

        if (accountManager == null) {
            return null;
        }

        return new BaseBank(accountManager);
    }

    private boolean isBankDataBaseIsOld() {
        long bankCreateTime = mSharePreference.getLong(PREF_BANK_CREATE_TIME, 0);
        long bankDatabaseCreateTime = mSharePreference.getLong(PREF_BANK_DATABASE_CREATE_TIME, 0);

        return bankDatabaseCreateTime < bankCreateTime;
    }

    private void updateBankCreateDate() {
        mSharePreference.edit()
                .putLong(PREF_BANK_CREATE_TIME, System.currentTimeMillis())
                .apply();
    }

    private void updateBankDataBaseCreateDate() {
        mSharePreference.edit()
                .putLong(PREF_BANK_DATABASE_CREATE_TIME, System.currentTimeMillis())
                .apply();
    }

    public void initBank() {
        stopBankService();
        updateBankCreateDate();
    }
}
