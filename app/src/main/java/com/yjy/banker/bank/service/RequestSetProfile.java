package com.yjy.banker.bank.service;

import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.bank.account.Profile;

public class RequestSetProfile extends AbstractRequest {

    private final AccountID mAccountID;
    private final Profile mProfile;
    private boolean mResult;

    public RequestSetProfile(AccountID accountID, Profile profile) {
        mAccountID = accountID;
        mProfile = profile;
    }

    @Override
    public void execute() {
        mResult = getBankService().setProfile(mAccountID, mProfile);
    }

    /**
     * @return Return <b>FALSE</b> if there is no accountID to be set profile.
     */
    public boolean isSuccess() {
        return mResult;
    }

}
