package com.lerenard.catchphrase;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

/**
 * Created by mc on 12-Jan-17.
 */

public class SoundGenerator {
    public static AudioTrack generate(final double duration, final double frequency) {
        int sampleRate = 44100;
        final int sampleCount = (int) (duration * sampleRate);
        final double sample[] = new double[sampleCount];
        final byte generatedSound[] = new byte[2 * sampleCount];
        // fill out the array
        for (int i = 0; i < sampleCount; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequency));
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
