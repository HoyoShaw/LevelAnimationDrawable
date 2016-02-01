package com.github.hoyo.library;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.Interpolator;

public abstract class LevelAnimationDrawable extends Drawable implements Runnable {

    public static final int ANIM_STYLE_DIRECT = 0;
    public static final int ANIM_STYLE_BOUNCE = 1;

    public static final int FRAME_TYPE_TWEEN = 0;
    public static final int FRAME_TYPE_FRAME = 1;

    private final int animStyle;

    private int mStartLevel;
    private int mTargetLevel;

    private final AnimDriver driver;

    private AnimationCallback callback;

    private boolean forever = false;

    private boolean isDied = false;

    public LevelAnimationDrawable() {
        this(ANIM_STYLE_BOUNCE, FRAME_TYPE_FRAME);
    }

    public LevelAnimationDrawable(int animStyle, int frameType) {
        this.animStyle = animStyle;
        driver = frameType == FRAME_TYPE_FRAME ? new FrameDriver() : new TweenDriver();
    }

    public void setDuration(long duration) {
        driver.duration = duration;
    }

    public void setInterpolator(Interpolator i) {
        driver.interpolator = i;
    }

    public void setFPS(float fps) {
        if (fps < 30) {
            fps = 30;
        }

        driver.frameTime = (int) (1000 / fps);
    }

    protected long getFrameTime() {
        return driver.frameTime;
    }

    public void setAnimationCallback(AnimationCallback cbk) {
        callback = cbk;
    }

    public void die() {
        isDied = true;

        unscheduleSelf(this);

        callback = null;
    }

    @Override
    protected boolean onLevelChange(int level) {
        AnimationCallback cbk = getAnimCallback();
        if (cbk != null) {
            cbk.onLevelChanged(level);
        }
        invalidateSelf();
        return true;
    }

    public void animateTo(float percent) {
        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }

        mStartLevel = getLevel();
        mTargetLevel = (int) (percent * 100);

        if (animStyle == ANIM_STYLE_DIRECT && mStartLevel == mTargetLevel) {
            endAnimation();
            return;
        }

        driver.reset();

        onAnimationStart();
        run();
    }

    public void animateForever() {
        forever = true;
        driver.reset();
        run();
    }

    protected void onAnimationStart() {
    }

    private void endAnimation() {
        AnimationCallback cbk = getAnimCallback();
        if (cbk != null) {
            cbk.onAnimationEnd();
        }
    }

    private AnimationCallback getAnimCallback() {
        return isDied ||callback == null ? null : callback;
    }

    @Override
    public void run() {

        //针对循环动画
        if (forever && !isDied) {

            invalidateSelf();

            driver.schedule(this, this);
            return;
        }

        float normalizedTime = driver.getNormalizedTime();

        if (normalizedTime >= 1) {
            setLevel(mTargetLevel);
            endAnimation();
            return;
        }

        normalizedTime = driver.getInterpolation(normalizedTime);

        final int level;
        if (animStyle == ANIM_STYLE_BOUNCE) {
            final float ratioThres = mStartLevel * 1.0f / (mTargetLevel + mStartLevel);
            level = (int) (normalizedTime < ratioThres ? mStartLevel * (1 - normalizedTime / ratioThres) : mTargetLevel * (normalizedTime - ratioThres) / (1 - ratioThres));
        } else {
            level = (int) (mStartLevel + (mTargetLevel - mStartLevel) * normalizedTime);
        }


        setLevel(level);

        if (!isDied) {
            driver.schedule(this, this);
        }
    }

    private static abstract class AnimDriver {

        long duration = 2000;
        Interpolator interpolator;
        long frameTime = 1000 / 60; // 60fps
        Handler mHandler = new Handler(Looper.getMainLooper());

        public void schedule(Drawable d, Runnable r) {
            mHandler.postAtTime(r, SystemClock.uptimeMillis() + frameTime);
        }

        float getInterpolation(float ratio) {
            if (ratio > 1) {
                ratio = 1;
            }

            if (interpolator != null) {
                ratio = interpolator.getInterpolation(ratio);
            }

            return ratio;
        }

        public abstract float getNormalizedTime();
        public abstract void reset();
    }

    private static class TweenDriver extends AnimDriver {

        private long startTime = -1;

        @Override
        public float getNormalizedTime() {
            if (duration == 0) {
                return 1;
            }

            long current = SystemClock.uptimeMillis();

            if (startTime == -1) {
                startTime = current;
            }

            final float normalizedTime = (current - startTime) * 1.0f / duration;

            return normalizedTime;
        }

        @Override
        public void reset() {
            startTime = -1;
        }

    }


    private static class FrameDriver extends AnimDriver {

        private long animatedTime = 0;

        @Override
        public float getNormalizedTime() {
            if (duration == 0) {
                return 1;
            }

            final float normalizedTime = animatedTime * 1.0f / duration;

            if (normalizedTime < 1) {
                animatedTime += frameTime;
            }

            return normalizedTime;
        }

        @Override
        public void reset() {
            animatedTime = 0;
        }

    }

    public static interface AnimationCallback {
        /**
         * @param level from 0 to 100
         * */
        public void onLevelChanged(int level);
        public void onAnimationEnd();
    }

}
