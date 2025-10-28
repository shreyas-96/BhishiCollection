//package com.example.routewisecollection.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.routewisecollection.MainActivity;
//import com.example.routewisecollection.R;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class LoginActivity extends AppCompatActivity {
//
//    EditText etMobile, etPassword;
//    Button btnLogin;
//    ProgressBar progressBar;
//
//    DatabaseReference agentsRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        etMobile = findViewById(R.id.etMobile);
//        etPassword = findViewById(R.id.etPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//        progressBar = findViewById(R.id.progressBar);
//
//        agentsRef = FirebaseDatabase.getInstance().getReference("agents");
//
//        btnLogin.setOnClickListener(v -> {
//            String mobile = etMobile.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//
//            if (TextUtils.isEmpty(mobile)) {
//                etMobile.setError("Enter mobile number");
//                return;
//            }
//            if (TextUtils.isEmpty(password)) {
//                etPassword.setError("Enter password");
//                return;
//            }
//
//            progressBar.setVisibility(View.VISIBLE);
//            loginAgent(mobile, password);
//        });
//    }
//
//    private void loginAgent(String mobile, String password) {
//        agentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean found = false;
//
//                for (DataSnapshot agentSnap : snapshot.getChildren()) {
//                    DataSnapshot infoSnap = agentSnap.child("agentInfo");
//
//                    if (infoSnap.exists()) {
//                        String dbMobile = infoSnap.child("mobileNumber").getValue(String.class);
//                        String dbPassword = infoSnap.child("password").getValue(String.class);
//
//                        if (dbMobile != null && dbMobile.equals(mobile)) {
//                            found = true;
//                            if (dbPassword != null && dbPassword.equals(password)) {
//                                progressBar.setVisibility(View.GONE);
//
//                                // Get agent info
//                                String agentName = infoSnap.child("agentName").getValue(String.class);
//                                String route = infoSnap.child("route").getValue(String.class);
//                                String agentId = infoSnap.child("agentId").getValue(String.class);
//
//                                Toast.makeText(LoginActivity.this, "Welcome " + agentName, Toast.LENGTH_SHORT).show();
//
//                                // Go to next screen
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//                                intent.putExtra("agentId", agentId);
//                                intent.putExtra("agentName", agentName);
//                                intent.putExtra("route", route);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                progressBar.setVisibility(View.GONE);
//                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//                        }
//                    }
//                }
//
//                if (!found) {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(LoginActivity.this, "Agent not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//}


package com.example.routewisecollection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.MainActivity;
import com.example.routewisecollection.R;
import com.example.routewisecollection.utils.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etMobile, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private DatabaseReference agentsRef;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginManager = new LoginManager(this);

        // Auto-login if already authenticated
        if (loginManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        initializeViews();

        agentsRef = FirebaseDatabase.getInstance().getReference("agents");

        btnLogin.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(mobile)) {
                etMobile.setError("Enter mobile number");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter password");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginAgent(mobile, password);
        });
    }

    private void initializeViews() {
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loginAgent(String mobile, String password) {
        agentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot agentSnap : snapshot.getChildren()) {
                    DataSnapshot infoSnap = agentSnap.child("agentInfo");

                    if (infoSnap.exists()) {
                        String dbMobile = infoSnap.child("mobileNumber").getValue(String.class);
                        String dbPassword = infoSnap.child("password").getValue(String.class);

                        if (dbMobile != null && dbMobile.equals(mobile)) {
                            found = true;
                            if (dbPassword != null && dbPassword.equals(password)) {
                                progressBar.setVisibility(View.GONE);

                                String agentId = infoSnap.child("agentId").getValue(String.class);
                                String agentName = infoSnap.child("agentName").getValue(String.class);
                                String route = infoSnap.child("route").getValue(String.class);

                                // Save session locally
                                loginManager.saveLoginSession(agentId, agentName, mobile, route);

                                Toast.makeText(LoginActivity.this, "Welcome " + agentName, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }

                if (!found) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Agent not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
