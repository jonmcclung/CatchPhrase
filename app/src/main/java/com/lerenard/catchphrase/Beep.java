package com.lerenard.catchphrase;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.lerenard.catchphrase.MainApplication.random;

/**
 * Created by mc on 12-Jan-17.
 */

class Beep {
    private static final String TAG = "test";
    private Timer beepTimer, incrementTimer;
    private static final int
            howLong = 30 * 1000,
            initialDelay = 1300,
            finalDelay = 300,
            steps = 10,
            delayChange = (initialDelay - finalDelay) / (steps + 1),
            goalTime = howLong / steps;
    private int delay;
    private AudioTrack sound;
    private BeepListener listener;

    public Beep() {
        sound = SoundGenerator.generate(.05, 932);
    }

    public void restart() {
        delay = initialDelay;
        restartTimers();
    }

    private void restartTimers() {
        beepTimer = new Timer();
        beepTimer.schedule(new BeepTask(), 0, delay);
        incrementTimer = new Timer();
        int incrementDelay = (goalTime / delay * delay);
        incrementDelay *= 1 + random.nextInt(3);
        incrementTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                increment();
            }
        }, incrementDelay);
    }

    public void cancel() {
        incrementTimer.cancel();
        beepTimer.cancel();
    }

    private void increment() {
        beepTimer.cancel();
        incrementTimer.cancel();
        delay -= delayChange;
        if (delay < finalDelay) {
            listener.onTimerUp();
        }
        else {
            restartTimers();
        }
    }

    public void setListener(BeepListener listener) {
        this.listener = listener;
    }

    interface BeepListener {
        void onTimerUp();
    }

    private class BeepTask extends TimerTask {

        @Override
        public void run() {
            sound.stop();
            sound.reloadStaticData();
            sound.play();
        }
    }
}