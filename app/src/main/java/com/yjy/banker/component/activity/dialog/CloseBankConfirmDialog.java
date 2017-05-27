package com.yjy.banker.component.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.yjy.banker.R;

public class CloseBankConfirmDialog extends DialogFragment {
    private static final String KEY_CODE = "KEY_CODE";
    private static final String RESULT_IS_CODE_CORRECT = "RESULT_IS_CODE_CORRECT";
    private String mCode;

    private EditText mInputCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCode = getArguments().getString(KEY_CODE);

        View view = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_delete_bank_confirm, null
        );

        TextView correctCodeTextView = (TextView) view.findViewById(R.id.correct_code);
        correctCodeTextView.setText(mCode);

        mInputCode = (EditText) view.findViewById(R.id.input_code);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(isCodeCorrect());
                    }
                }).create();
    }

    public boolean isCodeCorrect() {
        return mInputCode.getText().toString().equals(mCode);
    }

    private void sendResult(boolean isCodeCorrect) {
        Intent data = new Intent();
        data.putExtra(RESULT_IS_CODE_CORRECT, isCodeCorrect);

        getTargetFragment().onActivityResult(
                getTargetRequestCode(), Activity.RESULT_OK, data
        );
    }

    public static CloseBankConfirmDialog newInstance(String code,
                                                     Fragment targetFragment, int requestCode) {
        Bundle args = new Bundle();
        args.putString(KEY_CODE, code);

        CloseBankConfirmDialog fragment = new CloseBankConfirmDialog();
        fragment.setArguments(args);
        fragment.setTargetFragment(targetFragment, requestCode);

        return fragment;
    }

    public void show(Context context) {
        FragmentManager fragmentManager =
                ((AppCompatActivity) context).getSupportFragmentManager();

        this.show(fragmentManager, this.getClass().getName());
    }

    public static boolean isCodeCorrect(Intent intent) {
        return intent.getBooleanExtra(RESULT_IS_CODE_CORRECT, false);
    }
}
