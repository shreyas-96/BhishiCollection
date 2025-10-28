//package com.example.routewisecollection.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.routewisecollection.R;
//import com.example.routewisecollection.databinding.ActivityRegistrationBinding;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Objects;
//
//public class RegistrationActivity extends AppCompatActivity {
//
//    private ActivityRegistrationBinding binding;
//    private FirebaseAuth mAuth;
//    private DatabaseReference agentsRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Hide status and enable immersive mode
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN |
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );
//
//        try {
//            FirebaseApp.initializeApp(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Firebase initialization failed!", Toast.LENGTH_LONG).show();
//        }
//
//        mAuth = FirebaseAuth.getInstance();
//        agentsRef = FirebaseDatabase.getInstance().getReference("agents");
//
//        setupClickListeners();
//        setupBackPressHandler();
//    }
//
//    private void setupClickListeners() {
//        binding.btnRegister.setOnClickListener(v -> registerAgent());
//
//        binding.tvLoginLink.setOnClickListener(v -> {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        });
//    }
//
//    private void setupBackPressHandler() {
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                showExitConfirmation();
//            }
//        });
//    }
//
//    private void showExitConfirmation() {
//        new AlertDialog.Builder(this)
//                .setTitle("Cancel Registration")
//                .setMessage("Are you sure you want to cancel registration?")
//                .setPositiveButton("Yes", (dialog, which) -> finish())
//                .setNegativeButton("No", null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    }
//
//    private void registerAgent() {
//        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
//        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
//        String phone = Objects.requireNonNull(binding.etPhone.getText()).toString().trim();
//
//        if (!validateInput(email, password, phone)) return;
//
//        binding.btnRegister.setEnabled(false);
//        binding.progressBar.setVisibility(View.VISIBLE);
//
//        // Create user in Firebase Auth
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    binding.btnRegister.setEnabled(true);
//                    binding.progressBar.setVisibility(View.GONE);
//
//                    if (task.isSuccessful()) {
//                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                        if (firebaseUser != null) {
//                            saveAgentInfoToDatabase(firebaseUser.getUid(), email, phone);
//                            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
//                            finish();
//                        }
//                    } else {
//                        String error = task.getException() != null ?
//                            task.getException().getMessage() : "Registration failed";
//                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    private boolean validateInput(String email, String password, String phone) {
//        boolean isValid = true;
//
//        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.emailLayout.setError("Please enter a valid email");
//            isValid = false;
//        } else {
//            binding.emailLayout.setError(null);
//        }
//
//        if (TextUtils.isEmpty(password) || password.length() < 6) {
//            binding.passwordLayout.setError("Password must be at least 6 characters");
//            isValid = false;
//        } else {
//            binding.passwordLayout.setError(null);
//        }
//
//        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
//            binding.phoneLayout.setError("Please enter a valid 10-digit phone number");
//            isValid = false;
//        } else {
//            binding.phoneLayout.setError(null);
//        }
//
//        return isValid;
//    }
//
//    private void saveAgentInfoToDatabase(String agentId, String email, String phone) {
//        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//
//        Map<String, Object> agent = new HashMap<>();
//        agent.put("email", email);
//        agent.put("phone", phone);
//        agent.put("registrationDate", currentDate);
//        agent.put("status", "active");
//
//        agentsRef.child(agentId).setValue(agent)
//                .addOnSuccessListener(aVoid -> {
//                    // Send email verification
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    if (user != null) {
//                        user.sendEmailVerification()
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(this,
//                                        "Verification email sent to " + user.getEmail(),
//                                        Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    }
//                })
//                .addOnFailureListener(e ->
//                    Toast.makeText(this, "Failed to save agent info: " + e.getMessage(),
//                        Toast.LENGTH_SHORT).show());
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        binding = null;
//    }
//
//    private static class ActivityRegistrationBinding {
//    }
//}
