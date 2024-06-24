package com.mycompany.nsfmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.mycompany.nsfmobile.database.Database;

import java.util.ArrayList;

public class CreateDeliveryReceipt extends AppCompatActivity {

    Spinner sPCustomerName;
    Spinner sPTruckPlateNo;
    Spinner sPAgent;
    Button btnCreateDeliveryReceipt;
    Button btnNext;
    LinearLayoutCompat llCreateDeliveryReceiptLayout;
    LottieAnimationView loadingAnimation;

    Database database;
    ImageButton ipButton;
    EditText etSearchCustomer;
    String selectedCustomerName = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_delivery_receipt);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sPCustomerName = findViewById(R.id.sp_customer_name);
        sPTruckPlateNo = findViewById(R.id.sp_truck_plate_no);
        sPAgent = findViewById(R.id.sp_agent);
        btnCreateDeliveryReceipt = findViewById(R.id.btn_create_delivery_receipt);
        btnNext = findViewById(R.id.btn_next);
        loadingAnimation = findViewById(R.id.loading_animation_2);
        ipButton = findViewById(R.id.ip_button_cdr);
        loadingAnimation.setVisibility(View.GONE);
        llCreateDeliveryReceiptLayout = findViewById(R.id.create_delivery_receipt_layout);
        etSearchCustomer = findViewById(R.id.et_search_customer);

        ArrayAdapter<String> adapterCustomerName = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterCustomerName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPCustomerName.setAdapter(adapterCustomerName);

        ArrayAdapter<String> adapterTruckPlateNo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterTruckPlateNo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPTruckPlateNo.setAdapter(adapterTruckPlateNo);

        ArrayAdapter<String> adapterAgent = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterAgent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sPAgent.setAdapter(adapterAgent);

        // Initialize the arrayList before passing it to the fetchReceipt method
        ArrayList<String> arrayList = new ArrayList<>();

        database = new Database();
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ipAddress");
        System.out.println("Check CreateDeliveryReceipt ipAddress " + ipAddress);

        String username = intent.getStringExtra("loggedUser");
        System.out.println("Check CreateDeliveryReceipt loggedUser " + username);
        database.createConnection(ipAddress);
        database.fetchCustomerNames(this, adapterCustomerName, sPCustomerName);
        database.fetchTruckPlateNumbers(this, adapterTruckPlateNo, sPTruckPlateNo);
        database.fetchAgentNames(this, adapterAgent, sPAgent);
        database.fetchReceipt(this, arrayList);

        etSearchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method will be called every time the user types or deletes text
                String searchText = charSequence.toString().toLowerCase();

                // Filter the adapter based on the search text
                filterAdapter(adapterCustomerName, searchText);

                if (sPCustomerName.getSelectedItem() != null) {
                    selectedCustomerName = sPCustomerName.getSelectedItem().toString();
                }

                database.setSelectedCustomer(selectedCustomerName);

                // Fetch P.O. numbers based on the selected agent
                database.fetchAgentNames(CreateDeliveryReceipt.this, adapterAgent, sPAgent);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this implementation
            }
        });

        sPCustomerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                database.setSelectedCustomer(selectedItem);

                // Fetch P.O. numbers based on the selected agent
                database.fetchAgentNames(CreateDeliveryReceipt.this, adapterAgent, sPAgent);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        sPAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        sPTruckPlateNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Handle the selected item here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        btnCreateDeliveryReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llCreateDeliveryReceiptLayout.setVisibility(View.VISIBLE);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customer = "";
                String agent = "";
                String truckPlateNumber = "";

                Object objectCustomer = sPCustomerName.getSelectedItem();
                Object objectAgent = sPAgent.getSelectedItem();
                Object objectTruckPlateNumber = sPTruckPlateNo.getSelectedItem();

                if (objectTruckPlateNumber != null) {
                    truckPlateNumber = objectTruckPlateNumber.toString();
                }

                if (objectAgent != null) {
                    agent = objectAgent.toString();
                }

                if (objectCustomer != null) {
                    customer = objectCustomer.toString();
                }

                if (truckPlateNumber.equals("▼ Select Truck Plate No.")) {

                    TextView errorText = (TextView) sPTruckPlateNo.getSelectedView();
                    errorText.setError("Please select a valid Truck Plate Number.");
                    errorText.setTextColor(Color.RED);
                    sPTruckPlateNo.requestFocus();

                } else if (truckPlateNumber.equals("")) {

                    Toast.makeText(CreateDeliveryReceipt.this, "Connection problem, please check your server's IP address.", Toast.LENGTH_LONG).show();

                } else if (customer.equals("▼ Select Customer")) {

                    TextView errorText = (TextView) sPCustomerName.getSelectedView();
                    errorText.setError("Please select a valid Customer.");
                    errorText.setTextColor(Color.RED);
                    sPCustomerName.requestFocus();

                } else if (agent.equals("")) {

                    Toast.makeText(CreateDeliveryReceipt.this, "Connection problem, please check your server's IP address.", Toast.LENGTH_LONG).show();

                } else {

                    Intent intent2 = getIntent();
                    String ip = intent2.getStringExtra("ipAddress");

                    Intent intent = new Intent(CreateDeliveryReceipt.this, SendProducts.class);
                    intent.putExtra("customerName", customer);
                    intent.putExtra("agentName", agent);
                    intent.putExtra("truckPlatNo", truckPlateNumber);
                    intent.putExtra("ipAddress", ip);
                    intent.putExtra("loggedUser", username);
                    startActivity(intent);

                }
            }
        });

        ipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(CreateDeliveryReceipt.this);
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

                            Toast.makeText(CreateDeliveryReceipt.this, "Please provide IP Address", Toast.LENGTH_SHORT).show();
                            submittedIP.setError("Enter IP Address");
                            submittedIP.requestFocus();

                        } else {

                            SharedPreferences sharedPreferences = getSharedPreferences("IPPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("savedIP", ipAddress);
                            editor.apply();

                            String savedIP = sharedPreferences.getString("savedIP", ipAddress);

                            Intent intent = new Intent(CreateDeliveryReceipt.this, SplashScreen.class);
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

    private void filterAdapter(ArrayAdapter<String> adapter, String searchText) {
        adapter.getFilter().filter(searchText);
    }

    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();
    }

    private void showLogoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear the login status in SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateDeliveryReceipt.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("username", "");
                editor.apply();

                Intent intent2 = getIntent();
                String ip = intent2.getStringExtra("ipAddress");

                // Proceed to the MainActivity (login screen)
                Intent intent = new Intent(CreateDeliveryReceipt.this, MainActivity.class);
                intent.putExtra("ipAddress", ip);
                startActivity(intent);
                finish(); // Close the current activity to prevent going back to the logged-in state
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
