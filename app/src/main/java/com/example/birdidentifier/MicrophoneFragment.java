package com.example.birdidentifier;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.birdidentifier.databinding.FragmentMicrophoneBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MicrophoneFragment extends Fragment {
    private FragmentMicrophoneBinding binding;
    private Context context;
    private String audioSavePath;
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    private ByteArrayOutputStream audioOutputStream;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMicrophoneBinding.inflate(inflater, container, false);
        context = getActivity();
        assert context != null;

        try {
            File[] directories = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
            if (directories == null || directories.length == 0 || directories[0] == null)
                throw new IOException("Directory not found");
            audioSavePath = directories[0].getAbsolutePath() + "/audio.pcm";
        } catch (IOException e) {
            Toast.makeText(context, "Can't run app", Toast.LENGTH_SHORT).show();
            if ((e + "").equals("java.io.IOException: Directory not found"))
                Toast.makeText(context, "Directory not found", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
            Log.e("Can't run app", e + "");
            getActivity().finish();
        }

        Log.i("Audio path", audioSavePath);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private boolean checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    // Функции для записи и воспроизведения аудио
    private void startRecording() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        audioOutputStream = new ByteArrayOutputStream(); // Сбросим предыдущие данные

        audioRecord.startRecording();
        isRecording = true;

        // Запись в отдельном потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (isRecording) {
                    int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        // Сохраняем данные в ByteArrayOutputStream
                        audioOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }).start();
    }

    // Остановка записи
    private void stopRecording() {
        if (audioRecord != null) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    // Воспроизведение записи
    private void playRecording() {
        if (audioOutputStream != null && audioOutputStream.size() > 0) {
            final byte[] audioData = audioOutputStream.toByteArray(); // Получаем записанные данные

            audioTrack = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, audioData.length,
                    AudioTrack.MODE_STATIC);

            audioTrack.write(audioData, 0, audioData.length);
            audioTrack.play();
            isPlaying = true;
        }
    }

    // Остановка воспроизведения
    private void stopPlayback() {
        if (audioTrack != null) {
            isPlaying = false;
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
}
