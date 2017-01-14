package com.lerenard.catchphrase;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Locale;

/**
 * Created by mc on 12-Jan-17.
 */

public class SoundGenerator {
    private static final int sampleRate = 44100;

    public static AudioTrack generate(final double duration, final double frequency) {
        return generate(new double[]{duration}, new double[]{frequency});
    }

    public static AudioTrack generate(final double[] durations, final double[] frequencies) {
        if (durations.length != frequencies.length) {
            throw new IllegalArgumentException(String.format(
                    Locale.US,
                    "durations and frequencies have differing lengths: %d and %d",
                    durations.length,
                    frequencies.length));
        }

        double totalSampleCount = 0;

        for (double duration1 : durations) {
            totalSampleCount += duration1;
        }
        totalSampleCount *= sampleRate;

        final double sample[] = new double[(int) Math.ceil(totalSampleCount)];
        final byte generatedSound[] = new byte[2 * ((int) Math.ceil(totalSampleCount))];
        int runningIndex = 0;

        for (int i = 0; i < durations.length; ++i) {
            final double duration = durations[i];
            final double frequency = frequencies[i];
            final int sampleCount = (int) Math.floor(duration * sampleRate);
            // fill out the array
            for (int j = 0; j < sampleCount; ++j) {
                sample[runningIndex + j] =
                        Math.sin(2 * Math.PI * (runningIndex + j) / (sampleRate / frequency));
            }
            runningIndex += sampleCount;
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        AudioTrack res = new AudioTrack(AudioManager.STREAM_MUSIC,
                                        sampleRate, AudioFormat.CHANNEL_IN_DEFAULT,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        generatedSound.length,
                                        AudioTrack.MODE_STATIC);
        res.write(generatedSound, 0, generatedSound.length);
        return res;
    }
}
