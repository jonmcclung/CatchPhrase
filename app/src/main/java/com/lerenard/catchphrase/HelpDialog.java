package com.lerenard.catchphrase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by mc on 30-Jan-17.
 */

public class HelpDialog extends DialogFragment {
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.help_title)
                .setMessage(R.string.small_game_help_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.done();
                        }
                    }
                })
                .create();
    }

    public interface Listener {
        void done();
    }
}
