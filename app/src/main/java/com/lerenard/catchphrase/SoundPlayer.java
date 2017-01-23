package com.lerenard.catchphrase;

import android.media.AudioTrack;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by mc on 22-Jan-17.
 */

public class SoundPlayer implements AudioTrack.OnPlaybackPositionUpdateListener {
    private Queue<AudioTrack> sounds;

    public SoundPlayer() {
        sounds = new ArrayDeque<>();
    }

    public void play(AudioTrack sound) {
        sounds.add(sound);
        if (sounds.size() == 1) {
            playNext();
        }
    }

    private void playNext() {
        AudioTrack sound = sounds.peek();
        if (sound != null) {
            sound.setPlaybackPositionUpdateListener(this);
            sound.play();
        }
    }

    @Override
    public void onMarkerReached(AudioTrack track) {
        sounds.poll();
        playNext();
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {}
}
