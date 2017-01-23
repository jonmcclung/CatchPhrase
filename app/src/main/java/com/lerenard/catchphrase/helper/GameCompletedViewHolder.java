package com.lerenard.catchphrase.helper;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lerenard.catchphrase.Game;
import com.lerenard.catchphrase.R;

/**
 * Created by mc on 11-Jan-17.
 */
public class GameCompletedViewHolder extends RecyclerViewHolder<Game> {

    private TextView teamOneNameView, teamTwoNameView, teamOneScoreView, teamTwoScoreView;
    private LinearLayout teamOneView, teamTwoView;

    public GameCompletedViewHolder(
            View itemView,
            GameCompletedAdapter adapter) {
        super(itemView, adapter);
        LinearLayout rootView = (LinearLayout) itemView.findViewById(R.id.gameView);
        rootView.setBackgroundColor(adapter.getBackgroundColor());
        teamOneNameView = (TextView) itemView.findViewById(R.id.teamOneNameView);
        teamTwoNameView = (TextView) itemView.findViewById(R.id.teamTwoNameView);
        teamOneScoreView = (TextView) itemView.findViewById(R.id.teamOneScoreView);
        teamTwoScoreView = (TextView) itemView.findViewById(R.id.teamTwoScoreView);
        teamOneView = (LinearLayout) itemView.findViewById(R.id.teamOneView);
        teamTwoView = (LinearLayout) itemView.findViewById(R.id.teamTwoView);
    }

    @Override
    public void setItem(Game game) {
        super.setItem(game);
        teamOneNameView.setText(game.getTeamNames()[0]);
        teamTwoNameView.setText(game.getTeamNames()[1]);
        teamOneScoreView.setText(String.valueOf(game.getScores()[0]));
        teamTwoScoreView.setText(String.valueOf(game.getScores()[1]));
        int winnerTeam = game.winner();
        if (winnerTeam == -1) {
            throw new IllegalStateException("Completed game should not have game.winner() == -1");
        }
        else if (winnerTeam == 0) {
            makeWinner(teamOneView, teamOneNameView, teamOneScoreView);
            makeLoser(teamTwoView, teamTwoNameView, teamTwoScoreView);
        }
        else if (winnerTeam == 1) {
            makeWinner(teamTwoView, teamTwoNameView, teamTwoScoreView);
            makeLoser(teamOneView, teamOneNameView, teamOneScoreView);
        }
    }

    private void makeWinner(LinearLayout teamView, TextView... textViews) {
        teamView.setBackgroundColor(
                ((GameCompletedAdapter) adapter).getWinnerTeamBackgroundColor());
        for (TextView textView : textViews) {
            textView.setBackgroundColor(((GameCompletedAdapter) adapter).getWinnerTeamTextColor());
        }
    }

    private void makeLoser(LinearLayout teamView, TextView... textViews) {
        teamView.setBackgroundColor(((GameCompletedAdapter) adapter).getLoserTeamBackgroundColor());
        for (TextView textView : textViews) {
            textView.setBackgroundColor(((GameCompletedAdapter) adapter).getLoserTeamTextColor());
        }
    }
}
