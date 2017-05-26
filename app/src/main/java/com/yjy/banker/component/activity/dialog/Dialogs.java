package com.yjy.banker.component.activity.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import com.yjy.banker.R;

public class Dialogs {
    private final Fragment mDepositorFragment;
    private AlertDialog mMessageDialog;
    private ProgressDialog mWaitDialog;
    private AlertDialog mConfirmDialog;

    public Dialogs(Fragment mDepositorFragment) {
        this.mDepositorFragment = mDepositorFragment;
    }

    public void showMessageDialog(String message) {
        dismissMessageDialog();
        mMessageDialog = new AlertDialog.Builder(mDepositorFragment.getContext())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void showConfirmDialog(String message, DialogInterface.OnClickListener listener) {
        dismissConfirmMessageDialog();
        mConfirmDialog = new AlertDialog.Builder(mDepositorFragment.getContext())
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show();
    }

    public void showConfirmDialog(@StringRes int messageID, DialogInterface.OnClickListener listener,
                                  boolean isCancelable) {
        dismissConfirmMessageDialog();
        mConfirmDialog = new AlertDialog.Builder(mDepositorFragment.getContext())
                .setMessage(messageID)
                .setCancelable(isCancelable)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show();
    }

    /**
     * Show a cancelable confirm dialog
     */
    public void showConfirmDialog(@StringRes int messageID, DialogInterface.OnClickListener listener) {
        showConfirmDialog(messageID, listener, true);
    }

    public void dismissConfirmMessageDialog() {
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
    }

    public void showMessageDialog(@StringRes int messageID) {
        dismissMessageDialog();
        mMessageDialog = new AlertDialog.Builder(mDepositorFragment.getContext())
                .setMessage(messageID)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void dismissMessageDialog() {
        if (mMessageDialog != null) {
            mMessageDialog.dismiss();
        }
    }

    public void showPleaseWaitDialog() {
        dismissPleaseWaitDialog();
        mWaitDialog = ProgressDialog.show(
                mDepositorFragment.getContext(), "",
                mDepositorFragment.getContext().getString(R.string.alert_please_wait),
                true,
                false);
    }

    public void dismissPleaseWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }

    public void dismissAll() {
        dismissMessageDialog();
        dismissPleaseWaitDialog();
    }
}