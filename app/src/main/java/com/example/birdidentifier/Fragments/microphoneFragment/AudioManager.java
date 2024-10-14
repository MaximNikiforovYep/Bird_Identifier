package com.example.birdidentifier.Fragments.microphoneFragment;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import org.jtransforms.fft.DoubleFFT_1D;

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
                double[] fftInput = new double[BUFFER_SIZE];
                DoubleFFT_1D fft = new DoubleFFT_1D(BUFFER_SIZE);
                int CYCLE = 0;
                while (isRecording) {
                    int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    /*++CYCLE;
                    Log.i("AudioManger", "bytesRead: " + bytesRead);
                    Log.i("AudioManger", "buffer size: " + BUFFER_SIZE);
                    for(int i = 0; i < bytesRead / 2; i++) {
                        short val=(short)(((buffer[i*2+1] & 0xFF) << 8) | (buffer[i*2] & 0xFF));
                        Log.i("AudioManger", "buffer[" + CYCLE + "]: " + "sample byte - " + (i+1) + " " + buffer[i*2] + " " + val);
                    }
                    for (int i = 0; i < bytesRead / 2; i++) {
                        fftInput[i] = buffer[i * 2] | (buffer[i * 2 + 1] << 8); // Конвертируем байты в значения амплитуды
                    }*/

//                    fft.realForward(fftInput);
                    if (bytesRead > 0) {
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
                    /*float[] magnitudes = new float[fftInput.length / 2];
                    for (int i = 0; i < magnitudes.length; i++) {
                        double real = fftInput[i * 2];
                        double imaginary = fftInput[i * 2 + 1];
                        magnitudes[i] = (float) Math.sqrt(real * real + imaginary * imaginary);
                    }*/
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
        if (audioOutputStream != null && audioOutputStream.size() > 0 && !isPlaying && !isRecording) {
            isPlaying = true;
            final byte[] audioData = audioOutputStream.toByteArray();
            short[] audioDataShort = new short[audioData.length / 2];
            for (int i =0; i < audioData.length / 2; i++) {
                audioDataShort[i] = (short) (((audioData[i*2+1] & 0xFF) << 8) | (audioData[i*2] & 0xFF));
            }
            audioTrack = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, audioData.length,
                    AudioTrack.MODE_STATIC);

            audioTrack.write(audioDataShort, 0, audioDataShort.length);
            audioTrack.play();

            audioTrack.setNotificationMarkerPosition(audioDataShort.length);
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
