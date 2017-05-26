package com.yjy.banker.component.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yjy.banker.R;
import com.yjy.banker.bank.account.AbstractAccount;

public class MoneyPickerDialog extends DialogFragment {
    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_FROM_BALANCE = "KEY_FROM_BALANCE";
    private static final String KEY_TO_BALANCE = "KEY_TO_BALANCE";
    private static final String KEY_MONEY = "KEY_MONEY";

    private EditText mMoneyEditText;

    private int mFromBalance = 0;
    private int mToBalance = 0;
    private TextView mFromBalanceTextView;
    private TextView mToBalanceTextView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle argument = getArguments();
        String title = argument.getString(KEY_TITLE);
        mFromBalance = argument.getInt(KEY_FROM_BALANCE, 0);
        mToBalance = argument.getInt(KEY_TO_BALANCE, 0);

        View view =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_money_picker,
                        null
                );
        mMoneyEditText = (EditText) view.findViewById(R.id.money);
        mMoneyEditText.requestFocus();
        mMoneyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showBalance(getMoneyFromView());
            }

        });

        mFromBalanceTextView = (TextView) view.findViewById(R.id.from_balance);
        mToBalanceTextView = (TextView) view.findViewById(R.id.to_balance);
        showBalance(0);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(getMoneyFromView());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                ;
    }

    private void sendResult(Integer money) {
        Intent intent = new Intent();
        intent.putExtra(KEY_MONEY, money);

        Fragment targetFragment = getTargetFragment();
        targetFragment.onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                intent
        );
    }

    /**
     * @return Return null if there is no legal money user input in the view.
     */
    @Nullable
    private Integer getMoneyFromView() {
        String moneyString = mMoneyEditText.getText().toString();
        Integer money;
        try {
            money = Integer.valueOf(moneyString);
        } catch (NumberFormatException e) {
            money = null;
        }
        return money;
    }

    /**
     * Create an money pick dialog.
     *
     * @param title
     *         Show as dialog title.
     * @param targetFragment
     *         Set the fragment to be invoked after user finish picking money.
     *         <p>
     *         Usually set to the fragment which create this dialog.
     *         <p>
     *         Use {@link #getResultMoney(Intent)} to get money the user pick.
     * @param requestCode
     *         Request code for parameter of <b>targetFragment.onActivityResult</b>.
     */
    public static MoneyPickerDialog newInstance(String title,
                                                int fromBalance,
                                                int toBalance,
                                                Fragment targetFragment, int requestCode) {
        MoneyPickerDialog dialog = new MoneyPickerDialog();

        Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE, title);
        arguments.putInt(KEY_FROM_BALANCE, fromBalance);
        arguments.putInt(KEY_TO_BALANCE, toBalance);
        dialog.setArguments(arguments);
        dialog.setTargetFragment(targetFragment, requestCode);

        return dialog;
    }

    /**
     * Show the Money Picker Dialog
     */
    public void show(Context context) {
        FragmentManager fragmentManager =
                ((AppCompatActivity) context).getSupportFragmentManager();
        this.show(fragmentManager, MoneyPickerDialog.class.getName());
    }

    /**
     * @param intent
     *         Intent receive from <b>targetFragment.onActivityResult</b>.
     *
     * @return Null if user hasn't input a legal money.
     */
    public static Integer getResultMoney(Intent intent) {
        if (!intent.hasExtra(KEY_MONEY)) {
            return null;
        }
        return (Integer) intent.getSerializableExtra(KEY_MONEY);
    }

    private void showBalance(Integer money) {

        if (money == null) {
            if (mFromBalance == AbstractAccount.INFINITE) {
                mFromBalanceTextView.setText(
                        getString(R.string.depositor_infinite_balance)
                );
            } else {
                mFromBalanceTextView.setText(
                        getString(R.string.depositor_balance, mFromBalance)
                );
            }

            mFromBalanceTextView.setTextColor(
                    getResources().getColor(R.color.colorDanger)
            );

            if (mToBalance == AbstractAccount.INFINITE) {
                mToBalanceTextView.setText(
                        getString(R.string.depositor_infinite_balance)
                );
            } else {
                mToBalanceTextView.setText(
                        getString(R.string.depositor_balance, mToBalance)
                );
            }

            mToBalanceTextView.setTextColor(
                    getResources().getColor(R.color.colorDanger)
            );
            return;
        }

        if (mFromBalance == AbstractAccount.INFINITE) {
            mFromBalanceTextView.setText(
                    getString(R.string.depositor_infinite_balance)
            );
        } else {
            int payResult = mFromBalance - money;

            mFromBalanceTextView.setText(
                    getString(R.string.depositor_balance, payResult)
            );

            if (payResult < 0) {
                mFromBalanceTextView.setTextColor(
                        getResources().getColor(R.color.colorDanger)
                );
            } else {
                mFromBalanceTextView.setTextColor(
                        getResources().getColor(R.color.colorPrimary)
                );
            }
        }

        if (mToBalance == AbstractAccount.INFINITE) {
            mToBalanceTextView.setText(
                    getString(R.string.depositor_infinite_balance)
            );
        } else {

            long earnResult = mToBalance + money;

            mToBalanceTextView.setText(
                    getString(R.string.depositor_balance, earnResult)
            );
            if (earnResult > AbstractAccount.INFINITE) {
                mToBalanceTextView.setTextColor(
                        getResources().getColor(R.color.colorDanger)
                );
            } else {
                mToBalanceTextView.setTextColor(
                        getResources().getColor(R.color.colorAccent)
                );
            }
        }


    }
}
