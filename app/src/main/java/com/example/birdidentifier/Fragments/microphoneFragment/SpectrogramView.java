package com.example.birdidentifier.Fragments.microphoneFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


SurfaceView
public class SpectrogramView extends View {
    private final List<double[]> fftData;
    private final Paint paint;

    private final int MAX_AMPLITUDES = 500;
    private int amplitudeWidth;
    private final int amplitudeHeight = 882;

    public SpectrogramView(Context context) {
        super(context);
        paint = new Paint();
        fftData = new ArrayList<>();
    }

    public SpectrogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        fftData = new ArrayList<>();
    }

    public void updateFFTData(double[] data) {
        /*for (int i = 0; i < data.length; i++) {
            data[i] = (data[i]>2.5? 250f: data[i] * 100);
        }*/
        fftData.add(data);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        amplitudeWidth = width / MAX_AMPLITUDES;

        int fftDataLength = fftData.size();

        for (int i = 0; i < fftDataLength; ++i) {
            double[] data = fftData.get(i);
            for (int j = 0; j < data.length; ++j) {
                int color = Color.rgb((int) data[j], 1, 1);
                paint.setColor(color);
                if(i <= width) {
                    int left = i * amplitudeWidth;
                    int top = (int) ((1.0 - (j / (double) amplitudeHeight)) * height);
                    int right = left + amplitudeWidth;
                    int bottom = (int) ((1.0 - ((j + 1) / (double) amplitudeHeight)) * height);
                    canvas.drawRect(left, top, right, bottom, paint);
                    //canvas.drawLine((float) width / 100f * i, (float) j * height / (float) data.length, (float) width / 100f * i, ((float) j + 1f) * height / (float) data.length, paint);
                } else {
                    //canvas.drawLine(width, (float) j * height / (float) data.length, width, ((float) j + 1f) * height / (float) data.length, paint);
                }

            }
        }

        /*paint.setStrokeWidth(5);

        canvas.drawLine(0, getHeight()/2f, getWidth()/100f, getHeight()/2f, paint);
        paint.setColor(Color.BLUE);
        canvas.drawLine(getWidth()/100f, getHeight()/2f, 2*getWidth()/100f, getHeight()/2f, paint);*/


        /*int width = getWidth();
        int height = getHeight();
        float barWidth = (float) width / fftData.length;

        for (int i = 0; i < fftData.length; i++) {
            double value = fftData[i];
            double barHeight = value * height; // Пропорциональное отображение высоты

            canvas.drawRect(i * barWidth, (float) (height - barHeight), (i + 1) * barWidth, height, paint);
        }*/
    }
}