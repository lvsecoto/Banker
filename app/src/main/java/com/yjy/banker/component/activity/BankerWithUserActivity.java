package com.yjy.banker.component.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.yjy.banker.R;
import com.yjy.banker.bank.account.AccountID;
import com.yjy.banker.component.activity.baseActivity.DepositorListFragment;
import com.yjy.banker.component.service.BankService;

public class BankerWithUserActivity extends AppCompatActivity implements View.OnClickListener {

    private BankerFragment mBankerFragment;
    private UserFragment mUserFragment;
    private FragmentManager mFragmentManager;

    private boolean mIsBankerMode;
    private FloatingActionButton mActionButton;

    public static void startActivity(Context context, AccountID accountID) {
        Intent intent = new Intent(context, BankerWithUserActivity.class);
        UserFragment.Arguments arguments = new UserFragment.Arguments(accountID, "127.0.0.1");
        intent.putExtra(UserFragment.EXTRA_ARGUMENTS, arguments);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_fragment);

        BankService.startService(this);

        prepareFragment();

        mActionButton = (FloatingActionButton) findViewById(R.id
                .switch_action);
        mActionButton.setOnClickListener(this);

        showIconOnActionButton(mIsBankerMode);
    }

    private void prepareFragment() {
        mIsBankerMode = true;
        mBankerFragment = BankerFragment.newInstance();
        DepositorListFragment.Arguments arguments = (UserFragment.Arguments)
                getIntent().getExtras().getSerializable(UserFragment.EXTRA_ARGUMENTS);
        mUserFragment = UserFragment.newInstance(arguments);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mBankerFragment)
                    .add(R.id.fragment_container, mUserFragment)
                    .show(mBankerFragment)
                    .hide(mUserFragment)
                    .commit();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_action:
                mIsBankerMode = !mIsBankerMode;
                showSwitchFragment(mIsBankerMode);
                showIconOnActionButton(mIsBankerMode);
                break;
        }
    }

    private void showSwitchFragment(boolean isBankerMode) {
        if (isBankerMode) {
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .show(mBankerFragment)
                    .hide(mUserFragment)
                    .commit();
        } else {
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right,
                            R.anim.slide_out_left)
                    .show(mUserFragment)
                    .hide(mBankerFragment)
                    .commit();
        }
    }

    private void showIconOnActionButton(boolean isBankerMode) {
        if (isBankerMode) {
            mActionButton.setImageResource(R.drawable.ic_bank_white_24dp);
        } else {
            mActionButton.setImageResource(R.drawable.ic_user_white_24dp);
        }
    }

}
