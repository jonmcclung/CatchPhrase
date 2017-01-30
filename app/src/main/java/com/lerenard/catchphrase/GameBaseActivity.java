package com.lerenard.catchphrase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lerenard.catchphrase.helper.FontFitTextView;

import java.util.Locale;

/**
 * Created by mc on 22-Jan-17.
 */

public abstract class GameBaseActivity extends AppCompatActivity implements Beep.BeepListener,
                                                                            NextRoundDialog
                                                                                    .Listener {
    public static final String GAME_KEY = "GAME_KEY";
    private static final String TAG = "GameBaseActivity";
    private static final String NEXT_ROUND_DIALOG_TAG = "NEXT_ROUND_DIALOG_TAG";
    protected Button passButton, gotItButton;
    protected Beep beep;
    protected SoundPlayer player = new SoundPlayer();
    private FontFitTextView wordView;
    private String passesLeftFormatString;

    protected void pass(View v) {
        wordView.setText(WordBank.getNext());
        updateButtons();
    }

    protected void updateButtons() {
        if (passesLeftFormatString == null) {
            passesLeftFormatString = getString(R.string.pass_button_text)
                    .replace("%%", System.getProperty("line.separator"));
        }
        passButton.setText(
                String.format(Locale.getDefault(), passesLeftFormatString, getPassesLeft()));
    }

    protected abstract int getPassesLeft();

    public void gotIt(View v) {
        nextWord();
    }

    protected void nextWord() {
        wordView.setText(WordBank.getNext());
        updateButtons();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        beep.cancel();
    }

    protected void init(Bundle savedInstanceState) {
        Bundle savedState =
                (savedInstanceState == null ? getIntent().getExtras() : savedInstanceState);
        restoreState(savedState);
        wordView = (FontFitTextView) findViewById(R.id.word_view);
        passButton = (Button) findViewById(R.id.pass_button);
        gotItButton = (Button) findViewById(R.id.got_it_button);
        setupRoundFromInactiveActivity();
    }

    protected abstract void restoreState(Bundle state);

    protected void setupRoundFromInactiveActivity() {
        initializeBeep();
    }

    private void initializeBeep() {
        beep = new Beep();
        beep.setListener(this);
    }

    protected void confirmNextRound(boolean isNext) {
        if (getSupportFragmentManager().findFragmentByTag(NEXT_ROUND_DIALOG_TAG) == null) {
            NextRoundDialog nextRoundDialog = new NextRoundDialog();
            Bundle args = new Bundle();
            args.putBoolean(NextRoundDialog.IS_NEXT, isNext);
            nextRoundDialog.setArguments(args);
            nextRoundDialog.setListener(this);
            nextRoundDialog.show(getSupportFragmentManager(), NEXT_ROUND_DIALOG_TAG);
        }
    }

    @Override
    public void nextRound() {
        nextWord();
        resetPassesLeft();
        passButton.setEnabled(true);
        gotItButton.setEnabled(true);
        updateButtons();
        beep.restart();
    }

    @Override
    public void quit() {
        finish();
    }

    protected abstract void resetPassesLeft();

    @Override
    protected void onRestart() {
        super.onRestart();
        NextRoundDialog dialog =
                (NextRoundDialog) getSupportFragmentManager()
                        .findFragmentByTag(NEXT_ROUND_DIALOG_TAG);
        if (dialog != null) {
            dialog.setListener(this);
        }
        setupRoundFromInactiveActivity();
    }

    @Override
    public void onTimerUp() {
        beep.silence();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passButton.setEnabled(false);
                gotItButton.setEnabled(false);
            }
        });
        player.play(SoundGenerator.generate(2, 500));
    }
}
