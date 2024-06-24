package com.mycompany.nsfmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.mycompany.nsfmobile.adapters.BookingData;
import com.mycompany.nsfmobile.adapters.BookingDataAdapter;
import com.mycompany.nsfmobile.database.Database;

import java.util.ArrayList;

public class CustomerList extends AppCompatActivity implements BookingDataAdapter.OnItemClickListener {

    RecyclerView rvCustomerList;
    String deliveryReceiptNo = "";
    String truckPlatNo = "";
    String agentName = "";
    private Database database;
    private BookingDataAdapter bookingDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        rvCustomerList = (RecyclerView) findViewById(R.id.rv_customer_list);

        database = new Database();
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ipAddress");
        System.out.println("Customer List Start: " + ipAddress);
        database.createConnection(ipAddress);
        database.retrieveBookingData(this);

        Intent intentGetData = getIntent();
        System.out.println("Delivery: " + intentGetData.getStringExtra("deliveryReceiptNo"));
        System.out.println("truckPlatNo: " + intentGetData.getStringExtra("truckPlatNo"));
        System.out.println("agentName: " + intentGetData.getStringExtra("agentName"));
        deliveryReceiptNo = intentGetData.getStringExtra("deliveryReceiptNo");
        truckPlatNo = intentGetData.getStringExtra("truckPlatNo");
        agentName = intentGetData.getStringExtra("agentName");

    }

    public void updateBookingData(ArrayList<BookingData> bookingDataList) {
        // Update the UI with the booking data
        // Example: display the booking numbers
//        for (BookingData bookingData : bookingDataList) {
//            String bookingNo = bookingData.getBookingNo();
//            String agentName = bookingData.getAgentName();
//            String customerName = bookingData.getCustomerName();
//            System.out.println("Booking No: " + bookingNo + " " + agentName + " " + customerName);
//        }

        bookingDataAdapter = new BookingDataAdapter(bookingDataList);

        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ipAddress");
        bookingDataAdapter.setIPAddress(ipAddress);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(this));
        bookingDataAdapter.setOnItemClickListener(this);
        rvCustomerList.setAdapter(bookingDataAdapter);
    }

    @Override
    public void onItemClick(BookingData bookingData) {
        // Handle item click here
        // For example, start another activity and pass the clicked item data
        Intent intent = new Intent(this, ProductList.class);

        Database database = new Database();
        String ipAddress = intent.getStringExtra("ipAddress");
        database.createConnection(ipAddress);
        String checker = database.getBooknoChecker();

        if (checker.equals(null) || checker.isEmpty()) {
            intent.putExtra("bookingNoSelected", bookingData.getBookingNo());
            intent.putExtra("customerName", bookingData.getCustomerName());
        }

//        intent.putExtra("bookingNoSelected", bookingData.getBookingNo());
//        intent.putExtra("customerName", bookingData.getCustomerName());

        intent.putExtra("deliveryReceiptNoToProdList", deliveryReceiptNo);
        intent.putExtra("truckPlatNoToProdList", truckPlatNo);
        intent.putExtra("agentNameToProdList", agentName);

        Intent intent2 = getIntent();
        String ip = intent2.getStringExtra("ipAddress");
        intent.putExtra("ipAddress",ip);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();
    }

    private void showLogoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to process another delivery?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent2 = getIntent();
                String ip = intent2.getStringExtra("ipAddress");

                // Proceed to the MainActivity (login screen)
                Intent intent = new Intent(CustomerList.this, CreateDeliveryReceipt.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("ipAddress",ip);
                startActivity(intent);
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