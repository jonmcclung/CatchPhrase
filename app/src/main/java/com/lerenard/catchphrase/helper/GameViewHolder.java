package com.lerenard.catchphrase.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lerenard.catchphrase.Game;
import com.lerenard.catchphrase.R;

/**
 * Created by mc on 11-Jan-17.
 */
public class GameViewHolder extends RecyclerViewHolder<Game> {
    private TextView[] nameView, scoreView;
    private LinearLayout[] teamView;

    public GameViewHolder(
            View itemView,
            GameAdapter adapter) {
        super(itemView, adapter);
        ViewGroup rootView = (ViewGroup) itemView.findViewById(R.id.gameView);
        rootView.setBackgroundColor(adapter.getBackgroundColor());
        nameView = new TextView[]{
                (TextView) itemView.findViewById(R.id.teamOneNameView),
                (TextView) itemView.findViewById(R.id.teamTwoNameView)};
        scoreView = new TextView[]{
                (TextView) itemView.findViewById(R.id.teamOneScoreView),
                (TextView) itemView.findViewById(R.id.teamTwoScoreView)};
        teamView = new LinearLayout[]{
                (LinearLayout) itemView.findViewById(R.id.teamOneView),
                (LinearLayout) itemView.findViewById(R.id.teamTwoView)};
    }

    @Override
    public void setItem(Game game) {
        super.setItem(game);
        for (int i = 0; i < 2; ++i) {
            nameView[i].setText(game.getTeamNames()[i]);
            scoreView[i].setText(String.valueOf(game.getScores()[i]));
        }
        int activeTeam = game.getActiveTeam();
        if (activeTeam == -1) {
            activeTeam = game.winner();
        }
        int activeTextColor = ((GameAdapter) adapter).getActiveTeamTextColor(activeTeam);
        nameView[activeTeam].setTextColor(activeTextColor);
        scoreView[activeTeam].setTextColor(activeTextColor);
        teamView[activeTeam].setBackgroundColor
                (((GameAdapter) adapter).getActiveTeamBackgroundColor(activeTeam));

        int inactiveTeam = activeTeam == 0 ? 1 : 0;
        int inactiveTextColor = ((GameAdapter) adapter).getInactiveTeamTextColor();
        nameView[inactiveTeam].setTextColor(inactiveTextColor);
        scoreView[inactiveTeam].setTextColor(inactiveTextColor);
        teamView[inactiveTeam]
                .setBackgroundColor(((GameAdapter) adapter).getInactiveTeamBackgroundColor());
    }
}
