package com.example.routewisecollection.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.R;
import com.example.routewisecollection.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    private EditText etCompanyName, etCompanyAddress, etCompanyPhone, etDefaultInterestRate;
    private CheckBox cbSmsEnabled, cbPrintEnabled;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        loadSettings();
        setupClickListeners();

        // Animate header and setting cards for a modern feel
        View header = findViewById(R.id.headerSettings);
        View cardCompany = findViewById(R.id.cardCompany);
        View cardDefaults = findViewById(R.id.cardDefaults);
        View cardFeatures = findViewById(R.id.cardFeatures);
        View btnSave = findViewById(R.id.btnSaveSettings);

        animateIn(header, 0);
        animateIn(cardCompany, 80);
        animateIn(cardDefaults, 140);
        animateIn(cardFeatures, 200);
        animateIn(btnSave, 260);
    }

    private void initializeViews() {
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);
        etCompanyPhone = findViewById(R.id.etCompanyPhone);
        etDefaultInterestRate = findViewById(R.id.etDefaultInterestRate);
        cbSmsEnabled = findViewById(R.id.cbSmsEnabled);
        cbPrintEnabled = findViewById(R.id.cbPrintEnabled);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    }

    private void loadSettings() {
        etCompanyName.setText(sharedPreferences.getString(Constants.PREF_COMPANY_NAME, Constants.DEFAULT_COMPANY_NAME));
        etCompanyAddress.setText(sharedPreferences.getString(Constants.PREF_COMPANY_ADDRESS, ""));
        etCompanyPhone.setText(sharedPreferences.getString(Constants.PREF_COMPANY_PHONE, ""));
        etDefaultInterestRate.setText(String.valueOf(sharedPreferences.getFloat(Constants.PREF_DEFAULT_INTEREST_RATE, (float) Constants.DEFAULT_INTEREST_RATE)));
        cbSmsEnabled.setChecked(sharedPreferences.getBoolean(Constants.PREF_SMS_ENABLED, false));
        cbPrintEnabled.setChecked(sharedPreferences.getBoolean(Constants.PREF_PRINT_ENABLED, false));
    }

    private void setupClickListeners() {
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void animateIn(View v, long delay) {
        if (v == null) return;
        v.setAlpha(0f);
        v.setTranslationY(24f);
        v.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(320)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void saveSettings() {
        String companyName = etCompanyName.getText().toString().trim();
        String companyAddress = etCompanyAddress.getText().toString().trim();
        String companyPhone = etCompanyPhone.getText().toString().trim();
        String interestRateStr = etDefaultInterestRate.getText().toString().trim();

        if (companyName.isEmpty()) {
            Toast.makeText(this, "BhishiGroup name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        float interestRate = interestRateStr.isEmpty() ? (float) Constants.DEFAULT_INTEREST_RATE : Float.parseFloat(interestRateStr);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_COMPANY_NAME, companyName);
        editor.putString(Constants.PREF_COMPANY_ADDRESS, companyAddress);
        editor.putString(Constants.PREF_COMPANY_PHONE, companyPhone);
        editor.putFloat(Constants.PREF_DEFAULT_INTEREST_RATE, interestRate);
        editor.putBoolean(Constants.PREF_SMS_ENABLED, cbSmsEnabled.isChecked());
        editor.putBoolean(Constants.PREF_PRINT_ENABLED, cbPrintEnabled.isChecked());
        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
