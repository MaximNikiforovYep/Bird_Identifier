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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MicrophoneFragment extends Fragment {
    private FragmentMicrophoneBinding binding;
    private Context context;
    private MediaPlayer mediaPlayer;
    private String audioSavePath;

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

        RecordAudio.setAudioSavePath(audioSavePath);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAudio.startRecording();
            }
        });

        /*binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioRecorder != null) {
                    audioRecorder.stop();
                    audioRecorder.release();
                    audioRecorder = null;
                }
            }
        });*/

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,       // Поток для музыки
                        44100,                      // Частота дискретизации (та же, что и при записи)
                        AudioFormat.CHANNEL_OUT_MONO,    // Моно
                        AudioFormat.ENCODING_PCM_16BIT,  // Формат PCM 16-бит
                        RecordAudio.getBufferSize(),                      // Размер буфера (тот же, что и при записи)
                        AudioTrack.MODE_STREAM);         // Используем потоковый режим

// Воспроизведение данных
                File audioFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "audio.pcm");
                try {
                    FileInputStream fis = new FileInputStream(audioFile);

// Буфер для чтения данных из файла
                    byte[] audioBuffer = new byte[RecordAudio.getBufferSize()];

// Стартуем воспроизведение
                    audioTrack.play();

// Чтение и воспроизведение аудиоданных
                    int bytesRead;
                    while ((bytesRead = fis.read(audioBuffer)) != -1) {
                        audioTrack.write(audioBuffer, 0, bytesRead);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

// Остановка и освобождение ресурсов
                audioTrack.stop();
                audioTrack.release();
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    Toast.makeText(getActivity(), "Playing stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });





































        /*
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    try {
                        File[] directories = context.getExternalFilesDirs(Environment.DIRECTORY_MUSIC);
                        if (directories == null || directories.length == 0 || directories[0] == null)
                            throw new IOException("Directory not found");
                        AudioSavePath = directories[0].getAbsolutePath() + "/audio.3gp";

                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setOutputFile(AudioSavePath);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        Toast.makeText(context, "Recording failed", Toast.LENGTH_SHORT).show();
                        if ((e + "").equals("java.io.IOException: Directory not found"))
                            Toast.makeText(context, "Directory not found", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
                        Log.e("Error recording", e + "");
                    }


                } else {
                    Activity activity = getActivity();
                    assert activity != null;
                    ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                }
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    Toast.makeText(getActivity(), "Recording stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(AudioSavePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(context, "Playing started", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "Playing failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
                    Log.e("Error playing", e + "");
                }
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    Toast.makeText(getActivity(), "Playing stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */
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

    private static class RecordAudio {
        private static boolean isRecording = false;
        private static boolean isPaused = false;
        private static AudioRecord audioRecorder = null;

        public static int getBufferSize() {
            return bufferSize;
        }

        private static int bufferSize;
        private static String audioSavePath;
        public static void setAudioSavePath(String audioSavePathTmp) {
            audioSavePath = audioSavePathTmp;
        }

        public static void startRecording() {
            if (!isRecording && audioRecorder == null) {
                int sampleRate = 44100;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

                int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                @SuppressLint("MissingPermission")
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        sampleRate, channelConfig, audioFormat, bufferSize);

                short[] audioData = new short[bufferSize];
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(audioSavePath);
                    audioRecord.startRecording();
                    for (int i = 0; i < sampleRate * 5 / bufferSize; i++) {
                        int read = audioRecord.read(audioData, 0, bufferSize);
                        if (read > 0) {
                            byte[] audioBytes = new byte[read * 2]; // Каждое значение short занимает 2 байта
                            for (int j = 0; j < read; j++) {
                                audioBytes[j * 2] = (byte) (audioData[j] & 0x00FF);
                                audioBytes[j * 2 + 1] = (byte) ((audioData[j] >> 8) & 0xFF);
                            }
                            outputStream.write(audioBytes); // Запись данных в файл
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }

            }
            //bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            //audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                   // AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            //audioRecorder.startRecording();
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    short[] audioBuffer = new short[bufferSize];
                    while (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        audioRecorder.read(audioBuffer, 0, audioBuffer.length);
                        // Здесь можно добавить код для анализа аудиопотока
                    }
                }
            }).start();*/
        }
    }
}
