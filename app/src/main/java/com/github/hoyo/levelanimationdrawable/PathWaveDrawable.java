package com.github.hoyo.levelanimationdrawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;
import com.github.hoyo.library.LevelAnimationDrawable;

public class PathWaveDrawable extends LevelAnimationDrawable {
    private static final int WAVE_COLOR_RED_ONE = 0x99ff6273;
    private static final int WAVE_COLOR_RED_TWO = 0xe6ff6273;
    private static final int WAVE_COLOR_YELLOW_ONE = 0x99ff9727;
    private static final int WAVE_COLOR_YELLOW_TWO = 0xe6ff9727;
    private static final int WAVE_COLOR_BLUE_ONE = 0x991ecef3;
    private static final int WAVE_COLOR_BLUE_TWO = 0xe61ecef3;

    private static final int WAVE_COLOR_WHITE_ONE = 0x0affffff;
    private static final int WAVE_COLOR_WHITE_TWO = 0x0dffffff;

    private static final float WAVE_FREQUENCY = 4.0f;
    private static final float AMPLITUDE = 3.0f;
    private static final int FRAME_TIME = 1000 / 60; //60fps
    private static final int WAVE_SPEED = 3;

    private Paint wavePaint;
    private Paint secondWavePaint;
    private Path onePath;
    private Path secondPath;

    // 左右偏移 φ
    private int fai = 0;
    // 上下偏移
    private float k;
    // 角速度
    private float w = WAVE_FREQUENCY;
    // 振幅
    private float mAmplitude = AMPLITUDE;
    private float mSpeed = WAVE_SPEED;

    private int height;
    private int width;

    private Rect mBounds;
    private boolean isScheduleWave = false;
    private boolean isShowColorChange = true;

    private final Runnable waveRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isScheduleWave) {
                return;
            }
            fai += mSpeed;
            if (fai >= 360) {
                fai = 0;
            }
            invalidateSelf();
            scheduleSelf(waveRunnable, SystemClock.uptimeMillis() + FRAME_TIME);
        }
    };

    public PathWaveDrawable(Context context) {

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        wavePaint.setStrokeWidth(1f);
        secondWavePaint.setStrokeWidth(1f);

        onePath = new Path();
        secondPath = new Path();

        setAmplitude(AMPLITUDE);
        setOmega(WAVE_FREQUENCY);
        setWaveSpeed(WAVE_SPEED);

        setLevel(0);
    }

    public void setWaveSpeed(float waveSpeed) {
        this.mSpeed = waveSpeed;
    }

    public void setBounds(int width, int height) {
        mBounds = new Rect(0, 0, width, height);
        k = height;
    }

    @Override
    public void draw(Canvas canvas) {
        drawWave(canvas);
    }

    private void drawWave(Canvas canvas) {
        int level = getLevel();

        if (isShowColorChange) {
            if (level >= 86 * 100) {
                wavePaint.setColor(WAVE_COLOR_RED_ONE);
                secondWavePaint.setColor(WAVE_COLOR_RED_TWO);
            } else if (level >= 61 * 100) {
                wavePaint.setColor(WAVE_COLOR_YELLOW_ONE);
                secondWavePaint.setColor(WAVE_COLOR_YELLOW_TWO);
            } else {
                wavePaint.setColor(WAVE_COLOR_BLUE_ONE);
                secondWavePaint.setColor(WAVE_COLOR_BLUE_TWO);
            }
        } else {
            wavePaint.setColor(WAVE_COLOR_WHITE_ONE);
            secondWavePaint.setColor(WAVE_COLOR_WHITE_TWO);
        }

        drawPath(canvas);
    }

    /**
     * 是否允许颜色梯度
     *
     * @param change
     */
    public void setShowColorChange(boolean change) {
        isShowColorChange = change;
    }

    private void drawPath(Canvas canvas) {

        onePath.reset();
        secondPath.reset();

        int x = 0;
        float y1 = 0, y2 = 0;
        for (int i = 0; i <= width; i++) {
            x = i;
            y1 = (float) (mAmplitude * Math.sin((i * w - fai) * Math.PI / 180) + k);
            y2 = (float) (mAmplitude * Math.sin((i * w + fai) * Math.PI / 180) + k);
            if (i == 0) {
                onePath.moveTo(x, y1);
                secondPath.moveTo(x, y1);
            }
            onePath.quadTo(x, y1, x + 1, y1);
            secondPath.quadTo(x, y2, x + 1, y2);
        }
        onePath.lineTo(width, height);
        secondPath.lineTo(width, height);
        onePath.lineTo(0, height);
        secondPath.lineTo(0, height);
        onePath.close();
        secondPath.close();

        canvas.drawPath(onePath, wavePaint);
        canvas.drawPath(secondPath, secondWavePaint);
    }

    public void setAmplitude(float amplitude) {
        this.mAmplitude = amplitude;
    }

    public void setOmega(float w) {
        this.w = w;
    }

    public void scheduleWave() {
        isScheduleWave = true;
        scheduleSelf(waveRunnable, SystemClock.uptimeMillis());
    }

    public void unScheduleWave() {
        isScheduleWave = false;
        unscheduleSelf(waveRunnable);
    }

    public boolean isScheduleWave() {
        return isScheduleWave;
    }

    @Override
    protected boolean onLevelChange(int level) {
        float percent = getLevel() / 10000f;
        k = height * (1 - percent);
        return super.onLevelChange(level);
    }

    @Override
    public int getIntrinsicHeight() {
        return mBounds.height();
    }

    @Override
    public int getIntrinsicWidth() {
        return mBounds.width();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        width = mBounds.width();
        height = mBounds.height();
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

}
