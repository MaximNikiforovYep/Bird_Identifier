package com.example.birdidentifier.Fragments.microphoneFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class SpectrogramView extends View {
    private float[] fftData;
    private Paint paint;

    public SpectrogramView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    public SpectrogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    public void updateFFTData(float[] data) {
        this.fftData = data;
        invalidate(); // Перерисовываем график
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (fftData != null) {
            int width = getWidth();
            int height = getHeight();
            float barWidth = (float) width / fftData.length;

            for (int i = 0; i < fftData.length; i++) {
                float value = fftData[i];
                float barHeight = value * height; // Пропорциональное отображение высоты

                canvas.drawRect(i * barWidth, height - barHeight, (i + 1) * barWidth, height, paint);
            }
        }
    }
}