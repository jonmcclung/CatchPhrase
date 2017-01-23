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

public class PassingDialog extends DialogFragment {
    public static final String TEAM_NAMES_KEY = "TEAM_NAMES_KEY";
    private String[] teamNames;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        restoreArguments(args);
    }

    private void restoreArguments(Bundle state) {
        teamNames = state.getStringArray(TEAM_NAMES_KEY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreArguments(savedInstanceState);
        }
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.passing_dialog, null);

        setCancelable(false);
        Button[] buttons = new Button[]{
                (Button) dialogView.findViewById(R.id.team_one_name_button),
                (Button) dialogView.findViewById(R.id.team_two_name_button)};

        for (int i = 0; i < buttons.length; ++i) {
            final int team = i;
            buttons[i].setText(teamNames[i]);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTeam(team);
                }
            });
        }

        return new AlertDialog.Builder(getContext()).setView(dialogView).create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(TEAM_NAMES_KEY, teamNames);
    }

    private void selectTeam(int who) {
        if (listener != null) {
            listener.onTeamSelected(who);
        }
        dismiss();
    }

    interface Listener {
        void onTeamSelected(int who);
    }
}
