package com.lerenard.catchphrase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by mc on 17-Jan-17.
 */

public class ModifyScoreDialog extends DialogFragment {

    private static final String SCORES_KEY = "SCORES_KEY";
    private static final String TAG = "MODIFY_SCORE_DIALOG";
    private int[] scores;
    private ModifyScoreListener listener;

    public static ModifyScoreDialog newInstance(int[] scores) {
        ModifyScoreDialog res = new ModifyScoreDialog();
        Bundle args = new Bundle();
        args.putIntArray(SCORES_KEY, scores);
        res.setArguments(args);
        return res;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        restoreArguments(args);
    }

    private void restoreArguments(Bundle args) {
        scores = args.getIntArray(SCORES_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreArguments(savedInstanceState);
        }
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.modify_scores_dialog, null);
        final TextView teamOneScoreView =
                (TextView) dialogView.findViewById(R.id.team_one_score_view);
        final TextView teamTwoScoreView =
                (TextView) dialogView.findViewById(R.id.team_two_score_view);
        TextView teamOneIncrement = (TextView) dialogView.findViewById(R.id.team_one_increment);
        TextView teamTwoIncrement = (TextView) dialogView.findViewById(R.id.team_two_increment);
        TextView teamOneDecrement = (TextView) dialogView.findViewById(R.id.team_one_decrement);
        TextView teamTwoDecrement = (TextView) dialogView.findViewById(R.id.team_two_decrement);

        teamOneScoreView.setText(String.valueOf(scores[0]));
        teamTwoScoreView.setText(String.valueOf(scores[1]));

        teamOneIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.tentativeAdjustment(0, 1);
                }
                adjustPointsOnTextView(teamOneScoreView, 1);
            }
        });
        teamTwoIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.tentativeAdjustment(1, 1);
                }
                adjustPointsOnTextView(teamTwoScoreView, 1);
            }
        });
        teamOneDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.tentativeAdjustment(0, -1);
                }
                adjustPointsOnTextView(teamOneScoreView, -1);
            }
        });
        teamTwoDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.tentativeAdjustment(1, -1);
                }
                adjustPointsOnTextView(teamTwoScoreView, -1);
            }
        });

        setCancelable(false);
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.modify_scores_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onScoresAdjusted(new int[]{
                                Integer.parseInt(teamOneScoreView.getText().toString()),
                                Integer.parseInt(teamTwoScoreView.getText().toString())});
                        dismiss();
                    }
                })
                .setNegativeButton(
                        getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null) {
                                    listener.onCancel();
                                    Log.d(TAG, "cancelling");
                                }
                                dismiss();
                            }
                        })
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCORES_KEY, scores);
    }

    private static void adjustPointsOnTextView(TextView view, int howMuch) {
        view.setText(String.format(
                Locale.getDefault(),
                "%d",
                howMuch + Integer.parseInt(view.getText().toString())));
    }

    public void setListener(ModifyScoreListener listener) {
        this.listener = listener;
    }
}
