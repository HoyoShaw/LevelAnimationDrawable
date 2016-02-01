package com.github.hoyo.levelanimationdrawable;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.github.hoyo.library.LevelAnimationDrawable;

public class ScrollingActivity extends AppCompatActivity {

    private PathWaveDrawable waveDrawable;
    private ImageView vWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        vWaveView = (ImageView) findViewById(R.id.iv_wave);
        ViewTreeObserver vto = vWaveView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vWaveView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                startWave(vWaveView.getWidth(),vWaveView.getHeight());
            }
        });

    }

    private void startWave(int width,int height){
        if (waveDrawable == null){
            waveDrawable = new PathWaveDrawable(this);
            waveDrawable.setBounds(width, height);
            waveDrawable.setDuration(1000);
            waveDrawable.animateTo(60);
            waveDrawable.setAmplitude(getResources().getDimensionPixelOffset(R.dimen.wave_almplitude));
            waveDrawable.setOmega(getResources().getDimensionPixelOffset(R.dimen.wave_omega));
            waveDrawable.setAnimationCallback(new LevelAnimationDrawable.AnimationCallback() {

                @Override
                public void onLevelChanged(int level) {

                }

                @Override
                public void onAnimationEnd() {
                    if (!waveDrawable.isScheduleWave()) {
                        waveDrawable.scheduleWave();
                    }
                }
            });
            vWaveView.setImageDrawable(waveDrawable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waveDrawable != null){
            waveDrawable.unScheduleWave();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
