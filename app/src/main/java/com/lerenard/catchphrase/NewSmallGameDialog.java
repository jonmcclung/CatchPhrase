package com.lerenard.catchphrase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by mc on 22-Jan-17.
 */

public class NewSmallGameDialog extends DialogFragment {

    private Listener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView =
                getActivity().getLayoutInflater().inflate(R.layout.new_small_game_dialog, null);

        final EditText passesAllowed =
                (EditText) dialogView.findViewById(R.id.small_game_passes_allowed_field);

        passesAllowed.setText(String.format(Locale.getDefault(), "%d",
                              getContext().getSharedPreferences(
                                      MainApplication.SHARED_PREFERENCES_FILENAME,
                                      Context.MODE_PRIVATE)
                                          .getInt(NewGameDialog.TEAM_ONE_DEFAULT_PASSES_ALLOWED, -1)));

        passesAllowed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finish(Integer.parseInt(passesAllowed.toString()));
                    return true;
                }
                return false;
            }
        });

        return new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Small Game")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(Integer.parseInt(passesAllowed.getText().toString()));
                    }
                })
                .create();
    }

    private void finish(int passes) {
        if (listener != null) {
            listener.onPassesAllowedSelected(passes);
            getContext().getSharedPreferences(
                    MainApplication.SHARED_PREFERENCES_FILENAME,
                    Context.MODE_PRIVATE)
                        .edit()
                        .putInt(NewGameDialog.TEAM_ONE_DEFAULT_PASSES_ALLOWED, passes)
                        .apply();
            dismiss();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onPassesAllowedSelected(int passesAllowed);
    }
}
