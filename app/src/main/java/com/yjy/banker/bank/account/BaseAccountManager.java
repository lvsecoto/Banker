package com.yjy.banker.bank.account;

import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.yjy.banker.bank.Persistence.IBankDatabase;
import com.yjy.banker.bank.Persistence.IOnLoadListener;

import java.util.UUID;

public class BaseAccountManager implements AccountManager {
    private AccountID mSupperAccountID;

    private IBankDatabase mBankDataBase;
    private BalanceManager mBalanceManager;
    private ProfileManager mProfileManager;

    private BaseAccountManager() {
        mBalanceManager = new BalanceManager() {
            @Override
            protected void onBalanceListUpdate(AccountID id, int balance) {
                super.onBalanceListUpdate(id, balance);
                mBankDataBase.updateBalance(id, balance);
            }
        };
        mProfileManager = new ProfileManager() {
            @Override
            protected void onProfileUpdate(AccountID accountID, Profile profile) {
                super.onProfileUpdate(accountID, profile);
                mBankDataBase.updateProfile(accountID, profile);
            }
        };
    }

    public static BaseAccountManager createFrom(IBankDatabase bankDataBase) {
        BaseAccountManager baseAccountManager = new BaseAccountManager();
        baseAccountManager.create(bankDataBase);
        return baseAccountManager;
    }

    /**
     * @return Return Null if failed to load from the database.
     */
    @Nullable
    public static BaseAccountManager loadFrom(IBankDatabase bankDatabase) {
        BaseAccountManager baseAccountManager = new BaseAccountManager();
        if (baseAccountManager.load(bankDatabase)) {
            return baseAccountManager;
        } else {
            return null;
        }
    }

    private void create(IBankDatabase bankDatabase) {
        mBankDataBase = bankDatabase;
        mBankDataBase.reset();

        mSupperAccountID = createSuperAccount();
    }

    private boolean load(IBankDatabase bankDataBase) {
        mBankDataBase = bankDataBase;
        return bankDataBase.load(new IOnLoadListener() {
            @Override
            public boolean onLoad(long id, long balance, String name, String description, String photoUUIDString) {
                if ((int) balance < 0) {
                    return false;
                }

                AccountID accountID = new AccountID(id);

                mBalanceManager.add(
                        getAccount(id, balance, accountID));
                mProfileManager.add(accountID);
                mProfileManager.add(
                        accountID,
                        getProfile(name, description, photoUUIDString));

                return true;
            }

            private Account getAccount(long id, long balance, AccountID accountID) {
                Account account;
                if (id == 1) {
                    account = new SuperAccount(accountID);
                } else {
                    account = new BaseAccount(accountID);
                }
                account.earn((int) balance);
                return account;
            }

            private Profile getProfile(String name, String description, String photoUUIDString) {
                Profile profile = new Profile();
                profile.setName(name);
                profile.setDescription(description);

                UUID photoUUID;
                try {
                    photoUUID = UUID.fromString(photoUUIDString);
                } catch (Exception e) {
                    photoUUID = null;
                }
                profile.setPhoto(photoUUID);
                return profile;
            }
        });

    }

    /**
     * The super account is a single instance.
     */
    @Override
    public AccountID applySupperAccount() {
        return mSupperAccountID;
    }

    @Override
    public AccountID applyAccount() {
        return createAccount(BaseAccount.class);
    }

    private AccountID createSuperAccount() {
        return createAccount(SuperAccount.class);
    }

    private AccountID createAccount(Class<? extends Account> account) {
        AccountID id = mBankDataBase.insertAccount();
        Account superAccount;

        try {
            superAccount = account.getConstructor(AccountID.class).newInstance(id);
            mBankDataBase.updateBalance(id, superAccount.getBalance());

            mBalanceManager.add(superAccount);
            mProfileManager.add(id);
        } catch (Exception e) {
            Logger.e(e, e.getMessage());
        }

        return id;
    }

    /**
     * Not support
     */
    @Override
    public boolean deleteAccount(AccountID accountID) throws IllegalAccountIDException {
        return false;
    }

    @Override
    public BalanceManager getBalanceManager() {
        return mBalanceManager;
    }

    @Override
    public ProfileManager getProfileManager() {
        return mProfileManager;
    }

}
