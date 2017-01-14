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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lerenard.catchphrase.helper.FontFitTextView;
import com.lerenard.catchphrase.helper.GameAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements Beep.BeepListener {

    public static final String GAME_KEY = "GAME_KEY";
    private static final String TAG = "GameActivity_";
    private GameAdapter adapter;
    private FontFitTextView wordView;
    private Game game;
    private Button passButton, gotItButton;
    private Beep beep;
    private String passesLeftFormatString;
    private boolean dialogShowing;

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
                        String.format(
                                Locale.getDefault(),
                                getString(R.string.game_over_dialog_message),
                                game.getTeamNames()[game.winner()]))
                .setPositiveButton(
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                beep.silence();
                                finish();
                            }
                        }).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initializeBeep();
        if (!dialogShowing) {
            confirmNextRound(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modify_scores:
                showModifyScores();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showModifyScores() {
        beep.interrupt();
        View dialogView = getLayoutInflater().inflate(R.layout.modify_scores_dialog, null);
        final TextView teamOneScoreView =
                (TextView) dialogView.findViewById(R.id.team_one_score_view);
        final TextView teamTwoScoreView =
                (TextView) dialogView.findViewById(R.id.team_two_score_view);
        TextView teamOneIncrement = (TextView) dialogView.findViewById(R.id.team_one_increment);
        TextView teamTwoIncrement = (TextView) dialogView.findViewById(R.id.team_two_increment);
        TextView teamOneDecrement = (TextView) dialogView.findViewById(R.id.team_one_decrement);
        TextView teamTwoDecrement = (TextView) dialogView.findViewById(R.id.team_two_decrement);

        teamOneScoreView.setText(String.valueOf(game.getScores()[0]));
        teamTwoScoreView.setText(String.valueOf(game.getScores()[1]));

        teamOneIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustPointsOnTextView(teamOneScoreView, 1);
            }
        });
        teamTwoIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustPointsOnTextView(teamTwoScoreView, 1);
            }
        });
        teamOneDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustPointsOnTextView(teamOneScoreView, -1);
            }
        });
        teamTwoDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustPointsOnTextView(teamTwoScoreView, -1);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.modify_scores_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.setScores(new int[]{
                                Integer.parseInt(teamOneScoreView.getText().toString()),
                                Integer.parseInt(teamTwoScoreView.getText().toString())});
                        adapter.notifyDataSetChanged();
                        maybeStartNextRound(false);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        maybeStartNextRound(false);
                    }
                })
                .show();
    }

    private static void adjustPointsOnTextView(TextView view, int howMuch) {
        view.setText(String.format(
                Locale.getDefault(),
                "%d",
                howMuch + Integer.parseInt(view.getText().toString())));
    }

    private void maybeStartNextRound(boolean isNext) {
        if (game.isGameOver()) {
            gameOver();
        }
        else {
            confirmNextRound(isNext);
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent().putExtra(GAME_KEY, game);
        setResult(RESULT_OK, data);
        beep.cancel();
        super.finish();
    }

    private void initializeBeep() {
        beep = new Beep();
        beep.setListener(this);
    }

    private void confirmNextRound(boolean isNext) {
//        String s = "hello";
        new AlertDialog.Builder(this)
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
                                nextRound();
                            }
                        })
                .show();
    }

    private void nextRound() {
        nextWord();
        game.resetPassesUsed();
        passButton.setEnabled(true);
        gotItButton.setEnabled(true);
        updateButtons();
        beep.restart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_game_toolbar));
        Bundle savedState =
                (savedInstanceState == null ? getIntent().getExtras() : savedInstanceState);
        restoreState(savedState);
        wordView = (FontFitTextView) findViewById(R.id.word_view);

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
        initializeBeep();
        confirmNextRound(false);
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
    }

    private void restoreState(Bundle state) {
        game = state.getParcelable(GAME_KEY);
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
                                    maybeStartNextRound(true);
                                }
                            }).setNegativeButton(
                            R.string.steal_dialog_negative,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int which) {
                                    maybeStartNextRound(true);
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            maybeStartNextRound(true);
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
}
