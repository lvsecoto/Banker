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
import android.view.View;
import android.widget.EditText;
import com.yjy.banker.R;

public class MoneyPickerDialog extends DialogFragment {
    private static final String KEY_TITLE = "KEY_TITLE";
    private static final String KEY_MONEY = "KEY_MONEY";

    private EditText mMoneyEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle argument = getArguments();
        String title = argument.getString(KEY_TITLE);

        View view =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_money_picker,
                        null
                );
        mMoneyEditText = (EditText) view.findViewById(R.id.money);
        mMoneyEditText.requestFocus();

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
     * @param title          Show as dialog title.
     * @param targetFragment Set the fragment to be invoked after user finish picking money.
     *                       <p>
     *                       Usually set to the fragment which create this dialog.
     *                       <p>
     *                       Use {@link #getResultMoney(Intent)} to get money the user pick.
     * @param requestCode    Request code for parameter of <b>targetFragment.onActivityResult</b>.
     */
    public static MoneyPickerDialog newInstance(String title,
                                                Fragment targetFragment, int requestCode) {
        MoneyPickerDialog dialog = new MoneyPickerDialog();

        Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE, title);
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
     * @param intent Intent receive from <b>targetFragment.onActivityResult</b>.
     * @return Null if user hasn't input a legal money.
     */
    public static Integer getResultMoney(Intent intent) {
        if (!intent.hasExtra(KEY_MONEY)) {
            return null;
        }
        return (Integer) intent.getSerializableExtra(KEY_MONEY);
    }
}
