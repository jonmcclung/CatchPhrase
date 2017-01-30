package com.lerenard.catchphrase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.Locale;

/**
 * Created by mc on 30-Jan-17.
 */

public class NextRoundDialog extends DialogFragment {
    public static final String IS_NEXT = "IS_NEXT";
    private boolean isNext;

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        restoreArguments(args);
    }

    private void restoreArguments(Bundle args) {
        isNext = args.getBoolean(IS_NEXT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreArguments(savedInstanceState);
        }
        setCancelable(false);
        return new AlertDialog.Builder(getContext())
                .setTitle(String.format(
                        Locale.getDefault(),
                        getString(R.string.confirm_next_round_title),
                        isNext ? getString(R.string.confirm_next_round_title_is_next)
                               : getString(R.string.confirm_next_round_title_is_not_next)))
                .setMessage(String.format(
                        Locale.getDefault(),
                        getString(R.string.confirm_next_round_message),
                        getString(R.string.ok),
                        isNext ? getString(R.string.confirm_next_round_is_next)
                               : getString(
                                       R.string.confirm_next_round_is_not_next)))
                .setPositiveButton(
                        R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                if (listener != null) {
                                    listener.nextRound();
                                }
                            }
                        })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.quit();
                        }
                    }
                }).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_NEXT, isNext);
    }

    public interface Listener {
        void nextRound();

        void quit();
    }
}
