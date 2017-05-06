package com.yjy.banker.component.activity.baseActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.yjy.banker.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {


    private FragmentManager mFragmentManager;

    protected abstract Fragment getFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment;
        fragment = mFragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = getFragment();
            if (fragment == null) {
                finish();
                return;
            }
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    @Override
    public void onBackPressed() {
        Fragment fragment;
        fragment = mFragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment != null && fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        }
        super.onBackPressed();
    }
}
