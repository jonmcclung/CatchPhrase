package com.lerenard.catchphrase.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.lerenard.catchphrase.Game;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by mc on 11-Jan-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final int
            DATABASE_VERSION = 1;
    private static final String
            DATABASE_NAME = "games.db",
            TABLE_GAMES = "GAMES",
            _ID = BaseColumns._ID,
            GAMES_TEAM_ONE_NAME = "team_one_name",
            GAMES_TEAM_TWO_NAME = "team_two_name",
            GAMES_TEAM_ONE_SCORE = "team_one_score",
            GAMES_TEAM_TWO_SCORE = "team_two_score",
            GAMES_TEAM_ONE_PASSES_ALLOWED = "team_one_passes_allowed",
            GAMES_TEAM_TWO_PASSES_ALLOWED = "team_two_passes_allowed",
            GAMES_PASSES_USED = "passes_used",
            GAMES_ACTIVE_TEAM = "active_team",
            GAMES_LAST_VISITED = "last_visited",
            GAMES_GOAL_SCORE = "goal_score",

    CREATE_TABLE_GAMES =
            "CREATE TABLE " + TABLE_GAMES + "("
            + _ID + " INTEGER PRIMARY KEY, "
            + GAMES_TEAM_ONE_NAME + " TEXT, "
            + GAMES_TEAM_TWO_NAME + " TEXT, "
            + GAMES_TEAM_ONE_SCORE + " INTEGER, "
            + GAMES_TEAM_TWO_SCORE + " INTEGER, "
            + GAMES_TEAM_ONE_PASSES_ALLOWED + " INTEGER, "
            + GAMES_TEAM_TWO_PASSES_ALLOWED + " INTEGER, "
            + GAMES_PASSES_USED + " INTEGER, "
            + GAMES_ACTIVE_TEAM + " INTEGER, "
            + GAMES_GOAL_SCORE + " TEXT, "
            + GAMES_LAST_VISITED + " INTEGER)";

    private static final String[] star = {
            _ID, GAMES_TEAM_ONE_NAME, GAMES_TEAM_TWO_NAME, GAMES_LAST_VISITED, GAMES_TEAM_ONE_SCORE,
            GAMES_TEAM_TWO_SCORE, GAMES_TEAM_ONE_PASSES_ALLOWED, GAMES_TEAM_TWO_PASSES_ALLOWED,
            GAMES_PASSES_USED, GAMES_GOAL_SCORE, GAMES_ACTIVE_TEAM};

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void addGame(Game game) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getValues(game);
        game.setId(db.insert(TABLE_GAMES, null, values));
        db.close();
    }

    /**
     * Retrieve the values to be inserted for the given game.
     * Doesn't include the game id as it's assumed that you will
     * use a where clause with that or that it's otherwise
     * not useful.
     */
    private ContentValues getValues(Game game) {
        ContentValues values = new ContentValues();
        String[] teamNames = game.getTeamNames();
        int[] scores = game.getScores();
        int[] passesAllowed = game.getPassesAllowed();
        values.put(GAMES_TEAM_ONE_NAME, teamNames[0]);
        values.put(GAMES_TEAM_TWO_NAME, teamNames[1]);
        values.put(GAMES_LAST_VISITED, game.getLastVisited().getTime());
        values.put(GAMES_TEAM_ONE_SCORE, scores[0]);
        values.put(GAMES_TEAM_TWO_SCORE, scores[1]);
        values.put(GAMES_TEAM_ONE_PASSES_ALLOWED, passesAllowed[0]);
        values.put(GAMES_TEAM_TWO_PASSES_ALLOWED, passesAllowed[1]);
        values.put(GAMES_PASSES_USED, game.getPassesUsed());
        values.put(GAMES_ACTIVE_TEAM, game.getActiveTeam());
        values.put(GAMES_GOAL_SCORE, game.getGoalScore());
        return values;
    }

    public Game getGame(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_GAMES,
                star,
                _ID + " = ?",
                new String[]{Integer.toString(id)},
                null, null, null);
        cursor.moveToFirst();

        Game game = getGame(cursor);

        cursor.close();
        db.close();
        return game;
    }

    private Game getGame(Cursor cursor) {
        /*_ID, GAMES_TEAM_ONE_NAME, GAMES_TEAM_TWO_NAME, GAMES_LAST_VISITED, GAMES_TEAM_ONE_SCORE,
                GAMES_TEAM_TWO_SCORE, GAMES_TEAM_ONE_PASSES_ALLOWED, GAMES_TEAM_TWO_PASSES_ALLOWED,
                GAMES_PASSES_USED, GAMES_ACTIVE_TEAM};*/
        return new Game(
                new String[]{
                        cursor.getString(cursor.getColumnIndex(GAMES_TEAM_ONE_NAME)),
                        cursor.getString(cursor.getColumnIndex(GAMES_TEAM_TWO_NAME))},
                new Date(cursor.getLong(cursor.getColumnIndex(GAMES_LAST_VISITED))),
                new int[]{
                        cursor.getInt(cursor.getColumnIndex(GAMES_TEAM_ONE_SCORE)),
                        cursor.getInt(cursor.getColumnIndex(GAMES_TEAM_TWO_SCORE))},
                new int[]{
                        cursor.getInt(cursor.getColumnIndex(GAMES_TEAM_ONE_PASSES_ALLOWED)),
                        cursor.getInt(cursor.getColumnIndex(GAMES_TEAM_TWO_PASSES_ALLOWED))},
                cursor.getInt(cursor.getColumnIndex(_ID)),
                cursor.getInt(cursor.getColumnIndex(GAMES_PASSES_USED)),
                cursor.getInt(cursor.getColumnIndex(GAMES_ACTIVE_TEAM)),
                cursor.getInt(cursor.getColumnIndex(GAMES_GOAL_SCORE)));
    }

    public ArrayList<Game> getAllGames() {
        ArrayList<Game> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = getCursor(db);

        if (cursor.moveToFirst()) {
            do {
                res.add(getGame(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return res;
    }

    private Cursor getCursor(SQLiteDatabase db) {
        Cursor res = db.rawQuery(
                "SELECT "
                + _ID + ", "
                + GAMES_TEAM_ONE_NAME + ", "
                + GAMES_TEAM_TWO_NAME + ", "
                + GAMES_TEAM_ONE_SCORE + ", "
                + GAMES_TEAM_TWO_SCORE + ", "
                + GAMES_TEAM_ONE_PASSES_ALLOWED + ", "
                + GAMES_TEAM_TWO_PASSES_ALLOWED + ", "
                + GAMES_PASSES_USED + ", "
                + GAMES_ACTIVE_TEAM + ", "
                + GAMES_LAST_VISITED + ", "
                + GAMES_GOAL_SCORE
                + " FROM " + TABLE_GAMES
                + " ORDER BY " + GAMES_LAST_VISITED, null);
        return res;
    }

    public void updateGame(Game game) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(
                TABLE_GAMES,
                getValues(game),
                _ID + " = ?",
                new String[]{Long.toString(game.getId())});
        db.close();
    }

    public void batchDeleteGame(Collection<Game> games) {
        // TODO
        throw new UnsupportedOperationException("not implemented");
    }

    public String toString() {
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder stringBuilder = new StringBuilder(), line = new StringBuilder();
        int width = 15;
        String formatString = "%1$-" + width + "s";
        for (String column : star) {
            stringBuilder.append(String.format(Locale.US, formatString, column))
                         .append('|');
            line.append(new String(new char[width]).replace('\0', '-'))
                .append('+');
        }
        stringBuilder.append('\n')
                     .append(line.toString())
                     .append('\n');
        Cursor cursor = db.query(
                TABLE_GAMES,
                star,
                null, null, null, null,
                GAMES_LAST_VISITED);
        if (cursor.moveToFirst()) {
            do {
                for (String column : star) {
                    stringBuilder.append(String.format(Locale.US, formatString,
                                                       StringUtils.trim(cursor.getString(
                                                               cursor.getColumnIndexOrThrow(
                                                                       column)), width)))
                                 .append('|');
                }
                stringBuilder.append('\n');
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stringBuilder.toString();
    }

    public void deleteGame(Game game) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                TABLE_GAMES,
                _ID + " = ?",
                new String[]{Long.toString(game.getId())});
        db.close();
    }

    public Cursor getCursor() {
        return getCursor(getWritableDatabase());
    }

    public Game getLastUsed() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_GAMES,
                star,
                GAMES_LAST_VISITED + " = (SELECT MAX(" + GAMES_LAST_VISITED + ") FROM " +
                TABLE_GAMES + ")", null, null, null, null);
        cursor.moveToFirst();
        Game res = getGame(cursor);
        cursor.close();
        db.close();
        return res;
    }
}
