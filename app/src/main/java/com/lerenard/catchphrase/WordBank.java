package com.lerenard.catchphrase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by mc on 11-Jan-17.
 */

public class WordBank {
    private static final String SEED_KEY = "SEED_KEY", INDEX_KEY = "INDEX_KEY", TAG = "WordBank_";
    private static WordBank instance;
    private static String filename = "nouns.txt";
    private static ArrayList<String> unmodifiedWords;
    private ArrayList<String> words;
    private int index;

    // Let's find this comment!

    private WordBank(ArrayList<String> words, int index) {
        this.words = words;
        this.index = index % words.size();
    }

    public static String getNext() {
        return instance._getNext();
    }

    private String _getNext() {
        String res = words.get(index++);
        if (index == words.size()) {
            index = 0;
            storeData(index, shuffle());
        }
        else {
            getPreferences().edit().putInt(INDEX_KEY, index).apply();
        }
        return res;
    }

    private static void storeData(int index, long seed) {
        getPreferences().edit().putLong(SEED_KEY, seed).putInt(INDEX_KEY, index).apply();
    }

    /**
     * @return the seed used to shuffle
     */
    private long shuffle() {
        long seed = System.currentTimeMillis();
        Collections.copy(words, unmodifiedWords);
        Collections.shuffle(words, new Random(seed));
        return seed;
    }

    private static SharedPreferences getPreferences() {
        return MainApplication.getContext().getSharedPreferences(
                MainApplication.SHARED_PREFERENCES_FILENAME,
                Context.MODE_PRIVATE);
    }

    public static void initialize(Context context) {
        unmodifiedWords = new ArrayList<>();
        ArrayList<String> shuffleMe = new ArrayList<>();
        InputStream file = null;
        try {
            file = context.getAssets().open(filename);
            assert file != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                unmodifiedWords.add(line);
                shuffleMe.add(line);
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        SharedPreferences preferences = getPreferences();
        long seed = preferences.getLong(SEED_KEY, -1);
        int index = preferences.getInt(INDEX_KEY, -1);
        if (seed == -1) {
            seed = System.currentTimeMillis();
            index = 0;
            storeData(index, seed);
        }
        Collections.shuffle(shuffleMe, new Random(seed));
        instance = new WordBank(shuffleMe, index);
    }
}
