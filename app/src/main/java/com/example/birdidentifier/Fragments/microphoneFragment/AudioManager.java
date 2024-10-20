package com.example.birdidentifier.Fragments.microphoneFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.UiThread;

import com.example.birdidentifier.containers.ShortArray;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class AudioManager {
    private static final int SAMPLE_RATE;

    private static final int BUFFER_SIZE_BYTES;
    private static final int BUFFER_SIZE_SHORTS;
    private static final ShortArray audioOutput;

    private static AudioRecord audioRecord;
    private static AudioTrack audioTrack;

    private static boolean isRecording;
    private static boolean isPlaying;

    private static final Handler mainHandler;

    static {
        SAMPLE_RATE = 44100;

        BUFFER_SIZE_BYTES = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        BUFFER_SIZE_SHORTS = BUFFER_SIZE_BYTES / 2;
        audioOutput = new ShortArray();

        audioRecord = null;
        audioTrack = null;

        isRecording = false;
        isPlaying = false;

        mainHandler = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("MissingPermission")
    protected static void startRecording(Context context, SpectrogramView spectrogramView) {
        if (isRecording) stopRecording();
        if (isPlaying) stopPlayback();

        isRecording = true;

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE_BYTES);
        audioRecord.startRecording();

        new Thread(() -> {
            short[] buffer = new short[BUFFER_SIZE_SHORTS];
            while (isRecording) {
                int shortsRead = audioRecord.read(buffer, 0, BUFFER_SIZE_SHORTS);
                if (shortsRead > 0) {
                    audioOutput.write(buffer, 0, shortsRead);
                    double[] fft = new double[BUFFER_SIZE_SHORTS * 2];
                    for (int i = 0; i < shortsRead; ++i) {
                        fft[i*2] = (double) buffer[i] / 32768.0;
                        fft[i*2+1] = 0.0;
                    }
                    (new DoubleFFT_1D(BUFFER_SIZE_SHORTS)).complexForward(fft);
                    double[] amplitudes = new double[BUFFER_SIZE_SHORTS];
                    for (int i = 0; i < BUFFER_SIZE_SHORTS; ++i) {
                        double amplitude = Math.sqrt(fft[i*2]*fft[i*2] + fft[i*2+1]*fft[i*2+1]);
                        amplitudes[i] = amplitude > 2.5? 250: amplitude * 100;
                    }
                    mainHandler.post(() -> spectrogramView.updateFFTData(amplitudes));
                }
            }
        }).start();
    }

    protected static void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    protected static void playRecording() {
        if (isRecording) stopRecording();
        if (isPlaying) stopPlayback();

        if (audioOutput != null && audioOutput.size() > 0) {
            isPlaying = true;
            final short[] audioData = audioOutput.toShortArray();

            audioTrack = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, audioData.length * 2,
                    AudioTrack.MODE_STATIC);

            audioTrack.write(audioData, 0, audioData.length);
            audioTrack.play();

            audioTrack.setNotificationMarkerPosition(audioData.length);
            audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    stopPlayback();
                }
                @Override
                public void onPeriodicNotification(AudioTrack track) {}
            });
        }
    }

    protected static void stopPlayback() {
        if (audioTrack != null) {
            isPlaying = false;
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
}
