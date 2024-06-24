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
import android.widget.Toast;

import com.mycompany.nsfmobile.database.Database;

public class SplashScreen extends AppCompatActivity {

//    EditText etIpAddress;
//    Button btnSubmitIP;
//    String ipAddress = "";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String username = preferences.getString("username", "");
        System.out.println("Logged User: " + username);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences("IPPref", MODE_PRIVATE);
                String savedIP = sharedPreferences.getString("savedIP", "");

                if (savedIP.equals("")) {
                    Dialog dialog = new Dialog(SplashScreen.this);
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

                                Toast.makeText(SplashScreen.this, "Please provide IP Address", Toast.LENGTH_SHORT).show();
                                submittedIP.setError("Enter IP Address");
                                submittedIP.requestFocus();

                            } else {

                                SharedPreferences sharedPreferences = getSharedPreferences("IPPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("savedIP", ipAddress);
                                editor.apply();

                                String savedIP = sharedPreferences.getString("savedIP", ipAddress);

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
                                boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

                                String username = preferences.getString("username", "");

                                System.out.println("Logged User: " + username);

                                if (isLoggedIn) {
                                    // User is already logged in, proceed to CreateDeliveryReceipt activity
                                    Intent intent = new Intent(SplashScreen.this, CreateDeliveryReceipt.class);
                                    intent.putExtra("ipAddress", savedIP);
                                    startActivity(intent);
                                    finish(); // Close the current activity to prevent going back to the login screen

                                    if (savedIP != null || !savedIP.equals("")) {
                                        Database database = new Database();
                                        database.createConnection(savedIP);
                                    }
                                } else {
                                    if (savedIP != null || !savedIP.equals("")) {
                                        Database database = new Database();
                                        database.createConnection(savedIP);
                                    }

                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    intent.putExtra("ipAddress", savedIP);
                                    startActivity(intent);
                                    finish();
                                }

                                dialog.dismiss();

                            }
                        }
                    });

                    dialog.show();

                } else {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
                    boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

                    SharedPreferences preferencesIP = getSharedPreferences("IPPref", MODE_PRIVATE);
                    String getIP = preferencesIP.getString("savedIP", "");

                    if (isLoggedIn) {
                        // User is already logged in, proceed to CreateDeliveryReceipt activity
                        Intent intent = new Intent(SplashScreen.this, CreateDeliveryReceipt.class);
                        intent.putExtra("ipAddress", getIP);
                        startActivity(intent);
                        finish(); // Close the current activity to prevent going back to the login screen

                        if (getIP != null || !getIP.equals("")) {
                            Database database = new Database();
                            database.createConnection(getIP);
                        }

                    } else {

                        if (getIP != null || !getIP.equals("")) {
                            Database database = new Database();
                            database.createConnection(getIP);
                        }

                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("ipAddress", getIP);
                        startActivity(intent);
                        finish();

                    }

                }

//        etIpAddress = findViewById(R.id.et_ip);
//        btnSubmitIP = findViewById(R.id.btn_submit_ip);
//
//        ipAddress = etIpAddress.getText().toString();
//
//        btnSubmitIP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ipAddress = etIpAddress.getText().toString();
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
//                boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
//                if (isLoggedIn) {
//                    System.out.println("CREATE DELIVERY");
//                    // User is already logged in, proceed to CreateDeliveryReceipt activity
//                    Intent intent = new Intent(SplashScreen.this, CreateDeliveryReceipt.class);
//                    intent.putExtra("ipAddress",ipAddress);
//                    startActivity(intent);
//                    finish(); // Close the current activity to prevent going back to the login screen
//
//                    if (ipAddress != null || !ipAddress.equals("")) {
//                        Database database = new Database();
//                        database.createConnection(ipAddress);
//                    }
//                } else {
//                    System.out.println("MAIN ACTIVITY");
//                    System.out.println("VALUE OF ipAddress in login " + ipAddress);
//                    if (ipAddress != null || !ipAddress.equals("")) {
//                        Database database = new Database();
//                        database.createConnection(ipAddress);
//                    }
//                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//                    intent.putExtra("ipAddress",ipAddress);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
            }
        }, 5000);
    }
}