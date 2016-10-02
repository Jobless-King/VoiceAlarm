package com.galaxy.voicealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ClearAlarm extends AppCompatActivity {

    private ImageView clear;
    private Animation translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_alarm);

        clear = (ImageView)findViewById(R.id.Clear);

        translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        clear.startAnimation(translate);
    }
    public void Kill(View view){
        finish();
    }
}
