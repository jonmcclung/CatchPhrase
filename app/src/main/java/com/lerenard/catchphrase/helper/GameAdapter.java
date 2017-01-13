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

public class GameAdapter extends RecyclerAdapter<Game, GameViewHolder> {


    private static String TAG = "GameAdapter";
    private final int
            inactiveTeamBackgroundColor,
            inactiveTeamTextColor;
    private final int[] activeTeamTextColor, activeTeamBackgroundColor;

    public GameAdapter(

            Context context,
            ArrayList<Game> items,
            DataSetListener<Game> listener) throws NoSuchMethodException {
        this(context, items, listener,
             ContextCompat.getColor(context, R.color.listItem),
             ContextCompat.getColor(context, R.color.ripple),
             ContextCompat.getColor(context, R.color.inactive),
             ContextCompat.getColor(context, R.color.darkText),
             ContextCompat.getColor(context, R.color.teamOne),
             Color.WHITE,
             ContextCompat.getColor(context, R.color.teamTwo),
             Color.WHITE);
    }

    private GameAdapter(
            Context context,
            ArrayList<Game> items,
            DataSetListener<Game> listener,
            int unselectedColor, int selectedColor,
            int inactiveTeamBackgroundColor, int inactiveTeamTextColor,
            int activeTeamOneBackgroundColor, int activeTeamOneTextColor,
            int activeTeamTwoBackgroundColor, int activeTeamTwoTextColor) throws
            NoSuchMethodException {
        super(items, listener, R.layout.game_list_view,
              new Supplier<>(
                      GameViewHolder.class.getConstructor(
                              View.class,
                              GameAdapter.class)),
              unselectedColor, selectedColor);
        this.inactiveTeamBackgroundColor = inactiveTeamBackgroundColor;
        this.activeTeamBackgroundColor =
                new int[]{activeTeamOneBackgroundColor, activeTeamTwoBackgroundColor};
        this.activeTeamTextColor = new int[]{activeTeamOneTextColor, activeTeamTwoTextColor};
        this.inactiveTeamTextColor = inactiveTeamTextColor;
    }

    public int getBackgroundColor() {
        return getUnselectedColor();
    }

    public int getInactiveTeamBackgroundColor() {
        return inactiveTeamBackgroundColor;
    }

    public int getActiveTeamBackgroundColor(int who) {
        return activeTeamBackgroundColor[who];
    }

    public int getActiveTeamTextColor(int who) {
        return activeTeamTextColor[who];
    }

    public int getInactiveTeamTextColor() {
        return inactiveTeamTextColor;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return supplier.get(view, this);
    }
}
