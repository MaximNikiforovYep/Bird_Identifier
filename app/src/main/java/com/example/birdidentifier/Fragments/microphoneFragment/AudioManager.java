package com.example.birdidentifier.Fragments.microphoneFragment;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.ByteArrayOutputStream;

class AudioManager {
    private static final int SAMPLE_RATE;
    private static final int BUFFER_SIZE;
    private static ByteArrayOutputStream audioOutputStream;
    private static AudioRecord audioRecord;
    private static AudioTrack audioTrack;
    private static boolean isRecording;
    private static boolean isPlaying;


    static {
        SAMPLE_RATE = 44100;
        BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioOutputStream = new ByteArrayOutputStream();
        audioRecord = null;
        audioTrack = null;
        isRecording = false;
        isPlaying = false;
    }

    @SuppressLint("MissingPermission")
    protected static void startRecording() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (isRecording) {
                    int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    Log.i("AudioManger", "bytesRead: " + bytesRead);
                    Log.i("AudioManger", "buffer size: " + BUFFER_SIZE);
                    if (bytesRead > 0) {
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
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
        if (audioOutputStream != null && audioOutputStream.size() > 0 && !isPlaying) {
            isPlaying = true;
            final byte[] audioData = audioOutputStream.toByteArray();

            audioTrack = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, audioData.length,
                    AudioTrack.MODE_STATIC);

            audioTrack.write(audioData, 0, audioData.length);
            audioTrack.play();

            audioTrack.setNotificationMarkerPosition(audioData.length / 2);
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
