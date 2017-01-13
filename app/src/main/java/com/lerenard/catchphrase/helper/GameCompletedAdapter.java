package com.lerenard.catchphrase.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lerenard.catchphrase.Game;
import com.lerenard.catchphrase.R;

import java.util.ArrayList;

/**
 * Created by mc on 11-Jan-17.
 */

public class GameCompletedAdapter extends RecyclerAdapter<Game, GameCompletedViewHolder> {

    private static String TAG = "GameAdapter";
    private final int loserTeamBackgroundColor;
    private final int winnerTeamBackgroundColor;
    private final int winnerTeamTextColor;
    private final int loserTeamTextColor;

    public GameCompletedAdapter(
            Context context,
            ArrayList<Game> items,
            DataSetListener<Game> listener) throws NoSuchMethodException {
        this(context, items, listener,
             ContextCompat.getColor(context, R.color.colorPrimaryLight),
             ContextCompat.getColor(context, R.color.colorPrimary),
             ContextCompat.getColor(context, R.color.transparent),
             ContextCompat.getColor(context, R.color.colorAccent),
             ContextCompat.getColor(context, R.color.darkText),
             Color.WHITE);
    }

    public GameCompletedAdapter(
            Context context,
            ArrayList<Game> items,
            DataSetListener<Game> listener, int unselectedColor,
            int selectedColor, int loserTeamBackgroundColor,
            int winnerTeamBackgroundColor,
            int loserTeamTextColor, int winnerTeamTextColor) throws NoSuchMethodException {
        super(items, listener, R.layout.game_list_view,
              new Supplier<>(
                      GameCompletedViewHolder.class.getConstructor(
                              View.class,
                              GameCompletedAdapter.class)),
              unselectedColor, selectedColor);
        this.winnerTeamBackgroundColor = winnerTeamBackgroundColor;
        this.loserTeamBackgroundColor = loserTeamBackgroundColor;
        this.winnerTeamTextColor = winnerTeamTextColor;
        this.loserTeamTextColor = loserTeamTextColor;
    }

    public int getBackgroundColor() {
        return getUnselectedColor();
    }

    public int getLoserTeamBackgroundColor() {
        return loserTeamBackgroundColor;
    }

    public int getWinnerTeamBackgroundColor() {
        return winnerTeamBackgroundColor;
    }

    public int getWinnerTeamTextColor() {
        return winnerTeamTextColor;
    }

    public int getLoserTeamTextColor() {
        return loserTeamTextColor;
    }

    @Override
    public GameCompletedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return supplier.get(view, this);
    }
}
