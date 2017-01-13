package com.lerenard.catchphrase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by mc on 11-Jan-17.
 */

public class NewGameDialog extends DialogFragment {

    private static final String TEAM_ONE_DEFAULT_NAME = "TEAM_ONE_DEFAULT_NAME",
            TEAM_TWO_DEFAULT_NAME = "TEAM_TWO_DEFAULT_NAME", TEAM_ONE_DEFAULT_PASSES_ALLOWED =
            "TEAM_ONE_DEFAULT_PASSES_ALLOWED", TEAM_TWO_DEFAULT_PASSES_ALLOWED =
            "TEAM_TWO_DEFAULT_PASSES_ALLOWED", DEFAULT_GOAL_SCORE = "DEFAULT_GOAL_SCORE";
    private NewGameDialogListener listener;
    private EditText teamOneNameField, teamTwoNameField, teamOnePassesAllowed, teamTwoPassesAllowed,
            goalScoreField;

    public void setListener(NewGameDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        restoreArguments(args);
    }

    private void restoreArguments(Bundle args) {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreArguments(savedInstanceState);
        }

        SharedPreferences preferences = getContext().getSharedPreferences(
                MainApplication.SHARED_PREFERENCES_FILENAME,
                Context.MODE_PRIVATE);
        String teamOneDefaultName = preferences
                .getString(TEAM_ONE_DEFAULT_NAME, getString(R.string.team_one_default_name));
        String teamTwoDefaultName = preferences
                .getString(TEAM_TWO_DEFAULT_NAME, getString(R.string.team_two_default_name));
        int teamOneDefaultPassesAllowed = preferences.getInt(
                TEAM_ONE_DEFAULT_PASSES_ALLOWED,
                Integer.parseInt(getString(
                        R.string.default_passes_allowed)));
        int teamTwoDefaultPassesAllowed = preferences.getInt(
                TEAM_TWO_DEFAULT_PASSES_ALLOWED,
                Integer.parseInt(getString(
                        R.string.default_passes_allowed)));
        int defaultGoalScore = preferences.getInt(DEFAULT_GOAL_SCORE, Integer.parseInt(
                getString(R.string.default_goal_score)));

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.new_game_dialog, null);
        teamOneNameField = (EditText) dialogView.findViewById(R.id.team_one_name_field);
        teamTwoNameField = (EditText) dialogView.findViewById(R.id.team_two_name_field);

        teamOnePassesAllowed =
                (EditText) dialogView.findViewById(R.id.team_one_passes_allowed_field);
        teamTwoPassesAllowed =
                (EditText) dialogView.findViewById(R.id.team_two_passes_allowed_field);
        goalScoreField = (EditText) dialogView.findViewById(R.id.goal_score_field);
        goalScoreField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    finish();
                    return true;
                }
                return false;
            }
        });

        teamOneNameField.setText(teamOneDefaultName);
        teamTwoNameField.setText(teamTwoDefaultName);
        teamOnePassesAllowed.setText(String.valueOf(teamOneDefaultPassesAllowed));
        teamTwoPassesAllowed.setText(String.valueOf(teamTwoDefaultPassesAllowed));
        goalScoreField.setText(String.valueOf(defaultGoalScore));

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.new_game_dialog_title)
                        .setView(dialogView)
                        .setCancelable(true)
                        .setPositiveButton(
                                getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });

        return builder.create();
    }

    private void finish() {


        if (listener != null) {

            final String teamOneName = teamOneNameField.getText().toString();
            final String teamTwoName = teamTwoNameField.getText().toString();
            final int teamOnePassesAllowed =
                    Integer.parseInt(this.teamOnePassesAllowed.getText().toString());
            final int teamTwoPassesAllowed =
                    Integer.parseInt(this.teamTwoPassesAllowed.getText().toString());
            final int goalScore = Integer.parseInt(goalScoreField.getText().toString());

            getContext()
                    .getSharedPreferences(
                            MainApplication.SHARED_PREFERENCES_FILENAME,
                            Context.MODE_PRIVATE).edit()
                    .putString(TEAM_ONE_DEFAULT_NAME, teamOneName)
                    .putString(TEAM_TWO_DEFAULT_NAME, teamTwoName)
                    .putInt(TEAM_ONE_DEFAULT_PASSES_ALLOWED, teamOnePassesAllowed)
                    .putInt(TEAM_TWO_DEFAULT_PASSES_ALLOWED, teamTwoPassesAllowed)
                    .putInt(DEFAULT_GOAL_SCORE, goalScore).apply();

            listener.onCompleted(new Game(
                    new String[]{
                            teamOneName,
                            teamTwoName},
                    new int[]{
                            teamOnePassesAllowed,
                            teamTwoPassesAllowed},
                    goalScore));

        }
        dismiss();
    }

    interface NewGameDialogListener {
        void onCompleted(Game game);
    }
}
