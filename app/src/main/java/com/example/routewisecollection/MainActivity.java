//package com.example.routewisecollection;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//
//import com.example.routewisecollection.activities.AddCustomerActivity;
//import com.example.routewisecollection.activities.CustomerInfoActivity;
//import com.example.routewisecollection.activities.DepositActivity;
//import com.example.routewisecollection.activities.LoginActivity;
//import com.example.routewisecollection.activities.ReportsActivity;
//import com.example.routewisecollection.activities.RouteListActivity;
//import com.example.routewisecollection.activities.SettingsActivity;
//import com.example.routewisecollection.utils.LoginManager;
//
//public class MainActivity extends AppCompatActivity {
//
//    private CardView cardRoutes, cardAddCustomer, cardDeposit, cardReports, cardSettings;
//    private LoginManager loginManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize login manager
//        loginManager = new LoginManager(this);
//
//        initializeViews();
//        setupClickListeners();
//        animateCards();
//    }
//
//    private void initializeViews() {
//        cardRoutes = findViewById(R.id.cardRoutes);
//        cardAddCustomer = findViewById(R.id.cardAddCustomer);
//        cardDeposit = findViewById(R.id.cardDeposit);
//        cardReports = findViewById(R.id.cardReports);
//        cardSettings = findViewById(R.id.cardSettings);
//    }
//
//    private void setupClickListeners() {
//        cardRoutes.setOnClickListener(v -> {
//            animateCardClick(v);
//            startActivityWithAnimation(RouteListActivity.class);
//        });
//
//        cardAddCustomer.setOnClickListener(v -> {
//            animateCardClick(v);
//            startActivityWithAnimation(CustomerInfoActivity.class);
//        });
//
//        cardDeposit.setOnClickListener(v -> {
//            animateCardClick(v);
//            startActivityWithAnimation(DepositActivity.class);
//        });
//
//        cardReports.setOnClickListener(v -> {
//            animateCardClick(v);
//            startActivityWithAnimation(ReportsActivity.class);
//        });
//
//        cardSettings.setOnClickListener(v -> {
//            animateCardClick(v);
//            startActivityWithAnimation(SettingsActivity.class);
//        });
//    }
//
//    private void animateCards() {
//        // Animate cards on entry with staggered delay
//        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
//
//        cardRoutes.startAnimation(slideUp);
//
//        cardAddCustomer.postDelayed(() -> cardAddCustomer.startAnimation(fadeIn), 100);
//        cardDeposit.postDelayed(() -> cardDeposit.startAnimation(fadeIn), 200);
//        cardReports.postDelayed(() -> cardReports.startAnimation(fadeIn), 300);
//        cardSettings.postDelayed(() -> cardSettings.startAnimation(fadeIn), 400);
//    }
//
//    private void animateCardClick(View view) {
//        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
//        view.startAnimation(scaleUp);
//    }
//
//    private void startActivityWithAnimation(Class<?> activityClass) {
//        Intent intent = new Intent(MainActivity.this, activityClass);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_logout) {
//            performLogout();
//            return true;
//        } else if (id == R.id.action_profile) {
//            showUserProfile();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void performLogout() {
//        // Clear login session
//        loginManager.logout();
//
//        // Show logout message
//        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
//
//        // Navigate to login activity
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        finish();
//    }
//
//    private void showUserProfile() {
//        String agentName = loginManager.getAgentName();
//        String agentMobile = loginManager.getAgentMobile();
//
//        String message = "Logged in as:\n" + agentName + "\n" + agentMobile;
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//}


package com.example.routewisecollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.routewisecollection.activities.CustomerInfoActivity;
import com.example.routewisecollection.activities.DepositActivity;
import com.example.routewisecollection.activities.LoginActivity;
import com.example.routewisecollection.activities.ReportsActivity;
import com.example.routewisecollection.activities.RouteListActivity;
import com.example.routewisecollection.activities.SettingsActivity;
import com.example.routewisecollection.utils.LoginManager;

public class MainActivity extends AppCompatActivity {

    private CardView cardRoutes, cardAddCustomer, cardDeposit, cardReports, cardSettings;
    private ImageButton btnLogout;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginManager = new LoginManager(this);

        // Redirect to login if not logged in
        if (!loginManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        initializeViews();
        setupClickListeners();
        animateCards();

        // Initialize and set logout button click listener
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void initializeViews() {
        cardRoutes = findViewById(R.id.cardRoutes);
        cardAddCustomer = findViewById(R.id.cardAddCustomer);
        cardDeposit = findViewById(R.id.cardDeposit);
        cardReports = findViewById(R.id.cardReports);
        cardSettings = findViewById(R.id.cardSettings);
    }

    private void setupClickListeners() {
        cardRoutes.setOnClickListener(v -> startActivityWithAnimation(RouteListActivity.class));
        cardAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomerInfoActivity.class);
            intent.putExtra("agentId", loginManager.getAgentId());
            startActivity(intent);
        });
        cardDeposit.setOnClickListener(v -> startActivityWithAnimation(DepositActivity.class));
        cardReports.setOnClickListener(v -> startActivityWithAnimation(ReportsActivity.class));
        cardSettings.setOnClickListener(v -> startActivityWithAnimation(SettingsActivity.class));
    }

    private void animateCards() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        cardRoutes.startAnimation(slideUp);

        cardAddCustomer.postDelayed(() -> cardAddCustomer.startAnimation(fadeIn), 100);
        cardDeposit.postDelayed(() -> cardDeposit.startAnimation(fadeIn), 200);
        cardReports.postDelayed(() -> cardReports.startAnimation(fadeIn), 300);
        cardSettings.postDelayed(() -> cardSettings.startAnimation(fadeIn), 400);
    }

    private void startActivityWithAnimation(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            performLogout();
            return true;
        } else if (id == R.id.action_profile) {
            showUserProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        // Clear login session
        loginManager.logout();

        // Feedback
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to login and prevent back press
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void showUserProfile() {
        String message = "Agent: " + loginManager.getAgentName() +
                "\nMobile: " + loginManager.getAgentMobile() +
                "\nRoute: " + loginManager.getRoute();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
