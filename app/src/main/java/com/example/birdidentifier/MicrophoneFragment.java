package com.example.birdidentifier;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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
import java.io.IOException;

public class MicrophoneFragment extends Fragment {
    private FragmentMicrophoneBinding binding;
    private Context context;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String AudioSavePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMicrophoneBinding.inflate(inflater, container, false);
        context = getActivity();
        assert context != null;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
}
