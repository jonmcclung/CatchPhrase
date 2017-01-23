package com.lerenard.catchphrase;

import android.content.Context;
import android.media.AudioTrack;
import android.preference.PreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

import static com.lerenard.catchphrase.MainApplication.random;

/**
 * Created by mc on 12-Jan-17.
 */

class Beep {
    private static final String TAG = "test";
    private static final int
            initialDelay = 1300,
            finalDelay = 300,
            steps = 10,
            delayChange = (initialDelay - finalDelay) / (steps + 1);
    private static int
            howLong,
            goalTime;
    private Timer beepTimer, incrementTimer;
    private int delay;
    private AudioTrack sound;
    private BeepListener listener;
    private boolean silent;
    private boolean cancelled;

    public Beep() {
        cancelled = true;
        silent = true;
        sound = SoundGenerator.generate(.05, 932);
        Context context = MainApplication.getContext();
        setHowLong(Integer.parseInt(
                PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getString(
                                context.getString(R.string.seconds_in_round_key),
                                context.getString(R.string.seconds_in_round_default_value))));
    }

    public static void setHowLong(int howLong) {
        Beep.howLong = 1000 * howLong;
        goalTime = Beep.howLong / steps;
    }

    public void restart() {
        cancelled = false;
        silent = false;
        delay = initialDelay;
        restartTimers();
    }

    private void restartTimers() {
        if (!silent) {
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
    }

    public void playCancelSound() {
        double[] cancelFrequencies = {466, 1, 466, 1, 466};
        double[] cancelDurations = {.2, .2, .2, .4, .4};
        SoundGenerator.generate(cancelDurations, cancelFrequencies).play();
    }

    public void cancel() {
        if (!silent && !cancelled) {
            playCancelSound();
        }
        cancelTimers();
    }

    private void cancelTimers() {
        if (!cancelled) {
            cancelled = true;
            incrementTimer.cancel();
            beepTimer.cancel();
        }
    }

    public void interrupt() {
        cancelTimers();
        if (!silent) {
            /*
            a#: 932
            a: 880
            g#: 831
            d#: 622
            */
            double[] interruptFrequencies = {932, 880, 831, 622, 1, 622};
            double[] interruptDurations = {.2, .2, .2, .2, .1, .2};
            SoundGenerator.generate(interruptDurations, interruptFrequencies).play();
        }
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

    /**
     * quits any noise it is currently making as well as stops making noise in the future until
     * restarted.
     */
    public void silence() {
        silent = true;
        cancelTimers();
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