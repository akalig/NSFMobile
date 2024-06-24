package com.mycompany.nsfmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.nsfmobile.adapters.BookingData;
import com.mycompany.nsfmobile.adapters.BookingDataAdapter;
import com.mycompany.nsfmobile.adapters.ProductData;
import com.mycompany.nsfmobile.adapters.ProductDataAdapter;
import com.mycompany.nsfmobile.adapters.ProductSizeData;
import com.mycompany.nsfmobile.database.Database;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity implements ProductDataAdapter.OnItemClickListener {

    TextView tvBookingNo;
    TextView tvCustomerName;
    RecyclerView rvProductList;
    private Database database;
    private ProductDataAdapter productDataAdapter;

    String deliveryReceiptNo = "";
    String truckPlatNo = "";
    String agentName = "";
    String customerName = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        tvBookingNo = (TextView) findViewById(R.id.tv_booking_no);
        tvCustomerName = (TextView) findViewById(R.id.tv_customer_name);
        rvProductList = (RecyclerView) findViewById(R.id.rv_product_list);

        Intent intent = getIntent();
        String bookingNo = intent.getStringExtra("bookingNoSelected");
        customerName = intent.getStringExtra("customerName");
        deliveryReceiptNo = intent.getStringExtra("deliveryReceiptNoToProdList");
        truckPlatNo = intent.getStringExtra("truckPlatNoToProdList");
        agentName = intent.getStringExtra("agentNameToProdList");

        tvBookingNo.setText(bookingNo);
        tvCustomerName.setText(customerName);

        database = new Database();
        Intent intent2 = getIntent();
        String ipAddress = intent2.getStringExtra("ipAddress");
        database.createConnection(ipAddress);
        database.retrieveProductData(this);

    }

    public void updateProductData(ArrayList<ProductData> productDataList) {
        // Update the UI with the booking data
        // Example: display the booking numbers
//        for (BookingData bookingData : bookingDataList) {
//            String bookingNo = bookingData.getBookingNo();
//            String agentName = bookingData.getAgentName();
//            String customerName = bookingData.getCustomerName();
//            System.out.println("Booking No: " + bookingNo + " " + agentName + " " + customerName);
//        }

        productDataAdapter = new ProductDataAdapter(productDataList);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        productDataAdapter.setOnItemClickListener(this);
        rvProductList.setAdapter(productDataAdapter);
    }

    @Override
    public void onItemProductClick(ProductData productData) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.container_background));
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        Button standard = dialog.findViewById(R.id.btn_standard);
        Button catchWeight = dialog.findViewById(R.id.btn_catch_weight);

        standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option = "standard";

                Intent intent = new Intent(ProductList.this, CatchWeightComputation.class);
                intent.putExtra("productCw", productData.getDescription());
                intent.putExtra("bookingNoCw", productData.getBookingNo());
                intent.putExtra("productIdCw", productData.getProductId());
                intent.putExtra("priceCw", productData.getPrice());
                intent.putExtra("quantityCw", productData.getQuantity());
                intent.putExtra("sizeCw", productData.getSize());
                intent.putExtra("deliveryReceiptNoCw", deliveryReceiptNo);
                intent.putExtra("truckPlatNoCw", truckPlatNo);
                intent.putExtra("agentNameCw", agentName);
                intent.putExtra("customerNameCw", customerName);
                intent.putExtra("optionCw", option);

                Intent intent3 = getIntent();
                String ip = intent3.getStringExtra("ipAddress");
                intent.putExtra("ipAddress", ip);

                startActivity(intent);

                Toast.makeText(ProductList.this, ""+productData.getSize(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        catchWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option = "catchWeight";

                Intent intent = new Intent(ProductList.this, CatchWeightComputation.class);
                intent.putExtra("productCw", productData.getDescription());
                intent.putExtra("bookingNoCw", productData.getBookingNo());
                intent.putExtra("productIdCw", productData.getProductId());
                intent.putExtra("priceCw", productData.getPrice());
                intent.putExtra("quantityCw", productData.getQuantity());
                intent.putExtra("sizeCw", productData.getSize());
                intent.putExtra("deliveryReceiptNoCw", deliveryReceiptNo);
                intent.putExtra("truckPlatNoCw", truckPlatNo);
                intent.putExtra("agentNameCw", agentName);
                intent.putExtra("customerNameCw", customerName);
                intent.putExtra("optionCw", option);

                Intent intent3 = getIntent();
                String ip = intent3.getStringExtra("ipAddress");
                intent.putExtra("ipAddress", ip);

                startActivity(intent);

                Toast.makeText(ProductList.this, ""+productData.getSize(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        dialog.show();

    }
}