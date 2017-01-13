package com.lerenard.catchphrase;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.lerenard.catchphrase.helper.HasId;

import java.sql.Date;
import java.util.Arrays;

/**
 * Created by mc on 11-Jan-17.
 */

public class Game implements Parcelable, HasId {
    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
    private static final String TAG = "Game";
    private String[] teamNames;
    private Date lastVisited;
    private int[] scores;
    private int[] passesAllowed;
    private long id;
    private int passesUsed;
    private int activeTeam;
    private int goalScore;

    public Game(Game g) {
        this(
                g.teamNames, g.lastVisited, g.scores, g.passesAllowed, g.id, g.passesUsed,
                g.activeTeam, g.goalScore);
    }

    public Game(
            String[] teamNames, Date lastVisited, int[] scores, int[] passesAllowed, long id,
            int passesUsed, int activeTeam, int goalScore) {
        this.teamNames = Arrays.copyOf(teamNames, 2);
        this.lastVisited = lastVisited;
        this.scores = Arrays.copyOf(scores, 2);
        this.passesAllowed = Arrays.copyOf(passesAllowed, 2);
        this.id = id;
        this.passesUsed = passesUsed;
        this.activeTeam = activeTeam;
        this.goalScore = Math.max(1, goalScore);
        Log.d(TAG, "setting goalScore to max of 1 and " + goalScore + ", which is " + this.goalScore);
    }

    public Game(String[] teamNames, int[] passesAllowed, int goalScore) {
        this(
                teamNames, new Date(System.currentTimeMillis()), new int[]{0, 0}, passesAllowed, -1,
                0, 0, goalScore);
    }

    protected Game(Parcel in) {
        teamNames = in.createStringArray();
        lastVisited = new Date(in.readLong());
        scores = in.createIntArray();
        passesAllowed = in.createIntArray();
        id = in.readLong();
        passesUsed = in.readInt();
        activeTeam = in.readInt();
        goalScore = in.readInt();
    }

    public int getGoalScore() {
        return goalScore;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public int getPassesUsed() {
        return passesUsed;
    }

    public int[] getScores() {
        return scores;
    }

    public int[] getPassesAllowed() {
        return passesAllowed;
    }

    public int getActiveTeam() {
        if (Math.max(scores[0], scores[1]) >= goalScore) {
            activeTeam = -1;
        }
        return activeTeam;
    }

    public String[] getTeamNames() {
        return teamNames;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the number of passes still left to use, if there are any, or -1
     */
    public int requestPass() {
        if (getPassesLeft() > 0) {
            ++passesUsed;
            return getPassesLeft();
        }
        else {
            return -1;
        }
    }

    public int getPassesLeft() {
        return passesAllowed[getActiveTeam()] - passesUsed;
    }

    public void resetPassesUsed() {
        passesUsed = 0;
    }

    /**
     * @return true if the game should continue, false otherwise
     */
    public boolean nextTeam() {
        if (getActiveTeam() == -1) {
            return false;
        }
        else {
            activeTeam = (activeTeam + 1) % 2;
            passesUsed = 0;
            return true;
        }
    }

    /**
     * @return the integer denoting the winning team, or -1 if the game hasn't completed.
     */
    public int winner() {
        if (getActiveTeam() == -1) {
            return scores[0] > scores[1] ? 0 : 1;
        }
        else {
            return -1;
        }
    }

    public void adjustPoints(int who, int howMuch) {
        scores[who] += howMuch;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(teamNames);
        dest.writeLong(lastVisited.getTime());
        dest.writeIntArray(scores);
        dest.writeIntArray(passesAllowed);
        dest.writeLong(id);
        dest.writeInt(passesUsed);
        dest.writeInt(activeTeam);
        dest.writeInt(goalScore);
    }

    public void setLastVisitedToNow() {
        lastVisited = new Date(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        String res = "<Game(" + teamNames[0] + " (" + scores[0] + ") vs. " + teamNames[1] + " (" +
                     scores[1] + "). Passes: " + Arrays.toString(passesAllowed) + ", passesUsed: " +
                     passesUsed +
                     ", goalScore: " + goalScore;
        if (activeTeam != -1) {
            res += ", it's " + teamNames[activeTeam] + "'s turn";
        }
        else {
            res += ", " + teamNames[winner()] + " won.";
        }
        return res;
    }

    public boolean isGameOver() {
        return winner() != -1;
    }

    public int getInactiveTeam() {
        if (getActiveTeam() == -1) {
            return -1;
        }
        return getActiveTeam() == 1 ? 0 : 1;
    }
}
