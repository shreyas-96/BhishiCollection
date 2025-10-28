package com.example.routewisecollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.MainActivity;
import com.example.routewisecollection.R;
import com.example.routewisecollection.utils.LoginManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    
    private ImageView ivAppLogo;
    private TextView tvLoadingText;
    private ProgressBar progressBar;
    
    private Handler handler;
    private Runnable loadingTextRunnable;
    private int loadingStep = 0;
    private LoginManager loginManager;
    
    private String[] loadingMessages = {
        "Initializing...",
        "Loading Routes...",
        "Setting up Database...",
        "Preparing Interface...",
        "Almost Ready..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide status bar for full screen experience
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        initializeViews();
        startAnimations();
        startLoadingSequence();
        
        // Initialize login manager
        loginManager = new LoginManager(this);
        
        // Navigate to appropriate activity after splash duration
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextActivity();
        }, SPLASH_DURATION);
    }

    private void initializeViews() {
        ivAppLogo = findViewById(R.id.ivAppLogo);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void startAnimations() {
        // Logo bounce animation
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        ivAppLogo.startAnimation(bounceAnimation);
        
        // Fade in animation for other elements
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        tvLoadingText.startAnimation(fadeInAnimation);
        progressBar.startAnimation(fadeInAnimation);
    }

    private void startLoadingSequence() {
        handler = new Handler(Looper.getMainLooper());
        loadingTextRunnable = new Runnable() {
            @Override
            public void run() {
                if (loadingStep < loadingMessages.length) {
                    // Fade out current text
                    tvLoadingText.animate()
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                // Change text and fade in
                                tvLoadingText.setText(loadingMessages[loadingStep]);
                                tvLoadingText.animate()
                                        .alpha(1f)
                                        .setDuration(200)
                                        .start();
                                loadingStep++;
                            })
                            .start();
                    
                    // Schedule next message
                    handler.postDelayed(this, 600);
                }
            }
        };
        
        // Start the loading sequence after a short delay
        handler.postDelayed(loadingTextRunnable, 500);
    }

    private void navigateToNextActivity() {
        Intent intent;
        
        // Check if user is logged in
        if (loginManager.isLoggedIn()) {
            // User is logged in, go to MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User is not logged in, go to LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && loadingTextRunnable != null) {
            handler.removeCallbacks(loadingTextRunnable);
        }
    }
}
