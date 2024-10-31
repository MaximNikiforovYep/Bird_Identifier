package com.example.birdidentifier.Fragments.microphoneFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;



public class SpectrogramView extends View {
    private final List<double[]> fftData;
    private final Paint paint;
    Bitmap bitmap;
    private Canvas canvas;
    private final int MAX_AMPLITUDES = 500;
    private int amplitudeWidth;
    int indicator = 0;
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
        drawFFTDataOnBitmap();
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, indicator, 0, paint);

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(getWidth(), amplitudeHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.BLACK); // Очищаем bitmap для новой отрисовки
    }

    private void drawFFTDataOnBitmap() {
        int fftDataLength = fftData.size();
        indicator = getWidth() - fftDataLength;
        for (int i = fftDataLength - 1; i < fftDataLength; ++i) {
            double[] data = fftData.get(i);
            for (int j = 0; j < 882; ++j) {
                int color = Color.rgb((int) data[j], 1, 1);
                paint.setColor(color);
                bitmap.setPixel(i, 882-j-1, color);
            }
        }
    }

}