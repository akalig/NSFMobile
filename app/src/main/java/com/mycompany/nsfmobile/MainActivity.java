package com.mycompany.nsfmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mycompany.nsfmobile.database.Database;

public class MainActivity extends AppCompatActivity {

    EditText eTUsername;
    EditText eTPassword;
    Button btnLogin;
    LottieAnimationView loadingAnimation;
    Database database; // Database instance for authentication
    ImageButton ipButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Check if the user is already logged in
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // User is already logged in, proceed to CreateDeliveryReceipt activity
            Intent intent = new Intent(MainActivity.this, CreateDeliveryReceipt.class);
            startActivity(intent);
            finish(); // Close the current activity to prevent going back to the login screen
        }

        // Initialize Database instance
        database = new Database();
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ipAddress");
        intent.putExtra("ipAddress",ipAddress);
        database.createConnection(ipAddress);

        eTUsername = (EditText) findViewById(R.id.et_username);
        eTPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        ipButton = (ImageButton) findViewById(R.id.ip_button);
        loadingAnimation = (LottieAnimationView) findViewById(R.id.loading_animation);
        loadingAnimation.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadingAnimation.setVisibility(View.VISIBLE);
//                        btnLogin.setVisibility(View.GONE);
//                    }
//                }, 5000);

                String username = eTUsername.getText().toString();
                String password = eTPassword.getText().toString();

                if (username.equals("")) {
                    eTUsername.setError("Please input your Username.");
                    eTUsername.requestFocus();
                } else if (password.equals("")) {
                    eTPassword.setError("Please input your Password.");
                    eTPassword.requestFocus();
                } else {
                    // Check username and password in the database
                    authenticateUser(username, password);
                }
            }
        });

        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.ip_dialog);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.container_background));
                }

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);

                Button submitIP = dialog.findViewById(R.id.btn_submit_ip_add);
                EditText submittedIP = dialog.findViewById(R.id.et_ip_add);

                submitIP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String ipAddress = submittedIP.getText().toString();

                        if (ipAddress.equals(null) && ipAddress.equals("")) {

                            Toast.makeText(MainActivity.this, "Please provide IP Address", Toast.LENGTH_SHORT).show();
                            submittedIP.setError("Enter IP Address");
                            submittedIP.requestFocus();

                        } else {

                            SharedPreferences sharedPreferences = getSharedPreferences("IPPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("savedIP", ipAddress);
                            editor.apply();

                            String savedIP = sharedPreferences.getString("savedIP", ipAddress);

                            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                            dialog.dismiss();

                        }
                    }
                });

                dialog.show();

            }
        });
    }

    private void authenticateUser(String username, String password) {
        // Create an instance of the Database class
        Database database = new Database();
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ipAddress");
        database.createConnection(ipAddress);

        // Perform the authentication in a separate thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Retrieve the user's password from the database
                String storedPassword = database.getUserPassword(username);

                // Check if the username exists in the database
                if (storedPassword == null) {
                    showToast("Username doesn't exist.");
                } else {
                    // Check if the entered password matches the stored password
                    if (password.equals(storedPassword)) {
                        // Successful authentication
                        showToast("Authentication successful.");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.apply();

                        // Add your code to proceed to the next activity or perform any other action
                        Intent intent2 = getIntent();
                        String ip = intent2.getStringExtra("ipAddress");

                        Intent intent = new Intent(MainActivity.this, CreateDeliveryReceipt.class);
                        intent.putExtra("ipAddress",ip);
                        intent.putExtra("loggedUser", username);
                        startActivity(intent);
                        finish();

                    } else {
                        // Incorrect password
                        showToast("Password is incorrect.");
                    }
                }
            }
        });
        thread.start();
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}