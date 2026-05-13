package com.example.mescourses.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mescourses.R;
import com.example.mescourses.models.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvName    = findViewById(R.id.tvAppName);
        TextView tvTagline = findViewById(R.id.tvTagline);

        // Animation fade-in + slide-up
        animateView(tvName, 0);
        animateView(tvTagline, 300);

        // Redirection après 2 secondes
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(this, MainActivity.class);
            }
            else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void animateView(View view, long delay) {
        view.setTranslationY(40f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator slide = ObjectAnimator.ofFloat(view, "translationY", 40f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha, slide);
        set.setDuration(700);
        set.setStartDelay(delay);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }
}
