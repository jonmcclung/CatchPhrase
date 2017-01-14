package com.lerenard.catchphrase;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lerenard.catchphrase.helper.DataSetListener;
import com.lerenard.catchphrase.helper.GameAdapter;
import com.lerenard.catchphrase.helper.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Locale;

import co.paulburke.android.itemtouchhelperdemo.helper.SimpleItemTouchHelperCallback;


public class MainActivity extends AppCompatActivity implements NewGameDialog.NewGameDialogListener {
    private static final int UPDATE_GAME = 0;
    private static final java.lang.String INDEX_KEY = "INDEX_KEY";
    private static final String NEW_GAME_DIALOG_TAG = "NEW_GAME_DIALOG_TAG";
    private static final String TAG = "MainActivity_";
    private RecyclerView inProgressList, completedList;
    private GameAdapter inProgressAdapter;
    private GameAdapter completedAdapter;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        NewGameDialog dialog =
                (NewGameDialog) getSupportFragmentManager().findFragmentByTag(NEW_GAME_DIALOG_TAG);
        if (dialog != null) {
            dialog.setListener(this);
        }
    }

    public void newGame(View v) {
        NewGameDialog dialog = new NewGameDialog();
        dialog.setListener(this);
        FragmentManager fm = getSupportFragmentManager();
        dialog.show(fm, NEW_GAME_DIALOG_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeLists();
        setSupportActionBar((Toolbar) findViewById(R.id.activity_main_toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_main_help:
                showHelp();
                return true;
            case R.id.settings:
                showSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    private void showHelp() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_dialog_title)
                .setMessage(String.format(Locale.getDefault(), getString(R.string.help_dialog_message), getString(R.string.got_it)))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void initializeLists() {
        ArrayList<Game> games = MainApplication.getDatabase().getAllGames();
        ArrayList<Game> inProgressGames = new ArrayList<>();
        ArrayList<Game> completedGames = new ArrayList<>();
        for (Game game : games) {
            Log.d(TAG, game.toString());
            if (game.isGameOver()) {
                Log.d(TAG, "this game is over");
                completedGames.add(game);
            }
            else {
                Log.d(TAG, "this game is in progress");
                inProgressGames.add(game);
            }
        }

        inProgressList = (RecyclerView) findViewById(R.id.game_in_progress_list);
        completedList = (RecyclerView) findViewById(R.id.game_completed_list);

        inProgressList.setNestedScrollingEnabled(false);
        completedList.setNestedScrollingEnabled(false);

        InProgressDataSetListener inProgressListener =
                new InProgressDataSetListener(inProgressList);
        try {
            inProgressAdapter =
                    new GameAdapter(this, inProgressGames, inProgressListener);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        inProgressListener.setAdapter(inProgressAdapter);
        CompletedDataSetListener completedListener = new CompletedDataSetListener(completedList);
        try {
            completedAdapter = new GameAdapter(this, completedGames, completedListener);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        completedListener.setAdapter(completedAdapter);

        checkForNone();

        LinearLayoutManager inProgressLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true);
        inProgressLayoutManager.setStackFromEnd(true);
        inProgressList.setLayoutManager(inProgressLayoutManager);
        inProgressList.setAdapter(inProgressAdapter);
        new ItemTouchHelper(
                new SimpleItemTouchHelperCallback(inProgressAdapter) {
                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }
                })
                .attachToRecyclerView(inProgressList);

        DividerItemDecoration spacer =
                new DividerItemDecoration(
                        inProgressList.getContext(), inProgressLayoutManager.getOrientation());
        spacer.setDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.game_spacer));

        inProgressList.addItemDecoration(spacer);

        LinearLayoutManager completedLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                true);
        completedLayoutManager.setStackFromEnd(true);
        completedList.setLayoutManager(completedLayoutManager);
        completedList.setAdapter(completedAdapter);
        new ItemTouchHelper(
                new SimpleItemTouchHelperCallback(completedAdapter) {
                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }
                })
                .attachToRecyclerView(completedList);

        completedList.addItemDecoration(spacer);
    }

    private void checkForNone() {
        TextView inProgressView =
                (TextView) findViewById(R.id.empty_in_progress_games_text_view);
        TextView completedView =
                (TextView) findViewById(R.id.empty_completed_games_text_view);
        if (inProgressAdapter.getItemCount() == 0) {
            inProgressView.setText(getString(R.string.none));
        }
        else {
            inProgressView.setText("");
        }
        if (completedAdapter.getItemCount() == 0) {
            completedView.setText(getString(R.string.none));
        }
        else {
            completedView.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_GAME:
                    Bundle extras = data.getExtras();
                    Game game = extras.getParcelable(GameActivity.GAME_KEY);
                    updateGameInSomeList(game);
                    break;
                default:
                    throw new IllegalStateException("unexpected requestCode: " + requestCode);
            }
        }
    }

    private void updateGameInSomeList(Game game) {
        if (game.isGameOver()) {
            inProgressAdapter.remove(inProgressAdapter.getItemCount() - 1, false);
            completedAdapter.add(game, false);
        }
        else {
            inProgressAdapter.set(inProgressAdapter.getItemCount() - 1, game, false);
        }
        checkForNone();
    }

    @Override
    public void onCompleted(Game game) {
        inProgressAdapter.add(game, true);
        checkForNone();
        selectGame(game);
    }

    /**
     * called to go to GameActivity with a specific game
     */
    private void selectGame(Game game) {
        Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class)
                .putExtra(GameActivity.GAME_KEY, game);
        startActivityForResult(gameIntent, UPDATE_GAME);
    }

    abstract class GameListener implements DataSetListener<Game> {

        private final RecyclerView recyclerView;
        protected RecyclerAdapter adapter;

        GameListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onAdd(final Game game, final int index) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    MainApplication.getDatabase().addGame(game);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onDelete(final Game game, final int position) {
            checkForNone();
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.game_deleted,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.insert(position, game, true);
                            int firstVisiblePosition =
                                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                                            .findFirstVisibleItemPosition();
                            int lastVisiblePosition =
                                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                                            .findLastVisibleItemPosition();
                            if (firstVisiblePosition > position ||
                                lastVisiblePosition < position) {
                                recyclerView.smoothScrollToPosition(position);
                            }
                            checkForNone();
                        }
                    }).show();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    MainApplication.getDatabase().deleteGame(game);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onUpdate(final Game game) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    MainApplication.getDatabase().updateGame(game);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onClick(final Game game, int position) {
            game.setLastVisitedToNow();
            // move game to end of last
            adapter.remove(position, false);
            adapter.add(game, false);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    MainApplication.getDatabase().updateGame(game);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onDrag(Game game, int start, int end) {}

        @Override
        public boolean onLongPress(Game game, int position) {
            // TODO
        /*FragmentManager fm = getSupportFragmentManager();
        EditGameNameDialog dialog = new EditGameNameDialog();
        Bundle args = new Bundle();
        args.putParcelable(ReadingActivity.RIBBON_KEY, game);
        args.putInt(INDEX_KEY, position);
        dialog.setArguments(args);
        dialog.setListener(this);
        dialog.show(fm, EDIT_RIBBON_NAME_TAG);
        return true;*/
            return false;
        }

        public void setAdapter(RecyclerAdapter adapter) {
            this.adapter = adapter;
        }
    }

    class InProgressDataSetListener extends GameListener {


        InProgressDataSetListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public void onAdd(Game game, int index) {
            super.onAdd(game, index);
            checkForNone();
        }

        @Override
        public void onClick(final Game game, int position) {
            super.onClick(game, position);
            selectGame(game);
        }
    }

    class CompletedDataSetListener extends GameListener {


        CompletedDataSetListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public void onAdd(Game game, int index) {
            super.onAdd(game, index);
            checkForNone();
        }

        @Override
        public void onClick(final Game game, int position) {
            super.onClick(game, position);
            selectGame(game);
        }
    }
}
