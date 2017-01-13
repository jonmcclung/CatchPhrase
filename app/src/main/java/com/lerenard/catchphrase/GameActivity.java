package com.lerenard.catchphrase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lerenard.catchphrase.helper.FontFitTextView;
import com.lerenard.catchphrase.helper.GameAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements Beep.BeepListener {

    public static final String GAME_KEY = "GAME_KEY";
    private static final String WORD_KEY = "WORD_KEY";
    private static final String TAG = "GameActivity_";
    private GameAdapter adapter;
    private FontFitTextView wordView;
    private Game game;
    private Button passButton, gotItButton;
    private Beep beep;
    private String passesLeftFormatString;

    public void pass(View v) {
        if (game.requestPass() != -1) {
            wordView.setText(WordBank.getNext());
            updateButtons();
        }
        else {
            Snackbar.make(
                    findViewById(R.id.activity_game), R.string.no_passes_left,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void updateButtons() {
        if (passesLeftFormatString == null) {
            passesLeftFormatString = getString(R.string.pass_button_text)
                    .replace("%%", System.getProperty("line.separator"));
        }
        passButton.setText(
                String.format(Locale.getDefault(), passesLeftFormatString, game.getPassesLeft()));
        passButton.setBackgroundColor(adapter.getActiveTeamBackgroundColor(game.getInactiveTeam()));
        gotItButton.setBackgroundColor(adapter.getActiveTeamBackgroundColor(game.getActiveTeam()));
    }

    public void gotIt(View v) {
        if (game.nextTeam()) {
            nextWord();
            adapter.notifyDataSetChanged();
        }
        else {
            gameOver();
        }
    }

    private void nextWord() {
        wordView.setText(WordBank.getNext());
        updateButtons();
    }

    private void gameOver() {
        new AlertDialog.Builder(this)
                .setMessage(
                        String.format(Locale.getDefault(),
                                      getString(R.string.game_over_dialog_message),
                                      game.getTeamNames()[game.winner()]))
                .setPositiveButton(
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle savedState =
                (savedInstanceState == null ? getIntent().getExtras() : savedInstanceState);
        game = savedState.getParcelable(GAME_KEY);

        wordView = (FontFitTextView) findViewById(R.id.word_view);
        if (savedInstanceState != null) {
            wordView.setText(savedInstanceState.getString(WORD_KEY));
        }
        else {
            wordView.setText(WordBank.getNext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_game_recyclerView);
        ArrayList<Game> gameDisplayList = new ArrayList<>();
        gameDisplayList.add(game);
        try {
            adapter = new GameAdapter(this, gameDisplayList, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        passButton = (Button) findViewById(R.id.pass_button);
        gotItButton = (Button) findViewById(R.id.got_it_button);
        updateButtons();

        beep = new Beep();
        beep.setListener(this);
        beep.restart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        beep.cancel();
        updateDatabaseWithGame();
    }

    private void updateDatabaseWithGame() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                MainApplication.getDatabase().updateGame(game);
                return null;
            }
        }.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GAME_KEY, game);
        outState.putString(WORD_KEY, wordView.getText().toString());
    }

    @Override
    public void finish() {
        Intent data = new Intent().putExtra(GAME_KEY, game);
        setResult(RESULT_OK, data);
        beep.cancel();
        super.finish();
    }

    @Override
    public void onTimerUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passButton.setEnabled(false);
                gotItButton.setEnabled(false);
                givePointTo(false);
                if (!game.isGameOver()) {
                    new AlertDialog.Builder(GameActivity.this)
                            .setTitle(R.string.steal_dialog_title).setMessage(
                            R.string.steal_dialog_message).setPositiveButton(

                            R.string.steal_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    givePointTo(false);
                                    nextRound();
                                }
                            }).setNegativeButton(
                            R.string.steal_dialog_negative,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int which) {
                                    nextRound();
                                }
                            }).show();
                }
                else {
                    gameOver();
                }
            }
        });
        SoundGenerator.generate(2, 500).play();
    }

    private void givePointTo(boolean active) {
        if (active) {
            game.adjustPoints(game.getActiveTeam(), 1);
        }
        else {
            game.adjustPoints(game.getInactiveTeam(), 1);
        }
        adapter.notifyDataSetChanged();
    }

    private void nextRound() {
        if (game.isGameOver()) {
            gameOver();
        }
        else {
            nextWord();
            game.resetPassesUsed();
            passButton.setEnabled(true);
            gotItButton.setEnabled(true);
            updateButtons();
            beep.restart();
        }
    }
}
