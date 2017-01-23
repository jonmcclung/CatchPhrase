package com.lerenard.catchphrase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

/**
 * Created by mc on 22-Jan-17.
 */

public class EndRoundDialog extends DialogFragment {
    private Listener listener;
    private int successfulColor, unsuccessfulColor;
    public static final String SUCCESSFUL_COLOR = "SUCCESSFUL_COLOR", UNSUCCESSFUL_COLOR = "UNSUCCESSFUL_COLOR";

    public static EndRoundDialog newInstance(int successfulColor, int unsuccessfulColor) {
        EndRoundDialog dialog = new EndRoundDialog();
        dialog.successfulColor = successfulColor;
        dialog.unsuccessfulColor = unsuccessfulColor;
        return dialog;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        restoreArguments(args);
    }

    private void restoreArguments(Bundle args) {
        successfulColor = args.getInt(SUCCESSFUL_COLOR);
        unsuccessfulColor = args.getInt(UNSUCCESSFUL_COLOR);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SUCCESSFUL_COLOR, successfulColor);
        outState.putInt(UNSUCCESSFUL_COLOR, unsuccessfulColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreArguments(savedInstanceState);
        }
        View dialogView =
                getActivity().getLayoutInflater().inflate(R.layout.end_round_dialog, null);

        Button successfulSteal = (Button) dialogView.findViewById(R.id.successful_steal_button);
        Button unsuccessfulSteal = (Button) dialogView.findViewById(R.id.unsuccessful_steal_button);
        Button passing = (Button) dialogView.findViewById(R.id.passing_button);

        successfulSteal.setBackgroundColor(successfulColor);
        unsuccessfulSteal.setBackgroundColor(unsuccessfulColor);
        passing.setBackgroundColor(unsuccessfulColor);

        successfulSteal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChoiceSelected(Choices.SUCCESSFUL_STEAL);
                    dismiss();
                }
            }
        });

        unsuccessfulSteal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChoiceSelected(Choices.UNSUCCESSFUL_STEAL);
                    dismiss();
                }
            }
        });

        passing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChoiceSelected(Choices.PASSING);
                    dismiss();
                }
            }
        });
        setCancelable(false);
        return new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle(R.string.end_of_round_title).create();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    enum Choices {
        SUCCESSFUL_STEAL, UNSUCCESSFUL_STEAL, PASSING
    }

    interface Listener {
        void onChoiceSelected(Choices choice);
    }
}
