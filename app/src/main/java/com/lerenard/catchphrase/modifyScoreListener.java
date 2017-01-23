package com.lerenard.catchphrase;

/**
 * Created by mc on 17-Jan-17.
 */

public interface ModifyScoreListener {
    void onScoresAdjusted(int[] scores);

    void tentativeAdjustment(int who, int howMuch);

    void onCancel();
}
