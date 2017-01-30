package com.lerenard.catchphrase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lerenard.catchphrase.helper.GameAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class GameActivity extends GameBaseActivity
        implements Beep.BeepListener, ModifyScoreListener, EndRoundDialog.Listener,
                   PassingDialog.Listener {

    private static final String
            TAG = "GameActivity_",
            SHOW_MODIFY_SCORES_TAG = "SHOW_MODIFY_SCORES_TAG",
            ROUND_END_TAG = "ROUND_END_TAG",
            PASSING_DIALOG_TAG = "PASSING_DIALOG_TAG";
    private GameAdapter adapter;
    private String[] dialogTags = {SHOW_MODIFY_SCORES_TAG, ROUND_END_TAG, PASSING_DIALOG_TAG};
    private Game game;

    @Override
    protected void pass(View v) {
        if (game.requestPass() != -1) {
            super.pass(v);
        }
        else {
            Snackbar.make(
                    findViewById(R.id.activity_game), R.string.no_passes_left,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    protected void updateButtons() {
        super.updateButtons();
        passButton.setBackgroundColor(adapter.getActiveTeamBackgroundColor(game.getInactiveTeam()));
        gotItButton.setBackgroundColor(adapter.getActiveTeamBackgroundColor(game.getActiveTeam()));
    }

    @Override
    protected int getPassesLeft() {
        return game.getPassesLeft();
    }

    @Override
    public void gotIt(View v) {
        if (game.nextTeam()) {
            super.gotIt(v);
            adapter.notifyDataSetChanged();
        }
        else {
            // shouldn't happen
            throw new RuntimeException("why did I get here?");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (String tag : dialogTags) {
            if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
                return;
            }
        }
        confirmNextRound(false);
    }

    @Override
    protected void restoreState(Bundle state) {
        game = state.getParcelable(GAME_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_game_toolbar));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_game_recyclerView);
        ArrayList<Game> gameDisplayList = new ArrayList<>();
        try {
            adapter = new GameAdapter(this, gameDisplayList, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        init(savedInstanceState);
        adapter.add(game, false);
        if (savedInstanceState == null) {
            confirmNextRound(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateDatabaseWithGame();
    }

    @Override
    public void nextRound() {
        super.nextRound();
    }

    @Override
    protected void resetPassesLeft() {
        game.resetPassesUsed();
    }

    protected void setupRoundFromInactiveActivity() {
        super.setupRoundFromInactiveActivity();
        if (!game.isGameOver()) {
            updateButtons();
        }
        else {
            finish();
        }
    }

    @Override
    public void onTimerUp() {
        super.onTimerUp();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!game.isGameOver()) {
                    int[] colors = {
                            ContextCompat.getColor(getApplicationContext(), R.color.teamOne),
                            ContextCompat.getColor(getApplicationContext(), R.color.teamTwo)};
                    EndRoundDialog dialog = EndRoundDialog.newInstance(
                            colors[game.getInactiveTeam()],
                            colors[game.getActiveTeam()]);
                    dialog.setListener(GameActivity.this);
                    dialog.show(getSupportFragmentManager(), ROUND_END_TAG);
                }
                else {
                    gameOver();
                }
            }
        });
    }

    private void gameOver() {
        // a#5: 932
        // d5:  1175
        // f5:  1397
        // a6:  1760
        // a#6: 1865
        double[] gameOverFrequencies = {932, 1175, 1397, 1760, 1865};
        double[] gameOverDurations = {.2, .2, .2, .2, .8};
        player.play(SoundGenerator.generate(gameOverDurations, gameOverFrequencies));

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
                                finish();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).show();
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
        /*
        // Not sure where this code ought to go to ensure the dialog doesn't leak.
        // Currently I get "dismiss not allowed after onSaveInstanceState," which is confusing since
        // I'm only trying to do it *in* onSaveInstanceState.
        for (String tag : dialogTags) {
            DialogFragment dialog =
                    (DialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (dialog != null) {
                dialog.dismiss();
                break;
            }
        }*/
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        ModifyScoreDialog modifyScoreDialog =
                (ModifyScoreDialog) getSupportFragmentManager()
                        .findFragmentByTag(SHOW_MODIFY_SCORES_TAG);
        if (modifyScoreDialog != null) {
            modifyScoreDialog.setListener(this);
        }
        else {
            EndRoundDialog endRoundDialog =
                    (EndRoundDialog) getSupportFragmentManager().findFragmentByTag(ROUND_END_TAG);
            if (endRoundDialog != null) {
                endRoundDialog.setListener(this);
            }
            else {
                PassingDialog passingDialog = (PassingDialog) getSupportFragmentManager()
                        .findFragmentByTag(PASSING_DIALOG_TAG);
                if (passingDialog != null) {
                    passingDialog.setListener(this);
                }
                else {
                    Log.d(TAG, "confirming");
                    confirmNextRound(false);
                }
            }
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
            /*
            a#: 932
            a: 880
            g#: 831
            d#: 622
            */
        double[] interruptFrequencies = {932, 880, 831, 622, 1, 622};
        double[] interruptDurations = {.2, .2, .2, .2, .1, .2};
        player.play(SoundGenerator.generate(interruptDurations, interruptFrequencies));
        ModifyScoreDialog dialog = ModifyScoreDialog.newInstance(game.getScores());
        dialog.setListener(this);
        dialog.show(getSupportFragmentManager(), SHOW_MODIFY_SCORES_TAG);
    }

    @Override
    public void finish() {
        Intent data = new Intent().putExtra(GAME_KEY, game);
        setResult(RESULT_OK, data);
        beep.cancel();
        super.finish();
    }

    @Override
    public void onScoresAdjusted(int[] scores) {
        game.setScores(scores);
        adapter.notifyDataSetChanged();
        maybeStartNextRound(false);
    }

    @Override
    public void tentativeAdjustment(int who, int howMuch) {
        makePointAdjustmentSounds(who, howMuch);
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "cancelling");
        beep.playCancelSound();
        maybeStartNextRound(true);
    }

    private void makePointAdjustmentSounds(int who, int howMuch) {
        double[][] teamFrequencies = {{932, 1109, 932}, {932, 880, 932}};
        if (howMuch < 0) {
            howMuch *= -1;
            who = (who + 1) % 2;
        }
        int length = teamFrequencies[who].length;
        double[] durations = new double[howMuch * length], frequencies =
                new double[howMuch * length];
        Arrays.fill(durations, .2);
        for (int i = 0; i < howMuch; ++i) {
            System.arraycopy(teamFrequencies[who], 0, frequencies, i * length, length);
        }
        player.play(SoundGenerator.generate(durations, frequencies));
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
    public void onChoiceSelected(EndRoundDialog.Choices choice) {
        switch (choice) {
            case SUCCESSFUL_STEAL:
                adjustPoints(game.getInactiveTeam(), 2);
                maybeStartNextRound(true);
                break;
            case UNSUCCESSFUL_STEAL:
                adjustPoints(game.getInactiveTeam(), 1);
                maybeStartNextRound(true);
                break;
            case PASSING:
                gotIt(null);
                startPassingDialog();
                break;
        }
    }

    private void adjustPoints(int who, int howMuch) {
        adjustPoints(who, howMuch, false);
    }

    private void startPassingDialog() {
        PassingDialog dialog = new PassingDialog();
        Bundle args = new Bundle();
        args.putStringArray(PassingDialog.TEAM_NAMES_KEY, game.getTeamNames());
        dialog.setArguments(args);
        dialog.setListener(this);
        dialog.show(getSupportFragmentManager(), PASSING_DIALOG_TAG);
    }

    private void adjustPoints(int who, int howMuch, boolean silent) {
        if (!silent) {
            makePointAdjustmentSounds(who, howMuch);
        }
        game.adjustPoints(who, howMuch);
        adapter.notifyDataSetChanged();
    }

    private void givePointTo(boolean active) {
        if (active) {
            adjustPoints(game.getActiveTeam(), 1);
        }
        else {
            adjustPoints(game.getInactiveTeam(), 1);
        }
    }

    @Override
    public void onTeamSelected(int who) {
        adjustPoints((who + 1) % 2, 1);
        maybeStartNextRound(true);
    }
}
