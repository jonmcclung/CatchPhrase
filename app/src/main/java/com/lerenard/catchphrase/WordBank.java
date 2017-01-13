package com.lerenard.catchphrase;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import static com.lerenard.catchphrase.MainApplication.random;

/**
 * Created by mc on 11-Jan-17.
 */

public class WordBank {
    private static final String SEED_KEY = "SEED_KEY";
    private static WordBank instance;
    private static String filename = "nouns.txt";
    private ArrayList<String> words;
    private int index;

    private WordBank(ArrayList<String> words, int seed) {
        this.words = words;
        this.index = seed % words.size();
    }

    public static String getNext() {
        return instance._getNext();
    }

    private String _getNext() {
        String res = words.get(index);
        index = (index + 1) % words.size();
        MainApplication.getContext().getSharedPreferences(MainApplication.SHARED_PREFERENCES_FILENAME,
                                             Context.MODE_PRIVATE).edit().putInt(SEED_KEY, index).apply();
        return res;
    }

    public static void initialize(Context context) {
        ArrayList<String> words = new ArrayList<>();
        InputStream file = null;
        try {
            file = context.getAssets().open(filename);
            assert file != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        SharedPreferences preferences =
                context.getSharedPreferences(MainApplication.SHARED_PREFERENCES_FILENAME,
                                             Context.MODE_PRIVATE);
        int seed = preferences.getInt(SEED_KEY, -1);
        if (seed == -1) {
            seed = random.nextInt(words.size());
            preferences.edit().putInt(SEED_KEY, seed).apply();
        }
        initialize(words, seed);
    }

    public static void initialize(ArrayList<String> words, int seed) {
        instance = new WordBank(words, seed);
    }
}
