package com.lerenard.catchphrase;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SmallGameActivity extends GameBaseActivity {

    public static final String
            PASSES_ALLOWED_KEY = "PASSES_ALLOWED_KEY",
            PASSES_LEFT_KEY = "PASSES_LEFT_KEY",
            WORDS_GUESSED_KEY = "WORDS_GUESS_KEY";
    private int passesAllowed, passesLeft, wordsGuessed;
    private TextView wordsGuessView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PASSES_ALLOWED_KEY, passesAllowed);
        outState.putInt(PASSES_LEFT_KEY, passesLeft);
        outState.putInt(WORDS_GUESSED_KEY, wordsGuessed);
    }

    @Override
    protected int getPassesLeft() {
        return passesLeft;
    }

    @Override
    public void gotIt(View v) {
        super.gotIt(v);
        setWordsGuessed(wordsGuessed + 1);
    }

    @Override
    protected void pass(View v) {
        if (passesLeft > 0) {
            --passesLeft;
            super.pass(v);
        }
        else {
            Snackbar.make(
                    findViewById(R.id.activity_small_game), R.string.no_passes_left,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void restoreState(Bundle state) {
        passesAllowed = state.getInt(PASSES_ALLOWED_KEY);
        if (state.containsKey(PASSES_LEFT_KEY)) {
            passesLeft = state.getInt(PASSES_LEFT_KEY);
        }
        else {
            passesLeft = passesAllowed;
        }
        // defaults to 0 if no value, so this is fine.
        wordsGuessed = state.getInt(WORDS_GUESSED_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.small_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.activity_small_game_help:
                showHelp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelp() {
        beep.interrupt();
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_title)
                .setMessage(R.string.small_game_help_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmNextRound(false);
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_game);
        init(savedInstanceState);
        setSupportActionBar((Toolbar) findViewById(R.id.small_game_toolbar));
        wordsGuessView = (TextView) findViewById(R.id.activity_small_game_counter);
        wordsGuessView.setText(String.valueOf(wordsGuessed));
        confirmNextRound(savedInstanceState != null);
    }

    @Override
    protected void nextRound() {
        super.nextRound();
        setWordsGuessed(0);
    }

    @Override
    protected void resetPassesLeft() {
        passesLeft = passesAllowed;
    }

    @Override
    protected void startRoundFromInactiveActivity() {
        super.startRoundFromInactiveActivity();
        updateButtons();
    }

    @Override
    public void onTimerUp() {
        super.onTimerUp();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                confirmNextRound(true);
            }
        });
    }

    private void setWordsGuessed(int wordsGuessed) {
        this.wordsGuessed = wordsGuessed;
        wordsGuessView.setText(String.valueOf(wordsGuessed));
    }
}
